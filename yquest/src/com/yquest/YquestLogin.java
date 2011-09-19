package com.yquest;


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
import android.widget.EditText;


public class YquestLogin extends Activity {
	
	final String TAG = getClass().getName();
	private final YquestLogin self = this;
	private static final int DIALOG_LOAD_KEY = 1;
	private static final int DIALOG_CONNECTION_ERROR_KEY = 2;
	private static final int DIALOG_LOGIN_FAIL_KEY = 3;

	
	Button regButton,
		loginButton;
	EditText mail,
		password;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.ylogin);
        
    	mail = (EditText) findViewById(R.id.mail);
    	password = (EditText) findViewById(R.id.password);
    	
    	loginButton = (Button) findViewById(R.id.login_button);
        regButton = (Button) findViewById(R.id.reg_button);
    	
    	regButton.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View v) {
    			Log.i(TAG, "reg");
                startActivity(new Intent().setClass(v.getContext(), YquestReg.class));
            }
        });
    	
    	loginButton.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View v) {
    			Log.i(TAG, "login");
                login();
            }
        });
        
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
            	builder.setMessage(R.string.login_fail_error)
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
    
    
    public void login(){	
    	Log.i(TAG,"Requesting reg URL : " + Constants.YQUEST_LOGIN_URL);

    	AsyncHttpClient client = new AsyncHttpClient();
    	RequestParams params = new RequestParams();
    	
    	params.put("YQUEST_PROTOCOL", Constants.YQUEST_PROTOCOL.toString());
    	params.put("mail", mail.getText().toString());
    	params.put("password", password.getText().toString());
    	
    	
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
    	
        client.post(Constants.YQUEST_LOGIN_URL,params, new ResponseHandler() );
    }
    
    
    
	private class ResponseHandler extends JsonHttpResponseHandler{
    	
    	@Override
        public void onSuccess(JSONObject response) {
            Log.i(TAG, response.toString());
            
            try {
				if(response.getBoolean("success")){
					SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(self);
					SharedPreferences.Editor editor = settings.edit();
					
					editor.putInt("auth_type", Constants.yquest_auth);
					editor.putString("auth_token", response.getString("auth_token"));
					editor.putString("auth_id", response.getString("auth_id"));
				    
					editor.commit();
					
				}else{
					//просмотр ошибок
					JSONArray errors = response.getJSONArray("errors"); 
					for(int i = 0; i <errors.length(); i++){
//						
						switch(errors.getInt(i)){
						case Constants.login_fail:
							showDialog(DIALOG_LOGIN_FAIL_KEY);
							Log.i(TAG, "lofin_fail");
							return;
						case Constants.PROTOCOL_ERROR:
							Log.i(TAG, "PROTOCOL_ERROR");
							break;
						case Constants.SERVER_CONFIG_ERROR:
							Log.i(TAG, "SERVER_CONFIG_ERROR");
							break;
						case Constants.SERVER_DB_ERROR:
							Log.i(TAG, "SERVER_DB_ERROR");
							break;
						}
					}
					showDialog(DIALOG_CONNECTION_ERROR_KEY);
				}
			} catch (JSONException e) {
				Log.i(TAG,e.getMessage());
				showDialog(DIALOG_CONNECTION_ERROR_KEY);
			}
            
            

            
        }
        @Override
        public void onStart() {
            // Initiated the request
        	showDialog(DIALOG_LOAD_KEY);
        	setRequestedOrientation(getResources().getConfiguration().orientation);

        }

        @Override
        public void onFailure(Throwable e) {

        	showDialog(DIALOG_CONNECTION_ERROR_KEY);
        }

        @Override
        public void onFinish() {
            // Completed the request (either success or failure)
        	Log.i(TAG,"stop");
        	dismissDialog(DIALOG_LOAD_KEY);
        	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    	
    }
    
    
    
}