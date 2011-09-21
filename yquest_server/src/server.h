/*
 * server.h
 *
 *  Created on: 16.09.2011
 *      Author: chudovishee
 */

#ifndef SERVER_H
#define SERVER_H

#include <string>
#include <boost/bind.hpp>
#include <boost/asio.hpp>
#include <boost/asio/ssl.hpp>
#include <boost/unordered_map.hpp>

#include <mongo/client/dbclient.h>

#include "session.h"
#include "log.h"

class Server {
public:
	Server(const std::string& host,unsigned short port);
	virtual ~Server();

	void start_accept();
	void handle_accept(Session* new_session,
	     const boost::system::error_code& error);

	void run();

	void addSession(Session* session);
	void removeSession(Session* session);


private:
	boost::asio::io_service io_service;
	boost::asio::ip::tcp::acceptor acceptor;
	boost::asio::ssl::context context;

	std::string get_password() const;
	Log *log;

	//
	boost::unordered_map< std::string , Session *> sessions;
};

#endif /* SERVER_H */
