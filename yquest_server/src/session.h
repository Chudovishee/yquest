/*
 * session.h
 *
 *  Created on: 16.09.2011
 *      Author: chudovishee
 */

#ifndef SESSION_H
#define SESSION_H

#include <string>
#include <boost/bind.hpp>
#include <boost/asio.hpp>
#include <boost/asio/ssl.hpp>

#include <mongo/client/dbclient.h>

#include "log.h"
#include "db.h"

typedef boost::asio::ssl::stream<boost::asio::ip::tcp::socket> ssl_socket;

class session {
public:
  session(boost::asio::io_service& io_service,
      boost::asio::ssl::context& context);
  ~session();

  ssl_socket::lowest_layer_type& socket();

  void start();

  void handle_handshake(const boost::system::error_code& error);

  void handle_read(const boost::system::error_code& error);

  void handle_write(const boost::system::error_code& error);

  void handle_google();

private:
  ssl_socket _socket;
  enum {
	  max_length = 10240 // 10KB
  };
  char data[max_length];

  void processCommand();

  Log * log;
  DB * db;
  boost::asio::streambuf request_buf;	//буфер запроса клиента
  boost::asio::streambuf response_buf;	//буфер ответа сервера
  std::istream request;
  std::ostream response;
  mongo::OID id;

  int lastCommand;
  bool deleteBeforeWrite;

};

#endif /* SESSION_H */
