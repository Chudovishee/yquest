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

session::session(boost::asio::io_service& io_service,
      boost::asio::ssl::context& context)
    : _socket(io_service, context),
      request_buf(max_length),
      response_buf(max_length),
      request(&request_buf),
      response(&response_buf){

	lastCommand = noneCommand;
	deleteBeforeWrite = false;
	log = Log::Instance();
	log->write("new session");

	db = DB::Instance();

	id.clear();

  }

session::~session() {
	// TODO Auto-generated destructor stub
}


ssl_socket::lowest_layer_type& session::socket(){
  return _socket.lowest_layer();
}

void session::start(){

	//std::cout << "start " << boost::this_thread::get_id() << std::endl;

   _socket.async_handshake(boost::asio::ssl::stream_base::server,
       boost::bind(&session::handle_handshake, this,
         boost::asio::placeholders::error));
   log->write("start new session");

	  UpdateGoogleData *g = new UpdateGoogleData(_socket.io_service());
	  g->request(mongo::OID("4e70cd11aa8f692628000000"),
			  boost::bind(&session::handle_google, this));

}

void session::handle_handshake(const boost::system::error_code& error){

	//std::cout << "handle_handshake " << boost::this_thread::get_id() << std::endl;

  if (!error){
	  log->write("handle_handshake");
	  boost::asio::async_read_until(_socket, request_buf, "\n",
	           boost::bind(&session::handle_read, this,
	             boost::asio::placeholders::error));



  }
  else
  {
	log->write(error.message());
    delete this;
  }
}

void session::handle_google(){
	std::cout << "handle_google " << boost::this_thread::get_id() << std::endl;

}


void session::handle_read(const boost::system::error_code& error){
	//std::cout << "handle_read " << boost::this_thread::get_id() << std::endl;

  if (!error)
  {
	  lastCommand = 0;
	  log->write("handle_read");
	  //std::string str;
	  request >> lastCommand;
	  //std::getline(request,str);

	  //std::cout << " str("<<str<< ")" <<std::endl;
	  //boost::asio::async_read_until(_socket, request_buf, "\r\n",
	  //         boost::bind(&session::handle_read, this,
	  //           boost::asio::placeholders::error));
	  processCommand();

	  //ignore \n
	  request.ignore();
  }
  else{
	  log->write(error.message());
	  delete this;
  }
}

void session::handle_write(const boost::system::error_code& error){

	//std::cout << "handle_write " << boost::this_thread::get_id() << std::endl;

	if(error){
		log->write(error.message());
		delete this;
	}else if (deleteBeforeWrite){
		delete this;
	}else{
	  log->write("handle_write");
	  boost::asio::async_read_until(_socket, request_buf, "\n",
	           boost::bind(&session::handle_read, this,
	             boost::asio::placeholders::error));
	}

}


void session::processCommand(){
	if(id.isSet()){

	}
	else{
		//only protocol and auth commands
		int protocol = 0;
		std::string auth_id,
			auth_token;
		mongo::BSONObj user;
		mongo::OID oid;
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
				response << protocolCommand << NOT_ERROR ;
			}
			else{
				log->write("Protocol error");
				deleteBeforeWrite = true;
				response << protocolCommand << PROTOCOL_ERROR ;
			}
			break;
		/*
		 * Проверка токена, авторизация
		 * Синтаксис:
		 * authCommand auth_id auth_token
		 */
		case authCommand:
			request >> auth_id >> auth_token;
			oid.init(auth_id);
			user = db->findOne("yquest.users", QUERY("_id" << oid));
			std::cout << user["auth_token"].String() << std::endl;
			break;

		default:
			log->write("unresolved command");
			deleteBeforeWrite = true;
			response << protocolCommand << PROTOCOL_ERROR ;
			break;
		}
		//write
		boost::asio::async_write(_socket, response_buf,
				boost::bind(&session::handle_write, this,
						boost::asio::placeholders::error));
	}
}
