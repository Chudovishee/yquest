/*
 * AsyncCommand.h
 *
 *  Created on: 22.09.2011
 *      Author: chudovishee
 */

#ifndef ASYNCCOMMAND_H
#define ASYNCCOMMAND_H

#include <boost/asio.hpp>
#include <boost/thread.hpp>

template<typename CompletionHandlerSignature>
class AsyncCommand {
public:

	typedef boost::function<CompletionHandlerSignature> CompletionHandler;

	AsyncCommand(boost::asio::io_service& io_service);
	virtual ~AsyncCommand();

	void exec(CompletionHandler handler);
protected:
	virtual void doInBackground(CompletionHandler handler);
	boost::thread * thread;

private:
	boost::asio::io_service &io_service;
};

#endif /* ASYNCCOMMAND_H*/
