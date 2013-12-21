/*
 * 
 * Maps Set-Up Tutorial
 * Part I:  https://blog-emildesign.rhcloud.com/?p=403
 * Part II: https://blog-emildesign.rhcloud.com/?p=435
 * 
 * Resolved Duplicate ID Binary XML File Error that Happened when Reloading NearMe Fragment Using Code Found Here:
 * http://stackoverflow.com/questions/15562416/error-inflating-class-fragment-duplicate-id-illegalargumentexception
 * 
 * Adapted from the online code samples found at:
 * http://developer.android.com/training/basics/fragments/index.html
 * 
 * Heavily modified for use within this specific app,
 * but always give credit where credit is due.
 */
package com.hooapps.pca.cvilleart;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.hooapps.pca.cvilleart.R;

// TODO Update the JavaDoc description as the functionality increases

/**
 * Fragment to display the location-specific art in CVille. [MORE HERE]
 * 
 * @author Spencer Gennari Alex Ramey
 *
 */

//Add Comments As Functionality Increases
//Thus far, NearMeFragment contains a SupportMapFragment that keeps track of the user's location
//Location Tracking is initiated by mMap.setMyLocationEnabled(true). Additionally, a LocationClient is 
//set up that can be used to get the user's current location at any time. (Perhaps for distance to venue)
//TODO If we want to receive notifications of the user's LatLng every few seconds in order to respond
//  		with event-driven code, we need to use LocationClient
//			and implement com.google.android.gms.location.LocationListener.
//     Write Code to Handle Devices For Which Google Play Services Aren't Enabled and to Handle Failed 
//		Connection Attempts with the LocationClient

public class NearMeFragment extends Fragment implements GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener  {
	
	private GoogleMap mMap;
	private SupportMapFragment fragment;
	private LocationClient mLocationClient;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		fragment = new SupportMapFragment();
		FragmentManager fm = getChildFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.fragment_content, fragment);
		ft.commit();
		
		mLocationClient = new LocationClient(this.getActivity(), this, this);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// If the activity is being recreated, restore previous version
		// Mostly, this is needed in a two-pane layout
		if (savedInstanceState != null) {
			// TODO Code to restore prior version here  
		}
		
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.near_me_view, container, false);
	}
	
	/** Instantiate Map if necessary, then set it up*/
	public void setUpMap()
	{
		if (mMap == null) {
	        // Try to obtain the map from the SupportMapFragment.
	        mMap = fragment.getMap();
	    }
		if (mMap != null){
			mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			mMap.setMyLocationEnabled(true);
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		// During startup, check if there are arguments passed to the fragment.
		// Also, setUpMap(), which obtains MapView reference from SupportMapFragment.
        // onStart is a good place to do this because the layout has already been
        // applied to the fragment at this point so we can safely call methods that
		// rely upon the layout having already been set up
		setUpMap();
		Bundle args = getArguments();
		if (args != null) {
			// TODO Modify fragment according to settings
		} else {
			// TODO Setup the fragment according to other specifications
		}
	}
	
	//Connect LocationClient in onResume()
	@Override
	public void onResume()
	{
		super.onResume();
		if (servicesConnected() && !mLocationClient.isConnected())
		{
			mLocationClient.connect();
		}
	}
	
	//Disconnect in onPause()- This conserves battery
	@Override
	public void onPause()
	{
		super.onPause();
		if (mLocationClient.isConnected())
		{
			mLocationClient.disconnect();
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		// TODO Save important info form the fragment here
		// Use the format 'outState.put[String/Boolean/Int/etc.](key, value);'
	}

	private boolean servicesConnected() {
	// Check that Google Play services is available
		int errorCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.getActivity());
		if (errorCode != ConnectionResult.SUCCESS) {
		  GooglePlayServicesUtil.getErrorDialog(errorCode, this.getActivity(), 0).show();
		  return false;
		}
		Log.d("Checkpoints","GooglePlayServicesAvailable");
		return true;
	}
	
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.d("Checkpoints","LocationClient Connection Failed");
		//TODO Add Code As Seen On Google API to Resolve Some Failures and Re-attempt Connection
    }

	//Centers map on user every time LocationClient Connects (whenever onResume() is called)
	@Override
	public void onConnected(Bundle connectionHint) {
		Log.d("Checkpoints","LocationClient Connected");
		Location mCurLoc = mLocationClient.getLastLocation();
		double mCurLat = mCurLoc.getLatitude();
		double mCurLong = mCurLoc.getLongitude();
		LatLng mCurrentLocation = new LatLng(mCurLat, mCurLong);
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLocation, 18));
	}

	@Override
	public void onDisconnected() {
		Log.d("Checkpoints","LocationClient Disconnected");
	}

}
