package com.yquest;

import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;


import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.util.Log;


public class GoogleLoginActivity extends Activity {
	
	final String TAG = getClass().getName();
	
	//private  SharedPreferences settings;// = PreferenceManager.getDefaultSharedPreferences(this);
	
    private OAuthConsumer consumer; 
    private OAuthProvider provider;
	private static final int DIALOG_LOAD_KEY = 1;
	private static final int DIALOG_CONNECTION_ERROR_KEY = 2;
	private static final int DIALOG_LOGIN_FAIL_KEY = 3;
	
	private static final int DIALOG_GOOGLE_AUTH_LOAD_KEY = 5;
	private  SharedPreferences settings;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.googlelogin);
        
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        
        try{
    		consumer = new CommonsHttpOAuthConsumer(Constants.GOOGLE_CONSUMER_KEY, Constants.GOOGLE_CONSUMER_SECRET);
    		provider = new CommonsHttpOAuthProvider(
	    		Constants.GOOGLE_REQUEST_URL  + "?scope=" + URLEncoder.encode(Constants.SCOPE, Constants.ENCODING),
	    		Constants.GOOGLE_ACCESS_URL,
	    		Constants.GOOGLE_AUTHORIZE_URL);
    		Log.i(TAG, "Starting task to retrieve request token.");
    		
        	new OAuthRequestTokenTask(this,consumer,provider).execute();
    		showDialog(DIALOG_GOOGLE_AUTH_LOAD_KEY);
    	
    	} catch (Exception e) {
    		Log.e(TAG, "Error creating consumer / provider",e);
    		showDialog(DIALOG_CONNECTION_ERROR_KEY);
    	}
        
    }
  
    protected void onPause(){
    	try{
    		dismissDialog(DIALOG_GOOGLE_AUTH_LOAD_KEY);
    	}catch(IllegalArgumentException e){}
    	super.onPause();
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
    	AlertDialog.Builder builder;
        switch (id) {
        	case DIALOG_GOOGLE_AUTH_LOAD_KEY:
            case DIALOG_LOAD_KEY: 
            	ProgressDialog dialog = new ProgressDialog(this);
		        dialog.setMessage(getResources().getText(R.string.loading_wait));
		        dialog.setCancelable(false);
		        return dialog;      
            case DIALOG_CONNECTION_ERROR_KEY:
            	builder = new AlertDialog.Builder(this);
            	builder.setMessage(R.string.connection_error)
            	       .setCancelable(false)
            	       .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
            	           public void onClick(DialogInterface dialog, int id) {
            	        	   dialog.cancel();
            	           }
            	       });
            	return builder.create();
            case DIALOG_LOGIN_FAIL_KEY:
            	builder = new AlertDialog.Builder(this);
            	builder.setMessage(R.string.ytoken_fail_error)
            	       .setCancelable(false)
            	       .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
            	           public void onClick(DialogInterface dialog, int id) {
            	                dialog.cancel();
            	           }
            	       });
            	return builder.create();
        }
        return super.onCreateDialog(id);
    }

 
    
    
	/**
	 * Called when the OAuthRequestTokenTask finishes (user has authorized the request token).
	 * The callback URL will be intercepted here.
	 */
	@Override
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent); 
		//SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		final Uri uri = intent.getData();
		if (uri != null && uri.getScheme().equals(Constants.OAUTH_CALLBACK_SCHEME)) {
			
			Log.i(TAG, "Callback received : " + uri);
			Log.i(TAG, "Retrieving Access Token");
			Log.i(TAG, consumer.getToken());
			Log.i(TAG, consumer.getTokenSecret());
			Log.i(TAG, uri.getQueryParameter("oauth_verifier"));
			
			Log.i(TAG,"Requesting google login URL : " + Constants.GOOGLE_LOGIN_URL);

	    	AsyncHttpClient client = new AsyncHttpClient();
	    	RequestParams params = new RequestParams();
	    	
	    	params.put("YQUEST_PROTOCOL", Constants.YQUEST_PROTOCOL.toString());
	    	params.put("google_token", consumer.getToken());
	    	params.put("google_secret", consumer.getTokenSecret());
	    	params.put("google_verifier", uri.getQueryParameter("oauth_verifier"));

	    	// TODO убрать из рабочего кода
	        try {
				client.setSSLSocketFactory(new SSCSSLSocketFactory(null));
			} catch (KeyManagementException e) {
				Log.i(TAG,e.getMessage());
				showDialog(DIALOG_CONNECTION_ERROR_KEY);
			} catch (NoSuchAlgorithmException e) {
				Log.i(TAG,e.getMessage());
				showDialog(DIALOG_CONNECTION_ERROR_KEY);
			} catch (KeyStoreException e) {
				Log.i(TAG,e.getMessage());
				showDialog(DIALOG_CONNECTION_ERROR_KEY);
			} catch (UnrecoverableKeyException e) {
				Log.i(TAG,e.getMessage());
				showDialog(DIALOG_CONNECTION_ERROR_KEY);
			}
	        client.post(Constants.GOOGLE_LOGIN_URL,params, new GoogleLoginResponseHandler() );

		}else{
			showDialog(DIALOG_CONNECTION_ERROR_KEY);
		}
	}
	
	
	
	
	private class GoogleLoginResponseHandler extends JsonHttpResponseHandler{
    	
    	@Override
        public void onSuccess(JSONObject response) {
            Log.i(TAG, response.toString());
            
            try {
            	if(response.getBoolean("success")){
            		Log.i(TAG,"success");
					SharedPreferences.Editor editor = settings.edit();
					editor.putString("auth_token", response.getString("auth_token"));
					editor.putString("auth_id", response.getString("auth_id"));
					editor.commit();
				}else{
					//просмотр ошибок
					JSONArray errors = response.getJSONArray("errors"); 
					for(int i = 0; i <errors.length(); i++){
						switch(errors.getInt(i)){
						case Constants.login_fail:
							showDialog(DIALOG_LOGIN_FAIL_KEY);
							Log.i(TAG, "lofin_fail");
							break;
						case Constants.PROTOCOL_ERROR:
							Log.i(TAG, "PROTOCOL_ERROR");
							break;
						case Constants.SERVER_CONFIG_ERROR:
							Log.i(TAG, "SERVER_CONFIG_ERROR");
							break;
						case Constants.SERVER_DB_ERROR:
							Log.i(TAG, "SERVER_DB_ERROR");
							break;
						default:
							showDialog(DIALOG_CONNECTION_ERROR_KEY);
							return;
						}
					}
				}
			} catch (JSONException e) {
				Log.i(TAG,e.getMessage());
				showDialog(DIALOG_CONNECTION_ERROR_KEY);
			}
        }
        @Override
        public void onStart() {
         	showDialog(DIALOG_LOAD_KEY);
        	setRequestedOrientation(getResources().getConfiguration().orientation);
        }
        @Override
        public void onFailure(Throwable e) {
        	Log.i(TAG,e.getMessage());
        	showDialog(DIALOG_CONNECTION_ERROR_KEY);
        }
        @Override
        public void onFinish() {
        	Log.i(TAG,"stop");
        	dismissDialog(DIALOG_LOAD_KEY);
        	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }
   
    
}