/*
 * db.h
 *
 *  Created on: 15.09.2011
 *      Author: chudovishee
 */

#include <mongo/client/dbclient.h>

#ifndef DB_H_
#define DB_H_

class DB : public mongo::DBClientConnection {
public:
	static DB* Instance(){

		if(theSingleInstance == NULL)
				theSingleInstance = new DB();
		return theSingleInstance;
    }
private:
	DB();
	static DB* theSingleInstance;
};

#endif /* DB_H_ */
