#ifndef CONFIG_H
#define CONFIG_H

#include <string>
#include <assert.h>
#include <boost/property_tree/ini_parser.hpp>
#include <boost/thread.hpp>


class Config{
	static Config* theSingleInstance;
public:
	static Config* Instance();

	void load(boost::property_tree::ptree config);

	std::string MONGO_SERVER;
	std::string MONGO_USER;
	std::string MONGO_PASSWORD;
	std::string MONGO_DB;

	std::string SERVER_HOST;
	unsigned short SERVER_PORT;

	std::string SERVER_PRIVATE_KEY;
	std::string SERVER_PUBLIC_KEY;
	std::string SERVER_DH;
	std::string SERVER_LOG;

	//oauth
	std::string OAUTH_KEY;
	std::string OAUTH_SECRET;
	std::string OAUTH_SCOPE;
	std::string OAUTH_REQUEST_URL;
	std::string OAUTH_PROFILE_REQUEST_URL;
	std::string OAUTH_MAIL_REQUEST_URL;
	std::string OAUTH_ACCESS_URL;
	std::string OAUTH_AUTHORIZE_URL;
private:
        Config();
        static boost::mutex instanceMutex;

};



#endif
