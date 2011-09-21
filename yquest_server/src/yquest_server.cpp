#include <iostream>
#include <mongo/client/dbclient.h>
#include <assert.h>
#include <boost/property_tree/ini_parser.hpp>

#include "config.h"
#include "log.h"
#include "db.h"
#include "server.h"
#include "UpdateGoogleData.h"

boost::property_tree::ptree config_file;

int main(int argc, const char* argv[] ) {


	if(argc != 2){
		std::cout << "Use: " << argv[0] << " <config file>" << std::endl;
		return 1;
	}
	//UpdateGoogleData g;
	//g.request(mongo::OID("4e70cd11aa8f692628000000"));


	//loading configs
	try{
		boost::property_tree::read_ini(argv[1],config_file);

		std::cout << "loading config..." << std::endl;

		Config * config = Config::Instance();
		Log * log = Log::Instance();

		config->load(config_file);

		std::cout << "config loaded" << std::endl;

		log->exceptions ( std::ofstream::failbit | std::ofstream::badbit );
		//open log
		log->open( config->SERVER_LOG.c_str() , std::ios_base::out | std::ios_base::app);


		log->write("\n\nstarting server");

		DB * db = DB::SingleInstance();
		if(db){

			Server server(config->SERVER_HOST, config->SERVER_PORT);
			log->write("creat new server");
			server.run();

		}

	}
	//config parse errors
	catch (boost::property_tree::ini_parser_error& error){
		std::cout
			<< error.message() << ": "
			<< error.filename() << ", line "
			<< error.line() << std::endl;
	}catch (const boost::property_tree::ptree_bad_data& error){
		std::cout << "Bad config data: " << error.what() << std::endl;
	}
	catch (const boost::property_tree::ptree_bad_path& error){
		std::cout << "Bad config path: " << error.what() << std::endl;
	}
	//logs errors
	catch (std::ofstream::failure e) {
	    std::cout << "Error opening log file: " << e.what() << std::endl;
	} catch (std::exception& e){
	    std::cout << "Exception: " << e.what() << "\n";
  }
}
