package com.yquest;

import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.List;


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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

//debug 0xtmMDOnMgCZe_g3Dw-tDjf2nff0Q0LvdXohSTQ
//derujov 0xtmMDOnMgCaD1xev6EECGtC2xFEM6-0UWK53Lg
public class QuestMapActivity extends MapActivity {
	final String TAG = getClass().getName();
	private MapView myMap;
	private MyLocationOverlay myLocOverlay;
 
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
 
		initMap();
		initMyLocation();
	}
 
	/**
	 * Initialise the map and adds the zoomcontrols to the LinearLayout.
	 */
	private void initMap() {
		myMap = (MapView) findViewById(R.id.mapView);
 
		//View zoomView = myMap.getZoomControls();
		//LinearLayout myzoom = (LinearLayout) findViewById(R.id.myzoom);
		//myzoom.addView(zoomView);
		myMap.displayZoomControls(true);
 
	}
 
	/**
	 * Initialises the MyLocationOverlay and adds it to the overlays of the map
	 */
	private void initMyLocation() {
		myLocOverlay = new MyLocationOverlay(this, myMap);
		myLocOverlay.enableMyLocation();
		myMap.getOverlays().add(myLocOverlay);
 
	}
 
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}