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

import java.util.ArrayList;
import java.util.Locale;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hooapps.pca.cvilleart.R;

// TODO Update the JavaDoc description as the functionality increases

/**
 * Fragment to display the location-specific art in CVille. [MORE HERE]
 *
 * @author Spencer Gennari
 * @author Alex Ramey
 *
 */

//Add Comments As Functionality Increases
//Thus far, NearMeFragment contains a SupportMapFragment that keeps track of the user's location
//Location Tracking is initiated by mMap.setMyLocationEnabled(true). Additionally, a LocationClient is 
//set up that can be used to get the user's current location at any time. (Perhaps for distance to venue)
//TODO If we want to receive notifications of the user's LatLng every few seconds in order to respond
// with event-driven code, we need to use LocationClient and implement com.google.android.gms.location.LocationListener.
// Write Code to Handle Devices For Which Google Play Services Aren't Enabled and to Handle Failed 
// Connection Attempts with the LocationClient


//TODO: Sometimes the app crashes when leaving NearMeFragment b/c Acitvity gets destroyed?

public class NearMeFragment extends Fragment implements
GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener,
OnCheckedChangeListener {
	
	private GoogleMap mMap;
	private SupportMapFragment fragment;
	private LocationClient mLocationClient;
	private ArrayList<ToggleButton> toggles;
	private boolean[] toggleStates;
	private LinearLayout toggleHolder;
	Geocoder addressStringToAddressObject;
	
	//For Testing Purposes
	public static final LatLng jefTheaterLocation = new LatLng(38.030656,-78.481205);
	private static final String jefTheaterAddress = "110 E Main St, Charlottesville, VA";
	
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		toggleStates = new boolean[6];

		fragment = new SupportMapFragment();
		FragmentManager fm = getChildFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.near_me_fragment_content, fragment);
		ft.commit();
		
		mLocationClient = new LocationClient(this.getActivity(), this, this);
		
		Log.d("Checkpoints", Geocoder.isPresent() + "");
		if (Geocoder.isPresent())
		{
			addressStringToAddressObject = new Geocoder(this.getActivity(), Locale.US);
		}
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
			mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			mMap.setMyLocationEnabled(true); //creates "blue dot" that is updated on map automatically as user moves
		}
	}
	
	/**Clears map, toggles the appropriate boolean value in the toggleStates array, and redraws markers based on the toggle states */
	//TODO: Replace Test Code with code that traverses a given set of data and either makes a marker for every piece of data in the set
	// if there will be sets for each venue type or makes a marker for each object in the set whose venue field is the appropriate type
	// if all venues will be held in one set with a field value that denotes their type
	
	//TODO: Create all markers to register click events; when the event is received, the set can be searched 
	//		for the object whose title field matches the marker title, and then a method like onDiscoverViewSelected
	//		in the main activity can be called and sent the object as a parameter. The method will replace the 
	//		current fragment with a DiscoverItemFragment populated by information contained in the object.
	
	public void setUpMap(int markerType)
	{
		if (mMap == null)
			return;
		mMap.clear();
		
		toggleStates[markerType] = !toggleStates[markerType];
		
		//Dance
		if (toggleStates[0])
		{
			MarkerOptions test = new MarkerOptions().title("Dance").position(new LatLng(38.031,-78.481))
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.dancemarker)).anchor((float).50,(float).50);
			mMap.addMarker(test);
		}
		//Film
		if (toggleStates[1])
		{
			MarkerOptions test = new MarkerOptions().title("Film").position(new LatLng(38.032,-78.482))
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.filmmarker)).anchor((float).50,(float) .50);;
			mMap.addMarker(test);
		}
		//Gallery
		if (toggleStates[2])
		{
			MarkerOptions test = new MarkerOptions().title("Gallery").position(new LatLng(38.030,-78.480))
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.gallerymarker)).anchor((float).50,(float) .50);;
			mMap.addMarker(test);
		}
		//Music
		if (toggleStates[3])
		{
			Address address = null;
			try{
				address = addressStringToAddressObject.getFromLocationName(jefTheaterAddress,1).get(0);
			}
			catch (Exception e)
			{
				Log.d("Checkpoints","Address Exception");
			}
			if (address != null){
				LatLng JefTheater = new LatLng(address.getLatitude(), address.getLongitude());
				MarkerOptions test2 = new MarkerOptions().title("Jefferson Theater").position(JefTheater)
						.icon(BitmapDescriptorFactory.fromResource(R.drawable.musicmarker)).anchor((float).50,(float) .50);
				mMap.addMarker(test2);
			}
		}
		//Other
		if (toggleStates[4])
		{
			MarkerOptions test = new MarkerOptions().title("Other").position(new LatLng(38.029,-78.479))
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.othermarker)).anchor((float).50,(float) .50);;
			mMap.addMarker(test);
		}
		//Theatre
		if (toggleStates[5])
		{
			MarkerOptions test = new MarkerOptions().title("Theatre").position(new LatLng(38.033,-78.483))
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.theatremarker)).anchor((float).50,(float) .50);
			mMap.addMarker(test);
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
		
		toggleHolder = (LinearLayout) this.getView().findViewById(R.id.toggleholder);
		LayoutParams params = toggleHolder.getLayoutParams();
		params.height = getPixelHeight();
		
		setUpMap();
		
		toggles = new ArrayList<ToggleButton>();
		
		toggles.add((ToggleButton)(this.getView().findViewById(R.id.dancetoggle)));
		toggles.add((ToggleButton)(this.getView().findViewById(R.id.filmtoggle)));
		toggles.add((ToggleButton)(this.getView().findViewById(R.id.gallerytoggle)));
		toggles.add((ToggleButton)(this.getView().findViewById(R.id.musictoggle)));
		toggles.add((ToggleButton)(this.getView().findViewById(R.id.othertoggle)));
		toggles.add((ToggleButton)(this.getView().findViewById(R.id.theatretoggle)));
		
		
		//Every time the fragment is refreshed, all of the toggles start off enabled
		for (ToggleButton i : toggles)
		{
			i.setText("");
			i.setTextOff("OFF");
			i.setTextOn("");
			i.setChecked(true);
			i.setOnCheckedChangeListener(this);
		}
		
		for (int i = 0; i < toggleStates.length - 1; i++)
		{
			toggleStates[i] = true;
		}
		
		toggleStates[toggleStates.length - 1] = false;
		//Is flipped to True when setUpMap() is called
		setUpMap(toggleStates.length - 1);
		
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

	//If the user isn't within 10 miles of JefTheater, map centers on JefTheater

	@Override
	public void onConnected(Bundle connectionHint) {
		Log.d("Checkpoints","LocationClient Connected");
		Location mCurLoc = mLocationClient.getLastLocation();
		double mCurLat = mCurLoc.getLatitude();
		double mCurLong = mCurLoc.getLongitude();
		
		float[] distanceFromJefTheater = new float[1];
		Location.distanceBetween(mCurLat,mCurLong,jefTheaterLocation.latitude,jefTheaterLocation.longitude,distanceFromJefTheater);
		
		if (distanceFromJefTheater[0] < 16093) //16093 meters = 10 miles
		{
			LatLng mCurrentLocation = new LatLng(mCurLat, mCurLong);
			mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLocation, 18));
		}
		else
		{
			mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(jefTheaterLocation, 18));
		}
		
	}

	@Override
	public void onDisconnected() {
		Log.d("Checkpoints","LocationClient Disconnected");
	}
	
	public int getPixelHeight()
	{
		DisplayMetrics metrics = this.getActivity().getResources().getDisplayMetrics();
		int width = metrics.widthPixels;
		return width/6;
	}

	/** Map is redrawn whenever a button is toggled*/
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView.getId() == R.id.dancetoggle)
		{
			setUpMap(0);
		}
		else if (buttonView.getId() == R.id.filmtoggle)
		{
			setUpMap(1);
		}
		else if (buttonView.getId() == R.id.gallerytoggle)
		{
			setUpMap(2);
		}
		else if (buttonView.getId() == R.id.musictoggle)
		{
			setUpMap(3);
		}
		else if (buttonView.getId() == R.id.othertoggle)
		{
			setUpMap(4);
		}
		else if (buttonView.getId() == R.id.theatretoggle)
		{
			setUpMap(5);
		}
	}
}
