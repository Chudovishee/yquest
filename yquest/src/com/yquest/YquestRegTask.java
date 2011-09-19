package com.yquest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;


import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;



public class YquestRegTask extends AsyncTask<Void, Void, Void> {

	final String TAG = getClass().getName();
	
	private Context	 context;
	private List<NameValuePair> nameValuePairs;
	public YquestRegTask(Context context, List<NameValuePair> nameValuePairs) {
		this.context = context;
		this.nameValuePairs = nameValuePairs;
	}


	@Override
	protected Void doInBackground(Void...params) {
		
		DefaultHttpClient httpclient = new DefaultHttpClient();
    	HttpPost request = new HttpPost(Constants.YQUEST_REG_URL);
    	
    	Log.i(TAG,"Requesting reg URL : " + Constants.YQUEST_REG_URL);
    	
    	try {
    		// Add your data
            request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(request);
            
        } catch (ClientProtocolException e) {
        	Log.i(TAG, "ClientProtocolException: " + e.getMessage());
        } catch (IOException e) {
        	Log.i(TAG, "IOException: " + e.getMessage());
        }
		return null;
	}
}
