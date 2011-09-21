package com.yquest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import javax.net.ssl.SSLSocket;

import android.os.AsyncTask;

public class YquestClient extends AsyncTask<Void, String, Void>{
	
	final String TAG = getClass().getName();
	
	public YquestClient(){
		
	}
	
    



	@Override
	protected Void doInBackground(Void... params) {
		try {
        	SSCSSLSocketFactory factory;
			
        	factory = new SSCSSLSocketFactory(null);
			
            SSLSocket s = (SSLSocket) factory.createSocket();
            s.connect(new InetSocketAddress("192.168.2.9",6960));
            
            //outgoing stream redirect to socket
            OutputStream out =  s.getOutputStream();
            
            PrintWriter output = new PrintWriter(out);
            output.print("2 1\n");
            BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
            output.print("1 4e70c9c1aa8f694d1b000000 d8976fb3d57f04d41cc70328d0e8eeccdc7540610c6983e81d847a72aed07a1f4d7501f2ec9159ce2b33734a55339977129b838a26a54bbf4ebdd8bd2dbc1f35\n");
            output.flush();
            
            String st = input.readLine();
            publishProgress(st);
            st = input.readLine();
            publishProgress(st);
            st = input.readLine();
            publishProgress(st);
            //s.close();
            
	            
	    } catch (UnknownHostException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	    } catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	    }catch (KeyManagementException e) {
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
		}
        return null;
	}

}
