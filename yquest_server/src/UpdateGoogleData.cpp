/*
 * UpdateGoogleData.cpp
 *
 *  Created on: 19.09.2011
 *      Author: chudovishee
 */

#include "UpdateGoogleData.h"
#include "log.h"
#include "config.h"
UpdateGoogleData::UpdateGoogleData(boost::asio::io_service& io_service)
	:io_service(io_service){
	thread = NULL;
	std::cout << "construct" << boost::this_thread::get_id() << std::endl;
}

void UpdateGoogleData::request(const mongo::OID & user_id,CompletionHandler handler){
	std::cout << "request" << boost::this_thread::get_id() << std::endl;
	if( !thread){
		thread = new boost::thread(boost::bind(&UpdateGoogleData::doInBackground,this,
				user_id,handler));
	}

}

void UpdateGoogleData::doInBackground(const mongo::OID & user_id,CompletionHandler handler){
	std::cout << "do" << boost::this_thread::get_id() << std::endl;
	Log * log = Log::Instance();
	Config * config = Config::Instance();
	boost::this_thread::sleep(boost::posix_time::milliseconds(5000));
	log->write("Update user google info: " + user_id.toString());

	io_service.post(handler);

	delete this;
}

UpdateGoogleData::~UpdateGoogleData() {
	std::cout << "destruct" << boost::this_thread::get_id() << std::endl;
	thread->interrupt();
	delete thread;
}

