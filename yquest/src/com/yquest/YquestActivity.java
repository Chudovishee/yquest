package com.yquest;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;

import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

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
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import javax.net.ssl.SSLSocket;

public class YquestActivity extends Activity {
	
	final String TAG = getClass().getName();
	
	//private final YquestActivity self = this;
	
	private static final int DIALOG_LOAD_KEY = 1;
	private static final int DIALOG_CONNECTION_ERROR_KEY = 2;
	private static final int DIALOG_LOGIN_FAIL_KEY = 3;
	private static final int DIALOG_GOOGLE_AUTH_KEY = 4;
	
	private  SharedPreferences settings;
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.login);
        try {
        
        	SSCSSLSocketFactory factory;
		

			factory = new SSCSSLSocketFactory(null);
		
	        SSLSocket s = (SSLSocket) factory.createSocket();
	        s.connect(new InetSocketAddress("192.168.2.9",6960));
	        
	        AuthRequest auth = new AuthRequest(s);
	        auth.execute();
			
    	} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
//        YquestClient client = new YquestClient(){
//            protected void onProgressUpdate(String... strings) {
//                int count = strings.length;
//                for (int i = 0; i < count; i++) {
//                    Log.i(TAG,strings[i]);
//                }
//            }
//        };
//        client.execute();
        
 
        
        
        
        
        
        
        
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        // try auto login
        if(settings.contains("auth_id")){
	        String auth_id = settings.getString("auth_id", "");
	        String auth_token = settings.getString("auth_token", "");
	        Log.i(TAG, "try auto login with");
	        Log.i(TAG, "auth_id: " + auth_id);
	        Log.i(TAG, "auth_token: " + auth_token);
	        
	        ping(auth_id,auth_token);
        
        }
        
    	Log.i(TAG, "visible login buttons");
    	
    	Button useGoogleButton = (Button) findViewById(R.id.user_google_button);
    	Button useYquestButton = (Button) findViewById(R.id.use_yquest_button);
    	
    	useGoogleButton.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View v) {
    			showDialog(DIALOG_GOOGLE_AUTH_KEY);
            }
        });
    	
    	useYquestButton.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View v) {
    			Log.i(TAG, "y quest login");        	
                startActivity(new Intent().setClass(v.getContext(), YquestLogin.class));
            }
        });

    }
    
    @Override
    protected void onPause(){
    	super.onPause();
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
    	AlertDialog.Builder builder;
        switch (id) {
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
            case DIALOG_GOOGLE_AUTH_KEY:
            	builder = new AlertDialog.Builder(this);
            	builder.setMessage(R.string.google_auth_notice)
            	       .setCancelable(false)
            	       .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            	           public void onClick(DialogInterface dialog, int id) {
            	        	   Log.i(TAG, "google login");
            	        	   GoogleLogin();
            	        	   dialog.cancel();
            	           }
            	       })
            	       .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            	           public void onClick(DialogInterface dialog, int id) {
            	                dialog.cancel();
            	           }
            	       });
            	return builder.create();
        }
        return super.onCreateDialog(id);
    }
    
    
    private void ping(String auth_id, String auth_token){
    	Log.i(TAG,"Requesting ping URL : " + Constants.EXEC_URL);

    	AsyncHttpClient client = new AsyncHttpClient();
    	RequestParams params = new RequestParams();
    	
    	params.put("YQUEST_PROTOCOL", Constants.YQUEST_PROTOCOL.toString());
    	params.put("auth_id", auth_id);
    	params.put("auth_token", auth_token);
    	params.put("command", "ping");
    	
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
        client.post(Constants.EXEC_URL,params, new pingHandler() );
    }
    
    
    private void GoogleLogin(){
    	startActivity(new Intent().setClass(getBaseContext(), GoogleLoginActivity.class));
    }
    
    
    private class pingHandler extends JsonHttpResponseHandler{
    	
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
					startActivity(new Intent().setClass(getBaseContext(), QuestMapActivity.class));
				}else{
					SharedPreferences.Editor editor = settings.edit();
					editor.remove("auth_id");
					editor.remove("auth_token");
					editor.commit();
					//просмотр ошибок
					JSONArray errors = response.getJSONArray("errors"); 
					for(int i = 0; i <errors.length(); i++){
						switch(errors.getInt(i)){
						case Constants.auth_fail:
							showDialog(DIALOG_LOGIN_FAIL_KEY);
							Log.i(TAG, "auth_fail");
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
        	SharedPreferences.Editor editor = settings.edit();
			editor.remove("auth_id");
			editor.remove("auth_token");
			editor.commit();
        	showDialog(DIALOG_CONNECTION_ERROR_KEY);
        }
        @Override
        public void onFinish() {
        	Log.i(TAG,"finish");
        	dismissDialog(DIALOG_LOAD_KEY);
        	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }
    
    
   
}

