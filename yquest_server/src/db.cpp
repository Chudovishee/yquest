/*
 * db.cpp
 *
 *  Created on: 15.09.2011
 *      Author: chudovishee
 */

#include "db.h"
#include "log.h"
#include "config.h"

DB* DB::theSingleInstance = NULL;

DB::DB(){
}
DB::~DB(){
	connection->done();
	delete connection;
	connection = NULL;
}
void DB::done(){
	connection->done();
}
void DB::kill(){
	connection->kill();
}

DB * DB::SingleInstance(){
	if(theSingleInstance == NULL)
		theSingleInstance = Instance();
	return theSingleInstance;
}

DB * DB::Instance(){

	Config * config = Config::Instance();
	Log * log = Log::Instance();
	DB * db = NULL;
	mongo::ScopedDbConnection * connection = NULL;

	try{
		connection = new mongo::ScopedDbConnection(config->MONGO_SERVER);
		db = (DB*) connection->get();
		db->connection = connection;

		std::string errmsg;

		if(db->auth(config->MONGO_DB,config->MONGO_USER,config->MONGO_PASSWORD,errmsg)){
			log->write("db opened");
		}else{
			log->write(errmsg);
			connection->done();
			delete connection;
			delete db;
			db = NULL;
			connection = NULL;
		}
	}catch(mongo::DBException e){
		log->write(e.what());
		if(connection){
			connection->done();
			delete connection;
			connection = NULL;
		}
		if(db){
			delete db;
			db = NULL;
		}
	}
	return db;
}
