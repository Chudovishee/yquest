/*
 * UpdateGoogleData.cpp
 *
 *  Created on: 19.09.2011
 *      Author: chudovishee
 */

#include <sstream>
#include <boost/property_tree/json_parser.hpp>

#include "UpdateGoogleData.h"
#include "log.h"
#include "config.h"
#include "db.h"

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

	log->write("Update user google info: " + user_id.toString());

	bool ok = false;
	char *req_url = NULL;
	char *reply   = NULL;
	DB * db = DB::Instance();
	std::string mail,
		name,
		profile,
		thumbnail;

	if(db){
		mongo::BSONObj user = db->findOne("yquest.users", QUERY("_id" << user_id));
		if(user.valid()){
		req_url = oauth_sign_url2(config->OAUTH_PROFILE_REQUEST_URL.c_str(),
	    		NULL,
	    		OA_HMAC,
	    		NULL,
	    		config->OAUTH_KEY.c_str(),
	    		config->OAUTH_SECRET.c_str(),
	    		user["oauth_token"].String().c_str(),
	    		user["oauth_token_secret"].String().c_str());
	    reply = oauth_http_get(req_url,NULL);
	    log->write(reply);

	     std::stringstream stream(reply);
	     boost::property_tree::ptree ptree;
	      boost::property_tree::json_parser::read_json(stream, ptree);
	      std::cout << ptree.get<std::string>("entry.displayName")<< std::endl;
		}
	}
	io_service.post(boost::bind(handler,ok));

	delete this;
}

UpdateGoogleData::~UpdateGoogleData() {
	std::cout << "destruct" << boost::this_thread::get_id() << std::endl;
	thread->interrupt();
	delete thread;
}

