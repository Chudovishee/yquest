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

typedef boost::asio::ssl::stream<boost::asio::ip::tcp::socket> ssl_socket;
class Server;
class Log;
class DB;

class Session {
public:
  Session(Server * server,
	  boost::asio::io_service& io_service,
      boost::asio::ssl::context& context);
  ~Session();

  ssl_socket::lowest_layer_type& socket();

  void start();

  void handle_handshake(const boost::system::error_code& error);

  void handle_read(const boost::system::error_code& error);

  void handle_write(const boost::system::error_code& error);

  void handle_google(bool ok);

  const  mongo::OID & id();
private:
  ssl_socket _socket;
  enum {
	  max_length = 10240 // 10KB
  };
  char data[max_length];

  inline void processCommand();


  Log * log;
  DB * db;
  boost::asio::streambuf request_buf;	//буфер запроса клиента
  boost::asio::streambuf response_buf;	//буфер ответа сервера
  std::istream request;
  std::ostream response;
  mongo::OID _id;

  int lastCommand;
  bool deleteBeforeWrite;
  bool deleting; // true ставит деструктор, значит ненадо его заново вызывать когда заваливается сокет
  Server * server;

};

#endif /* SESSION_H */
