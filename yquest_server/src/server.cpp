/*
 * server.cpp
 *
 *  Created on: 16.09.2011
 *      Author: chudovishee
 */

#include "server.h"
#include "config.h"


Server::Server(const std::string& host,unsigned short port):
	io_service(),
	context(io_service,boost::asio::ssl::context::sslv23),
	acceptor(io_service,
	          boost::asio::ip::tcp::endpoint(boost::asio::ip::address_v4::from_string(host), port)){

	Config * config = Config::Instance();

	context.set_options(
        boost::asio::ssl::context::default_workarounds
        | boost::asio::ssl::context::no_sslv2
        | boost::asio::ssl::context::single_dh_use);

	context.set_password_callback(boost::bind(&Server::get_password, this));
    context.use_certificate_chain_file(config->SERVER_PUBLIC_KEY);
    context.use_private_key_file(config->SERVER_PRIVATE_KEY, boost::asio::ssl::context::pem);
    context.use_tmp_dh_file(config->SERVER_DH);

    log = Log::Instance();


}

std::string Server::get_password() const{
	return "VojureD12";
}

Server::~Server() {
}

void Server::run(){
	log->write("server listen ");
	start_accept();
	io_service.run();
}

void Server::addSession(Session * session){
	std::string id_s = session->id().str();

	if(sessions.find(id_s) != sessions.end()){
		//пока старый :)
		log->write(id_s + " unstoraged");
		delete sessions[id_s];
	}
	log->write(id_s + " storaged");
	sessions[id_s] = session;
}

void Server::removeSession(Session * session){
	boost::unordered_map< std::string , Session *>::iterator find = sessions.find(session->id().str());
	if(find != sessions.end()){
		//пока-пока!
		sessions.erase(find);
	}
}

void Server::start_accept(){
   Session* new_session = new Session(this,io_service, context);
   acceptor.async_accept(new_session->socket(),
       boost::bind(&Server::handle_accept, this, new_session, boost::asio::placeholders::error));
 }

 void Server::handle_accept(Session* new_session,
     const boost::system::error_code& error){
   if (!error){
	   log->write("accept new connection");
     new_session->start();
   }
   else{
	   log->write("error accepting new connection: " + error.message());
	   delete new_session;
   }

   start_accept();
 }
