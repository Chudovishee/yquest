package com.yquest;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.text.Editable;
import android.text.TextWatcher;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.regex.Pattern;

import com.loopj.android.http.*;


public class YquestReg extends Activity {
	
	final String TAG = getClass().getName();
	
	private EditText	mail,
						password,
						password2,
						name;
	private Button regButton;

	private final YquestReg self = this;
	
	private static final int DIALOG_LOAD_KEY = 1;
	private static final int DIALOG_CONNECTION_ERROR_KEY = 2;
	private static final int DIALOG_DUPLICATE_MAIL_KEY = 3;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.yreg);
        
        Log.i(TAG, "creat");
        
    	mail = (EditText) findViewById(R.id.mail);
    	password = (EditText) findViewById(R.id.password);
    	password2 = (EditText) findViewById(R.id.password2);
    	name = (EditText) findViewById(R.id.name);
    	
    	regButton = (Button) findViewById(R.id.reg_button);
    	
    	//validation
        mail.addTextChangedListener(new InputValidator(mail));
        name.addTextChangedListener(new InputValidator(name));
        password.addTextChangedListener(new InputValidator(password));
        password2.addTextChangedListener(new InputValidator(password2));
        
        //click
    	regButton.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View v) {
    			Log.i(TAG, "reg");
                reg();
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
            case DIALOG_DUPLICATE_MAIL_KEY:
            	builder = new AlertDialog.Builder(this);
            	builder.setMessage(R.string.duplicate_mail_error)
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
    
    private void reg(){
    	
    	if(validate(mail) && 
    			validate(name) && 
    			validate(password) && 
    			validate(password2)){
    			
    		Log.i(TAG, "Validate Ok");
    		
        	Log.i(TAG,"Requesting reg URL : " + Constants.YQUEST_REG_URL);

        	AsyncHttpClient client = new AsyncHttpClient();
        	RequestParams params = new RequestParams();
        	
        	params.put("YQUEST_PROTOCOL", Constants.YQUEST_PROTOCOL.toString());
        	params.put("mail", mail.getText().toString());
        	params.put("name", name.getText().toString());
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
        	
            client.post(Constants.YQUEST_REG_URL,params, new ResponseHandler() );
    	
    	}else{
    		Log.i(TAG, "Validate fail");
    	} 	
    	
    }
        
    
    public Boolean validate(EditText et){
    	String s = et.getText().toString();
    	switch (et.getId()) {
        case R.id.mail:
            if (!Pattern.compile("^[_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,4})$",Pattern.CASE_INSENSITIVE).matcher(
            		s
            		).matches() ) {
            	et.setError(getResources().getText(R.string.validate_mail_error));
            	return false;
            }else{
            	et.setError(null);
            }
            break;
        case R.id.name:
        	if (!Pattern.compile("^([a-zа-я0-9_]{3,64}) ?([a-zа-я0-9_]{3,64})?$",Pattern.CASE_INSENSITIVE).matcher(
        			s
        			).matches() ) {
            	et.setError(getResources().getText(R.string.validate_name_error));
            	return false;
        	}else{
            	et.setError(null);
            }
            break;
        case R.id.password:
        	if(s.length() < Constants.PASSWORD_MIN_LEN){
        		et.setError(getResources().getText(R.string.validate_password_error));
        		return false;
        	}else{
            	et.setError(null);
            }
        	break;
        case R.id.password2:
        	if(!s.equals(password.getText().toString())){
        		et.setError(getResources().getText(R.string.validate_password2_error));
        		return false;
        	}else{
            	et.setError(null);
            }
        	break;   
        }
    	return true;
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
						
						switch(errors.getInt(i)){
						case Constants.duplicate_mail:
							showDialog(DIALOG_DUPLICATE_MAIL_KEY);
							Log.i(TAG, "duplicate_mail");
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
						case Constants.unauthorized_mail:
							Log.i(TAG, "unauthorized_mail");
							break;
						case Constants.unauthorized_name:
							Log.i(TAG, "unauthorized_name");
							break;
						case Constants.unauthorized_password:
							Log.i(TAG, "unauthorized_password");
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
    
    
    private class InputValidator implements TextWatcher {
    	private EditText et;

        private InputValidator(EditText editText) {
            this.et = editText;
        }

        public void afterTextChanged(Editable s) {

        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            validate(et);
        }
        
        
    }
    
    
}