package com.hooapps.pca.cvilleart.Transportation;

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

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
/**
 * Loaded in when the appropriate tab is selected on the TransportationParkingFragment.
 * Tier 3 Fragment
 * @author Alex Ramey
*/
public class TransportationParkingMapFragment extends Fragment implements 
GooglePlayServicesClient.OnConnectionFailedListener {
	
	private GoogleMap mMap;
	private SupportMapFragment fragment;
	private LatLng waterStGarage;
	private LatLng waterStLot;
	private LatLng marketStGarage;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// If the activity is being recreated, restore previous version
		// Mostly, this is needed in a two-pane layout
		if (savedInstanceState != null) {
			// TODO Code to restore prior version here  
		}
		
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.parking_map, container, false);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		fragment = new SupportMapFragment();
		FragmentManager fm = getChildFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.parking_fragment_content, fragment);
		ft.commit();
		
		waterStGarage = new LatLng(38.029242,-78.480583);
		waterStLot = new LatLng(38.029581,-78.481654);
		marketStGarage = new LatLng(38.030409,-78.477736);
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		setUpMap();
		// During startup, check if there are arguments passed to the fragment.
        // onStart is a good place to do this because the layout has already been
        // applied to the fragment at this point so we can safely call the method
        // below that sets the article text.
		Bundle args = getArguments();
		if (args != null) {
			// TODO Modify fragment according to settings
		} else {
			// TODO Setup the fragment according to other specifications
		}
	}
	
	/** Instantiate Map if necessary, then set it up*/
	public void setUpMap()
	{
		if (mMap == null) {
	        // Try to obtain the map from the SupportMapFragment.
	        mMap = fragment.getMap();
	    }
		if (mMap != null){
			mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
			mMap.setMyLocationEnabled(true);//creates "blue dot" that is updated on map automatically as user moves
			addMarkers();
			mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(38.030214,-78.479935), 16));
			
		}
	}
	
	private void addMarkers()
	{
		MarkerOptions waterLot = new MarkerOptions().title("Water Street Lot").position(waterStLot);
		mMap.addMarker(waterLot);
		
		MarkerOptions waterGarage = new MarkerOptions().title("Water Street Garage").position(waterStGarage);
		mMap.addMarker(waterGarage);
		
		MarkerOptions marketGarage = new MarkerOptions().title("Market Street Garage").position(marketStGarage);
		mMap.addMarker(marketGarage);
		
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
		// TODO Auto-generated method stub
		
	}	
}
