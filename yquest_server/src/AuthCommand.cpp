/*
 * AuthCommand.cpp
 *
 *  Created on: 22.09.2011
 *      Author: chudovishee
 */

#include "AuthCommand.h"
#include "log.h"
#include "db.h"
#include "config.h"
#include "constants.h"
#include <time.h>
#include <sstream>
#include <boost/property_tree/json_parser.hpp>

AuthCommand::AuthCommand(boost::asio::io_service& io_service)
	:io_service(io_service){
	thread = NULL;
}

AuthCommand::~AuthCommand() {
	if(thread){
		thread->interrupt();
		delete thread;
	}
}

void AuthCommand::exec(const std::string & auth_id,
		const std::string & auth_token,
		CompletionHandler handler){
	if( !thread ){
		thread = new boost::thread(
			boost::bind(&AuthCommand::doInBackground,this,
				auth_id,auth_token,handler));
	}
}

void AuthCommand::doInBackground(std::string auth_id,
		std::string auth_token,
		CompletionHandler handler){

	Log * log = Log::Instance();
	Config * config = Config::Instance();
	DB * db = DB::Instance();

	bool ok = false;
	char *requestProfile = NULL;
	char *replyProfile   = NULL;
	char *requestMail = NULL;
	char *replyMail   = NULL;
	std::string mail,
		name,
		profile,
		thumbnail;

	mongo::OID oid;
	std::stringstream replyStream;
	boost::property_tree::ptree profileTree;
	time_t t = time(NULL);

	//chrcking input
	if(db && (auth_id.size() == 24)){
		oid.init(auth_id);

		//db query
		mongo::BSONObj user = db->findOne("yquest.users", QUERY("_id" << oid));

		//checking auth
		if(!user.isEmpty() &&
			(user["auth_token"].String() == auth_token) ){

			//check auth_token_expiration if google login
			if( (user["auth_type"].Int() ==  google_auth) &&
					(user["auth_timestamp"].Int() + auth_token_expiration < t ) ){

				//make requests
				requestProfile = oauth_sign_url2(config->OAUTH_PROFILE_REQUEST_URL.c_str(),
						NULL,
						OA_HMAC,
						NULL,
						config->OAUTH_KEY.c_str(),
						config->OAUTH_SECRET.c_str(),
						user["oauth_token"].String().c_str(),
						user["oauth_token_secret"].String().c_str());
				requestMail = oauth_sign_url2(config->OAUTH_MAIL_REQUEST_URL.c_str(),
						NULL,
						OA_HMAC,
						NULL,
						config->OAUTH_KEY.c_str(),
						config->OAUTH_SECRET.c_str(),
						user["oauth_token"].String().c_str(),
						user["oauth_token_secret"].String().c_str());

				//replies
				replyProfile = oauth_http_get(requestProfile,NULL);
				replyMail = oauth_http_get(requestMail,NULL);

				if(replyProfile && replyMail){

					replyStream.str(replyProfile);

					try{
						boost::property_tree::json_parser::read_json(replyStream, profileTree);

						name			= profileTree.get<std::string>("entry.displayName");
						profile 		= profileTree.get<std::string>("entry.profileUrl");
						try{
							thumbnail 	= profileTree.get<std::string>("entry.thumbnailUrl");
						}catch (const boost::property_tree::ptree_bad_path& error){}
					}
					catch (const boost::property_tree::ptree_bad_data& error){}
					catch (const boost::property_tree::ptree_bad_path& error){}

					mail = replyMail;
				}
				//free
				if(requestProfile){ free(requestProfile); requestProfile = NULL;}
				if(requestMail){ free(requestMail); requestMail = NULL;}
				if(replyProfile){ free(replyProfile); replyProfile = NULL;}
				if(replyMail){ free(replyMail); replyMail = NULL;}

				size_t found,found2;
				found = mail.find("email=");
				if (found != string::npos){
					found2 = mail.find("&",found+1);
					if(found2 != string::npos){
						mail = mail.substr(found+6,found2-found-6);
					}else{
						mail = mail.substr(found+6);
					}
				}
				std::cout << mail << ' ' <<user["mail"].String() << std::endl;
				if( (mail == user["mail"].String()) &&
						!name.empty() &&
						!profile.empty() ){
					if(thumbnail.empty()){
						db->update( "yquest.users",
						   BSON( "_id" << oid ),
						   BSON( "$set" << BSON(
								   "auth_timestamp" << (unsigned int)t <<
								   "name"			<< name <<
								   "profile"		<< profile <<
								   "thumbnail"		<< false) ) );
					}else{
						db->update( "yquest.users",
						   BSON( "_id" << oid ),
						   BSON( "$set" << BSON(
								   "auth_timestamp" << (unsigned int)t <<
								   "name"			<< name <<
								   "profile"		<< profile <<
								   "thumbnail"		<< thumbnail) ) );
					}
					ok = true;
				}else{
					ok = false;
				}
			}
			//end google check
			//set ok = true else
			else{
				ok = true;
			}
		}
	}
	io_service.post(boost::bind(handler,ok));
	delete this;
}

