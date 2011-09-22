/*
 * AuthCommand.h
 *
 *  Created on: 22.09.2011
 *      Author: chudovishee
 */

#ifndef AUTHCOMMAND_H_
#define AUTHCOMMAND_H_

#include <boost/asio.hpp>
#include <boost/thread.hpp>
#include <mongo/client/dbclient.h>
extern "C" {
  #include "oauth.h"
}

/*
 * Комманда авторизации
 * Проверяет токен и обновляет гугло дату если нужно
 * Возвращает TRUE или FALSE в зависимости от успеха операции
 */

class AuthCommand{
public:
	typedef boost::function<void (bool ok)> CompletionHandler;

	AuthCommand(boost::asio::io_service& io_service);
	~AuthCommand();

	/*
	 * Не потоко-безапастно
	 */
	void exec(const std::string & auth_id,
			const std::string & auth_token,
			CompletionHandler handler);
protected:
	void doInBackground(std::string auth_id,
			std::string auth_token,
			CompletionHandler handler);
private:
	boost::thread * thread;
	boost::asio::io_service &io_service;


};

#endif /* AUTHCOMMAND_H_ */
