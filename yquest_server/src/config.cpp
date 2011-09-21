#include "config.h"

Config* Config::theSingleInstance = NULL;
boost::mutex Config::instanceMutex;

Config::Config(){
	MONGO_SERVER	= "localhost";
	MONGO_USER		= "user";
	MONGO_PASSWORD	= "";
	MONGO_DB		= "user";

	SERVER_HOST		= "localhost";
	SERVER_PORT		= 6969;

	SERVER_PRIVATE_KEY	= "";
	SERVER_PUBLIC_KEY	= "";
	SERVER_DH			= "";
	SERVER_LOG			= "";

	OAUTH_KEY			= "";
	OAUTH_SECRET		= "";
	OAUTH_SCOPE			= "";
	OAUTH_REQUEST_URL	= "";
	OAUTH_PROFILE_REQUEST_URL		= "";
	OAUTH_MAIL_REQUEST_URL			= "";
	OAUTH_ACCESS_URL	= "";
	OAUTH_AUTHORIZE_URL	= "";
}

Config* Config::Instance(){
	boost::mutex::scoped_lock scoped_lock(instanceMutex);

	if(theSingleInstance == NULL)
			theSingleInstance = new Config();
	return theSingleInstance;
}

void Config::load(boost::property_tree::ptree config){
	//global
	SERVER_HOST = config.get<std::string>("global.host");
	SERVER_PORT = config.get<unsigned short>("global.port");
	SERVER_LOG 	= config.get<std::string>("global.log");
	//db
	MONGO_SERVER 	= config.get<std::string>("db.server");
	MONGO_DB		= config.get<std::string>("db.db");
	MONGO_USER		= config.get<std::string>("db.user");
	MONGO_PASSWORD	= config.get<std::string>("db.password");
	//ssl
	SERVER_PRIVATE_KEY	= config.get<std::string>("ssl.private_key");
	SERVER_PUBLIC_KEY	= config.get<std::string>("ssl.public_key");
	SERVER_DH			= config.get<std::string>("ssl.dh_key");
	//oauth
	OAUTH_KEY					= config.get<std::string>("oauth.key");
	OAUTH_SECRET				= config.get<std::string>("oauth.secret");
	OAUTH_SCOPE					= config.get<std::string>("oauth.scope");
	OAUTH_REQUEST_URL			= config.get<std::string>("oauth.request_url");
	OAUTH_PROFILE_REQUEST_URL	= config.get<std::string>("oauth.profile_request_url");
	OAUTH_MAIL_REQUEST_URL		= config.get<std::string>("oauth.mail_request_url");
	OAUTH_ACCESS_URL			= config.get<std::string>("oauth.access_url");
	OAUTH_AUTHORIZE_URL			= config.get<std::string>("oauth.authorize_url");
}
