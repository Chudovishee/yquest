package com.yquest;


public class Constants {

	public static final Integer google_auth = 1;
	public static final Integer yquest_auth = 2;
	
	public static final String GOOGLE_CONSUMER_KEY 	= "anonymous";
	public static final String GOOGLE_CONSUMER_SECRET 	= "anonymous";

	public static final String SCOPE 			= "https://www-opensocial.googleusercontent.com/api/people/ https://www.googleapis.com/auth/userinfo#email";
	public static final String GOOGLE_REQUEST_URL 		= "https://www.google.com/accounts/OAuthGetRequestToken";
	public static final String GOOGLE_ACCESS_URL 		= "https://www.google.com/accounts/OAuthGetAccessToken";  
	public static final String GOOGLE_AUTHORIZE_URL 	= "https://www.google.com/accounts/OAuthAuthorizeToken";
	
	public static final String GOOGLE_API_REQUEST 		= "https://www.google.com/m8/feeds/contacts/default/full?alt=json";
	
	public static final String ENCODING 		= "UTF-8";
	
	public static final String	OAUTH_CALLBACK_SCHEME	= "x-oauthflow";
	public static final String	OAUTH_CALLBACK_HOST		= "googlelogin";
	public static final String	OAUTH_CALLBACK_URL		= OAUTH_CALLBACK_SCHEME + "://" + OAUTH_CALLBACK_HOST;

	public static final String BASE_URL					= "http://192.168.2.9/yquest/";
	
	public static final String YQUEST_REG_URL				= BASE_URL + "reg.php";
	public static final String GOOGLE_LOGIN_URL			= BASE_URL + "google_login.php";
	public static final String YQUEST_LOGIN_URL			= BASE_URL + "login.php";
	//public static final String YQUEST_CHECK_TOKEN_URL		= BASE_URL + "check_ytoken.php";
	public static final String EXEC_URL					= BASE_URL + "exec.php";
	
	public static final Integer PASSWORD_MIN_LEN	= 4;
	
	
	
	//protocol
	
	public static final int noneCommand			= 0;
	public static final int authCommand			= 1;
	public static final int protocolCommand		= 2;
	
	public static final Integer YQUEST_PROTOCOL	= 1;
	
	public static final int PROTOCOL_ERROR 		= 1;
	public static final int SERVER_CONFIG_ERROR	= 5;
	public static final int SERVER_DB_ERROR		= 6;

	public static final int unauthorized_mail 	= 2; 
	public static final int unauthorized_name 	= 3;
	public static final int unauthorized_password	= 4;

	public static final int duplicate_mail		= 7;
	public static final int login_fail			= 8;
	public static final int google_login_fail		= 9;

	public static final int auth_fail				= 10; //токен и ид не прошли проверку при выполнении комманды

	public static final int command_fail			= 11; //команда зафэйлилась или не была найдена
}
