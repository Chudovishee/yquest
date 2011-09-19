/*
 * log.cpp
 *
 *  Created on: 15.09.2011
 *      Author: chudovishee
 */

#include <iostream>
#include <fstream>
#include <time.h>
#include "log.h"

Log* Log::theSingleInstance = NULL;
boost::mutex Log::instanceMutex;


Log* Log::Instance(){
		boost::mutex::scoped_lock scoped_lock(instanceMutex);

		if(theSingleInstance == NULL){
				theSingleInstance = new Log();
		}
		return theSingleInstance;
    }

Log::Log():
	std::ofstream(){

}
void Log::write(std::string msg){

	boost::mutex::scoped_lock scoped_lock(mutex);

	time_t rawtime;
	  struct tm * timeinfo;

	  time ( &rawtime );
	  timeinfo = localtime ( &rawtime );

	  std::string timeStr = asctime (timeinfo);
	  timeStr[timeStr.length() -1] = ' ';
	  *this << timeStr << msg << std::endl;
	  this->flush();
}
