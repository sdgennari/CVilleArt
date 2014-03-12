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
import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hooapps.pca.cvilleart.R;
import com.hooapps.pca.cvilleart.DataElems.PCAContentProvider;
import com.hooapps.pca.cvilleart.DataElems.PCAContentProvider.Categories;
import com.hooapps.pca.cvilleart.DataElems.VenueTable;
import com.hooapps.pca.cvilleart.DiscoverListFragment.OnDiscoverViewSelectedListener;
import com.hooapps.pca.cvilleart.ListViewElems.Item;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

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

public class NearMeFragment extends Fragment implements GooglePlayServicesClient.ConnectionCallbacks,
	GooglePlayServicesClient.OnConnectionFailedListener,
	OnCheckedChangeListener {
	
	public interface OnInfoWindowSelectedListener {
		/**
		 * Called by NearMeFragment when an InfoWindow is selected
		 * 
		 * @param id The id of the venue in the database
		 * */
		public void onInfoWindowSelected(int id);
	}
	
	private OnInfoWindowSelectedListener mCallback;
	
	private LayoutInflater inflater;
	private Context context;
	private HashMap<Marker, VenueHolder> venueMap;
	
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
		
		venueMap = new HashMap<Marker, VenueHolder>();
		context = this.getActivity().getApplicationContext();
		
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
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Make sure that the activity implements OnViewSelectedListener
		// If not, throw an exception
		try {
			mCallback = (OnInfoWindowSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnInfoWindowSelectedListener");
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// If the activity is being recreated, restore previous version
		// Mostly, this is needed in a two-pane layout
		if (savedInstanceState != null) {
			// TODO Code to restore prior version here  
		}
		
		this.inflater = inflater;
		
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
	
	private void saveMapCenterToPrefs() {
		CameraPosition camera = mMap.getCameraPosition();
		int lat = (int)(camera.target.latitude * 1000000);
		int lon = (int)(camera.target.longitude * 1000000);
		
		SharedPreferences prefs = this.getActivity().getSharedPreferences("com.hooapps.pca", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt(VenueTable.LAT, lat);
		editor.putInt(VenueTable.LON, lon);
		editor.commit();
	}
	
	private LatLng retrieveMapCenterFromPrefs() {
		SharedPreferences prefs = this.getActivity().getSharedPreferences("com.hooapps.pca", Context.MODE_PRIVATE);
		int lat = prefs.getInt(VenueTable.LAT, -1);
		int lon = prefs.getInt(VenueTable.LON, -1);
		
		if (lat == -1 || lon == -1) {
			return null;
		}
		
		return new LatLng(lat/1000000.0, lon/1000000.0);
	}
	
	private void clearMapCenterFromPrefs() {
		SharedPreferences prefs = this.getActivity().getSharedPreferences("com.hooapps.pca", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.remove(VenueTable.LAT);
		editor.remove(VenueTable.LON);
		editor.commit();
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
		if (mMap == null) {
			return;
		}
		mMap.clear();
		
		String[] projection = {
				VenueTable.COLUMN_ID,
				VenueTable.ORGANIZATION_NAME,
				VenueTable.CATEGORY_ART_COMMUNITY_CATEGORIES,
				VenueTable.ADDRESS_HOME_STREET,
				VenueTable.LAT,
				VenueTable.LON,
				VenueTable.IMAGE_URLS
		};
		
		Cursor cursor = this.getActivity().getContentResolver().query(PCAContentProvider.VENUE_CONTENT_URI, projection, null, null, null);
		
		while (cursor != null && cursor.moveToNext()) {
			int id = cursor.getInt(cursor.getColumnIndex(VenueTable.COLUMN_ID));
			String name = cursor.getString(cursor.getColumnIndex(VenueTable.ORGANIZATION_NAME));
			String address = cursor.getString(cursor.getColumnIndex(VenueTable.ADDRESS_HOME_STREET));
			double lat = (cursor.getInt(cursor.getColumnIndex(VenueTable.LAT)) / 1000000.0);
			double lon = (cursor.getInt(cursor.getColumnIndex(VenueTable.LON)) / 1000000.0);
			String categoryString = cursor.getString(cursor.getColumnIndex(VenueTable.CATEGORY_ART_COMMUNITY_CATEGORIES));
			String imageUrl = cursor.getString(cursor.getColumnIndex(VenueTable.IMAGE_URLS));
			
			int resId = 0;
			Categories category = Categories.valueOf(categoryString.toUpperCase(Locale.getDefault()));
			switch (category) {
			case DANCE: resId = R.drawable.dance_marker;
				break;
			case MUSIC: resId = R.drawable.music_marker;
				break;
			case THEATRE: resId = R.drawable.theatre_marker;
			break;
			case VISUAL_ARTS: resId = R.drawable.gallery_marker;
				break;
			case VENUE:
			default: resId = R.drawable.other_marker;
				break;
			}
						
			MarkerOptions test = new MarkerOptions().title(name).snippet(address).position(new LatLng(lat, lon)).icon(BitmapDescriptorFactory.fromResource(resId)).anchor(0.50F, 1.0F);
			Marker m = mMap.addMarker(test);
			
			VenueHolder holder = new VenueHolder(name, imageUrl, category, id);
			venueMap.put(m, holder);
		}
		
		// Set up the InfoWindows displayed when a marker is clicked
		mMap.setInfoWindowAdapter(new InfoWindowAdapter() {
			@Override
			public View getInfoWindow(Marker marker) {
			    // Getting view from the layout file
			    View v = inflater.inflate(R.layout.info_window, null);
			    
			    TextView title = (TextView) v.findViewById(R.id.venue_title);
			    title.setText(marker.getTitle());

			    TextView address = (TextView) v.findViewById(R.id.address);
			    address.setText(marker.getSnippet());
			    
			    ImageView imageView = (ImageView) v.findViewById(R.id.image);
			    String imagePath = venueMap.get(marker).imagePath;
			    int drawableResId = 0;
			    switch (venueMap.get(marker).category) {
			    case DANCE: drawableResId = R.drawable.dance;
			    	break;
			    case MUSIC: drawableResId = R.drawable.music;
			    	break;
			    case THEATRE: drawableResId = R.drawable.theatre;
			    	break;
			    case VISUAL_ARTS: drawableResId = R.drawable.gallery;
			    	break;
			    case VENUE:
			    default: drawableResId = R.drawable.other;
			    	break;
			    }
			    
			    if(imagePath != null && !imagePath.isEmpty()) {
			    	// TODO CONSIDER USING A CALLBACK HERE ONCE THE IMAGE LOADS
					Picasso.with(context).load(imagePath).placeholder(drawableResId).into(imageView);
				} else {
					// Load a placeholder image if no url is provided
					Picasso.with(context).load(drawableResId).placeholder(drawableResId).into(imageView);
				}

			    return v;
			}

			@Override
			public View getInfoContents(Marker arg0) {
			    return null;
			}
		});
		
		// Set up the OnClickListener for the InfoWindows
		mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

			@Override
			public void onInfoWindowClick(Marker marker) {
				mCallback.onInfoWindowSelected(venueMap.get(marker).id);
			}
		});
	}
	
	@Override
	public void onStart() {
		super.onStart();
		// During startup, check if there are arguments passed to the fragment.
		// Also, setUpMap(), which obtains MapView reference from SupportMapFragment.
        // onStart is a good place to do this because the layout has already been
        // applied to the fragment at this point so we can safely call methods that
		// rely upon the layout having already been set up
		
		Log.d("MAP", "onStart()");
		
		toggleHolder = (LinearLayout) this.getView().findViewById(R.id.toggleholder);
		LayoutParams params = toggleHolder.getLayoutParams();
		params.height = getPixelHeight();
		
		setUpMap();
		
		toggles = new ArrayList<ToggleButton>();
		
		toggles.add((ToggleButton)(this.getView().findViewById(R.id.dancetoggle)));
		//toggles.add((ToggleButton)(this.getView().findViewById(R.id.filmtoggle)));
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
		
		Log.d("MAP", "onResume()");
		
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
		
		Log.d("MAP", "onPause()");
		
		if (mMap != null) {
			saveMapCenterToPrefs();
		}
		
		if (mLocationClient.isConnected())
		{
			mLocationClient.disconnect();
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		Log.d("MAP", "onSaveInstanceState()");
		
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
		Location mCurLoc = mLocationClient.getLastLocation();
		double mCurLat = mCurLoc.getLatitude();
		double mCurLong = mCurLoc.getLongitude();
		
		Log.d("MAP", "onConnected()");
		
		float[] distanceFromJefTheater = new float[1];
		Location.distanceBetween(mCurLat,mCurLong,jefTheaterLocation.latitude,jefTheaterLocation.longitude,distanceFromJefTheater);
		
		LatLng center = retrieveMapCenterFromPrefs();
		
		if (distanceFromJefTheater[0] < 16093) { //16093 meters = 10 miles
			LatLng mCurrentLocation = new LatLng(mCurLat, mCurLong);
			mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLocation, 18));
		} else if (center != null) {
			mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 18));
		} else {
			mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(jefTheaterLocation, 18));
		}
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		// Clear saved map center from shared preferences
		clearMapCenterFromPrefs();
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
		/*
		else if (buttonView.getId() == R.id.filmtoggle)
		{
			setUpMap(1);
		}
		*/
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
	
	private class VenueHolder {
		public String name;
		public String imagePath;
		public Categories category;
		public int id;
		
		public VenueHolder (String name, String imagePath, Categories category, int id) {
			this.name = name;
			this.imagePath = imagePath;
			this.category = category;
			this.id = id;
		}
	}
}
