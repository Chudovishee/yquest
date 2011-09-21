/*
 * session.cpp
 *
 *  Created on: 16.09.2011
 *      Author: chudovishee
 */

#include "session.h"
#include "constants.h"
#include <time.h>

#include "UpdateGoogleData.h"
#include "log.h"
#include "db.h"
#include "server.h"

Session::Session(Server * server,
		boost::asio::io_service& io_service,
		boost::asio::ssl::context& context)
    : _socket(io_service, context),
      request_buf(max_length),
      response_buf(max_length),
      request(&request_buf),
      response(&response_buf),
      server(server){

	lastCommand = noneCommand;
	deleteBeforeWrite = false;
	log = Log::Instance();
	log->write("new session");

	db = DB::SingleInstance();

	_id.clear();
	deleting = false;

  }

Session::~Session() {
	log->write("session delete");
	deleting = true;
	//если у нас валидный идшник, скажем серверу чтом ы умерли,
	//иначе сервер даже незнает о нас и ему насрать :)
	if(_id.isSet()){
		server->removeSession(this);
	}
}


const mongo::OID & Session::id(){
	return _id;
}

ssl_socket::lowest_layer_type& Session::socket(){
  return _socket.lowest_layer();
}

void Session::start(){

   _socket.async_handshake(boost::asio::ssl::stream_base::server,
       boost::bind(&Session::handle_handshake, this,
         boost::asio::placeholders::error));
   log->write("start new session");


}

void Session::handle_handshake(const boost::system::error_code& error){

	log->write( "handle_handshake ");

  if (!error){
	  boost::asio::async_read_until(_socket, request_buf, "\n",
	           boost::bind(&Session::handle_read, this,
	             boost::asio::placeholders::error));



  }
  else{
	log->write(error.message());
    if(!deleting)
    	delete this;
  }
}

void Session::handle_google(bool ok){
	log->write("handle_google ");
}


void Session::handle_read(const boost::system::error_code& error){
	log->write( "handle_read " );

  if (!error)
  {
	  lastCommand = 0;
	  request >> lastCommand;
	  processCommand();

	  //ignore \n
	  request.ignore();
  }
  else{
	  log->write(error.message());
	if(!deleting)
		delete this;
  }
}

void Session::handle_write(const boost::system::error_code& error){

	log->write( "handle_write " );

	if(error){
		log->write(error.message());
	    if(!deleting)
	    	delete this;
	}else if (deleteBeforeWrite){
	    if(!deleting)
	    	delete this;
	}else{
	//  log->write("handle_write");
	  boost::asio::async_read_until(_socket, request_buf, "\n",
	           boost::bind(&Session::handle_read, this,
	             boost::asio::placeholders::error));
	}

}


inline void Session::processCommand(){
	boost::this_thread::sleep(boost::posix_time::seconds(5));
	if(_id.isSet()){

	}
	else{
		//only protocol and auth commands
		int protocol = 0;
		std::string auth_id,
			auth_token;
		mongo::BSONObj user;
		mongo::OID oid;
		UpdateGoogleData *g;

		switch(lastCommand){
		/*
		 * Проверка протокола
		 * Синтаксис:
		 * protocolCommand protocol
		 * Ответ:
		 * protocolCommand NOT_ERROR | PROTOCOL_ERROR
		 */
		case protocolCommand:

			request >> protocol;
			//std::cout << protocol << std::endl;
			if(protocol == YQUEST_PROTOCOL){
				log->write("Protocol OK");
				response << protocolCommand << ' ' << NOT_ERROR << '\n';
			}
			else{
				log->write("Protocol error");
				deleteBeforeWrite = true;
				response << protocolCommand << ' ' << PROTOCOL_ERROR << '\n';
			}
			break;
		/*
		 * Проверка токена, авторизация
		 * Синтаксис:
		 * authCommand auth_id auth_token
		 * Ответ:
		 * protocolCommand PROTOCOL_ERROR - если где-то ошбика в сообщении
		 * authCommand auth_fail - токен не подошел
		 * authCommand auth_ok   - все ok
		 */
		case authCommand:
			request >> auth_id >> auth_token;

			if(auth_id.size() == 24){
				oid.init(auth_id);
				user = db->findOne("yquest.users", QUERY("_id" << oid));

				//std::cout << user["auth_token"].String()<< std::endl;
				//std::cout << auth_token << std::endl;

				if(!user.isEmpty() &&
						(user["auth_token"].type() == mongo::String) &&
						(user["auth_token"].String() == auth_token) ){
					log->write("auth ok");
					//скажем серверу привет!
					server->addSession(this);
					response << authCommand << ' ' << auth_ok << '\n';
				}else{
					log->write("auth fail");
					deleteBeforeWrite = true;
					response << authCommand << ' ' << auth_fail << '\n';
				}
			}
			else{
				deleteBeforeWrite = true;
				response << protocolCommand << ' ' << PROTOCOL_ERROR << '\n';
			}
			break;

		default:
			log->write("unresolved command");
			deleteBeforeWrite = true;
			response << protocolCommand << ' ' << PROTOCOL_ERROR << '\n';
			break;
		}
		//write
		boost::asio::async_write(_socket, response_buf,
				boost::bind(&Session::handle_write, this,
						boost::asio::placeholders::error));
	}
}
