/*
 * db.cpp
 *
 *  Created on: 15.09.2011
 *      Author: chudovishee
 */

#include "db.h"

DB* DB::theSingleInstance = NULL;

DB::DB():
	mongo::DBClientConnection(true){

}
