/*
 * AsyncCommand.cpp
 *
 *  Created on: 22.09.2011
 *      Author: chudovishee
 */

#include "AsyncCommand.h"

template<typename CompletionHandlerSignature>
AsyncCommand<CompletionHandlerSignature>::AsyncCommand(boost::asio::io_service& io_service)
	:io_service(io_service){

	thread = NULL;
}

template<typename CompletionHandlerSignature>
AsyncCommand<CompletionHandlerSignature>::~AsyncCommand() {
	if(thread){
		thread->interrupt();
		delete thread;
	}
}

template<typename CompletionHandlerSignature>
void AsyncCommand<CompletionHandlerSignature>::exec(CompletionHandler handler){
	if( !thread){
		thread = new boost::thread(
			boost::bind(&AsyncCommand<CompletionHandlerSignature>::doInBackground,this,
				handler));
	}
}
/*
 * and self destruct
 */
template<typename CompletionHandlerSignature>
void AsyncCommand<CompletionHandlerSignature>::doInBackground(CompletionHandler handler){
	io_service.post(handler);
	delete this;
}
