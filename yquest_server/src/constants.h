 
#ifndef CONSTANTS
#define CONSTANTS

#include <string>

//protocol
const int YQUEST_PROTOCOL		= 1;

//commands
const int noneCommand			= 0;
const int authCommand			= 1;
const int protocolCommand		= 2;

//errors
const int NOT_ERROR				= 0;
const int PROTOCOL_ERROR 		= 1;
const int SERVER_CONFIG_ERROR	= 5;
const int SERVER_DB_ERROR		= 6;

const int unauthorized_mail 	= 2;
const int unauthorized_name 	= 3;
const int unauthorized_password	= 4;

const int duplicate_mail		= 7;
const int login_fail			= 8;
const int google_login_fail		= 9;

const int auth_fail				= 10; //токен и ид не прошли проверку при выполнении комманды
const int auth_ok				= 12;

const int command_fail			= 11; //команда зафэйлилась или не была найдена

//user
const int google_auth = 1;
const int yquest_auth = 2;
const int auth_token_expiration = 60;//86400; //1день

#endif
