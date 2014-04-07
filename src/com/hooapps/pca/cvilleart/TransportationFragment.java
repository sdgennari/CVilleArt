/*
 * Adapted from the online code samples found at:
 * http://developer.android.com/training/basics/fragments/index.html
 * 
 * Heavily modified for use within this specific app,
 * but always give credit where credit is due.
 */
package com.hooapps.pca.cvilleart;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hooapps.pca.cvilleart.R;
import com.hooapps.pca.cvilleart.DataElems.PCAContentProvider;
import com.hooapps.pca.cvilleart.DataElems.VenueTable;

// TODO Update the JavaDoc description as the functionality increases

/**
 * Fragment to provide information about various transportation routes to and
 * from art venues. This Fragment has a FragmentTabHost which contains a TabWidget wrapped
 * in a HorizontalScrollView that holds tabs and a FrameLayout in which tab content is displayed.
 * Tabs are added by invoking FragmentTabHost.addTab(TabSpec, FragmenttoLaunch, Bundle args), where 
 * the TabSpec defines the tab's tag (used inside code of FragmentTabHost) and indicator (label).
 * the setup method must be invoked before tabs can be added to finish initializing the FragmentTabHost.
 * You pass it the context, the ChildFragmentManager, and the FrameLayout that will contain the Fragments
 * that are loaded in by the tabs.
 * 
 * Tier 1 Fragment
 * @author Spencer Gennari
 * @author Alex Ramey
 *
 */

public class TransportationFragment extends Fragment {
	
	
	private static final String CAT_PACKAGE_NAME = "com.cville.cattail";
	private static final String GOOGLE_MAP_BASE = "http://maps.google.com/maps?";
	private static final String GOOGLE_MAP_START = "saddr=";
	private static final String GOOGLE_MAP_END = "daddr=";
	
	public static final LatLng center = new LatLng(38.030656,-78.481205);
	
	private ArrayList<Marker> markers = new ArrayList<Marker>();
	private LatLng waterStreetGarage = new LatLng(38.029357, -78.480873);
	private LatLng waterStreetLot = new LatLng(38.029534, -78.481688);
	private LatLng marketStreetGarage = new LatLng(38.030210, -78.477815);
	float density;
	
	private LayoutInflater inflater;
	private MapView mapView;
	private GoogleMap mMap;
	private View parentView;
	private HashMap<String, String> venueMap;
	AutoCompleteTextView startAutoComp;
	AutoCompleteTextView endAutoComp;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// If the activity is being recreated, restore previous version
		// Mostly, this is needed in a two-pane layout
		if (savedInstanceState != null) {
			// TODO Code to restore prior version here  
		}
		
		this.inflater = inflater;
		
		parentView = inflater.inflate(R.layout.transportation_view, container, false);
		
		// Load the mapView here to prevent null pointer exceptions
		mapView = (MapView) parentView.findViewById(R.id.map_view);
		mapView.onCreate(savedInstanceState);
		return parentView;
	}
	
	@Override
	public void onStart() {
		super.onStart();

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
		
		// Get the density of the screen
		DisplayMetrics metrics = new DisplayMetrics();
		this.getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
		density = metrics.density;

		// Initialize the map
		try {
			MapsInitializer.initialize(this.getActivity().getApplicationContext());
		} catch (GooglePlayServicesNotAvailableException e) {
			e.printStackTrace();
		}

		loadVenueMap();
		configureViews();

	}
	
	private void configureViews() {		
		// Bind listeners for buttons
		Button getDirectionsButton = (Button) parentView.findViewById(R.id.get_directions_button);
		getDirectionsButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String startAddress = startAutoComp.getText().toString();
				String endAddress = endAutoComp.getText().toString();
				
				createGoogleMapIntent(startAddress, endAddress);
			}
		});
		
		// Prepare the autocomplete textviews
		startAutoComp = (AutoCompleteTextView) parentView.findViewById(R.id.start_auto_comp);
		endAutoComp = (AutoCompleteTextView) parentView.findViewById(R.id.destination_auto_comp);
		String[] venues = new String[venueMap.keySet().size()];
		
		// Get the venues from the map keyset
		int i = 0;
		for (Object o : venueMap.keySet().toArray()) {
			venues[i] = o.toString();
			i++;
		}
		
		// Set the adapters for the AutoCompleteTextViews
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, venues);
		startAutoComp.setAdapter(adapter);
		endAutoComp.setAdapter(adapter);
		
		// Configure the MapView
		mMap = mapView.getMap();
		
		mMap.setInfoWindowAdapter(new InfoWindowAdapter() {
			@Override
			public View getInfoWindow(Marker marker) {
				// Getting view from the layout file
				View v = inflater.inflate(R.layout.garage_info_window, null);

				TextView title = (TextView) v.findViewById(R.id.garage_name);
				title.setText(marker.getTitle());
				
				return v;
			}

			@Override
			public View getInfoContents(Marker arg0) {
				return null;
			}
		});
		
		mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

			@Override
			public void onInfoWindowClick(Marker marker) {
				endAutoComp.setText(marker.getTitle());
				marker.hideInfoWindow();
			}
		});
		
		mMap.getUiSettings().setAllGesturesEnabled(false);
		mMap.getUiSettings().setScrollGesturesEnabled(true);
		
		// Add the three garages
		MarkerOptions options = new MarkerOptions().title("Water Street Garage").position(waterStreetGarage).icon(BitmapDescriptorFactory.fromResource(R.drawable.garage_marker)).anchor(0.50F, 1.0F);
		venueMap.put("Water Street Garage", waterStreetGarage.latitude + ", " + waterStreetGarage.longitude);
		markers.add(mMap.addMarker(options));
		
		options = new MarkerOptions().title("Water Street Lot").position(waterStreetLot).icon(BitmapDescriptorFactory.fromResource(R.drawable.garage_marker)).anchor(0.50F, 1.0F);
		venueMap.put("Water Street Lot", waterStreetLot.latitude + ", " + waterStreetLot.longitude);
		markers.add(mMap.addMarker(options));

		options = new MarkerOptions().title("Market Street Garage").position(marketStreetGarage).icon(BitmapDescriptorFactory.fromResource(R.drawable.garage_marker)).anchor(0.50F, 1.0F);
		venueMap.put("Market Street Garage", marketStreetGarage.latitude + ", " + marketStreetGarage.longitude);
		markers.add(mMap.addMarker(options));

		// Center the map on the three garages
		// Call this method after the mapView has loaded properly
		mapView.getViewTreeObserver().addOnGlobalLayoutListener( new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				// If you need this to be called again, then run again addOnGlobalLayoutListener.
				moveToMapCenter();
			}
		});
		
		// 
		if (this.isCATInstalled()) {
			LinearLayout catContainer = (LinearLayout)parentView.findViewById(R.id.cat_container);
			catContainer.setVisibility(View.GONE);
		}
		
		Button install = (Button) parentView.findViewById(R.id.download_cat_button);
		install.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + CAT_PACKAGE_NAME));
				    startActivity(intent);
				} catch (android.content.ActivityNotFoundException anfe) {
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + CAT_PACKAGE_NAME));
				    startActivity(intent);
				}
			}
		});
	}
	
	private void moveToMapCenter() {
		
		LatLngBounds.Builder boundsBuilder = LatLngBounds.builder();
		
		for (Marker m : markers) {
			boundsBuilder.include(m.getPosition());
		}
		
		mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), (int)(50 * density)));
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
		
		// TODO Save important info form the fragment here
		// Use the format 'outState.put[String/Boolean/Int/etc.](key, value);'
	}

	
	@Override
	public void onResume() {
		mapView.onResume();
		super.onResume();
	}
	
	@Override
	public void onPause() {
		mapView.onPause();
		super.onPause();
	}
	
	@Override
	public void onDestroy()
	{
		mapView.onDestroy();
		super.onDestroy();
	}
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mapView.onLowMemory();
	}
	
	private void createGoogleMapIntent(String startAddress, String endAddress) {
		
		if (endAddress == null || endAddress.isEmpty()) {
			Toast.makeText(getActivity(), "Please enter a destination", Toast.LENGTH_SHORT).show();
			return;
		}
		
		// Check to see if a better address is provided
		if (venueMap.get(startAddress) != null) {
			startAddress = venueMap.get(startAddress);
		}
		if (venueMap.get(endAddress) != null) {
			endAddress = venueMap.get(endAddress);
		}
		
		// Encode the uri
		String uri = GOOGLE_MAP_BASE;
		uri += GOOGLE_MAP_START + Uri.encode(startAddress);
		uri += "&" + GOOGLE_MAP_END + Uri.encode(endAddress);
		
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
		intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
		startActivity(intent);
		
	}
	
	private void loadVenueMap() {
		
		venueMap = new HashMap<String, String>();
		
		// TODO CONSIDER USING LAT, LON
		String[] projection = {
				VenueTable.ORGANIZATION_NAME,
				VenueTable.ADDRESS_HOME_STREET };
		
		Cursor cursor = this.getActivity().getContentResolver().query(PCAContentProvider.VENUE_CONTENT_URI, projection, null, null, null);
		
		if (cursor != null) {
			while (cursor.moveToNext()) {
				String name = cursor.getString(cursor.getColumnIndex(VenueTable.ORGANIZATION_NAME));
				String address = cursor.getString(cursor.getColumnIndex(VenueTable.ADDRESS_HOME_STREET));
				venueMap.put(name, address);
			}
		}
	}

	private boolean isCATInstalled() {
		PackageManager pm = this.getActivity().getApplicationContext().getPackageManager();
		boolean installed = false;
		try {
			pm.getPackageInfo("com.cville.cattail", PackageManager.GET_ACTIVITIES);
			installed = true;
		} catch (Exception e) {
			installed = false;
		}
		return installed;
	}
}
