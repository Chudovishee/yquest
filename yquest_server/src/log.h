#ifndef LOG_H
#define LOG_H

#include <iostream>
#include <fstream>
#include <string>
#include <boost/thread.hpp>

class Log:public std::ofstream{
	static Log* theSingleInstance;
public:
	static Log* Instance();

	void write(std::string msg);

private:
        Log();
        boost::mutex mutex;
        static boost::mutex instanceMutex;

};
#endif
