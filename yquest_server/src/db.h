/*
 * db.h
 *
 *  Created on: 15.09.2011
 *      Author: chudovishee
 */

#include <mongo/client/dbclient.h>
#include <mongo/client/connpool.h>

#ifndef DB_H_
#define DB_H_

class DB : public mongo::DBClientBase {
public:
	static DB* Instance();
	static DB* SingleInstance();

	void done();
	void kill();
	~DB();
private:
	DB();
	mongo::ScopedDbConnection * connection;
	static DB* theSingleInstance;
};

#endif /* DB_H_ */
