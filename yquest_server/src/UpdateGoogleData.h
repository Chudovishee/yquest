/*
 * UpdateGoogleData.h
 *
 *  Created on: 19.09.2011
 *      Author: chudovishee
 */

#ifndef UPDATEGOOGLEDATA_H
#define UPDATEGOOGLEDATA_H

#include <boost/asio.hpp>
#include <boost/thread.hpp>
#include <mongo/client/dbclient.h>
extern "C" {
  #include "oauth.h"
}
class UpdateGoogleData {
public:
	typedef boost::function<void (bool ok)> CompletionHandler;

	UpdateGoogleData(boost::asio::io_service& io_service);
	virtual ~UpdateGoogleData();

	void request(const mongo::OID & user_id,CompletionHandler handler);


private:
	boost::thread * thread;
	void doInBackground(const mongo::OID & user_id,CompletionHandler handler);

	boost::asio::io_service &io_service;
};

#endif /* UPDATEGOOGLEDATA_H */
