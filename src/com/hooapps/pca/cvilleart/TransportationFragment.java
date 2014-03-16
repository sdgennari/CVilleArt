/*
 * Adapted from the online code samples found at:
 * http://developer.android.com/training/basics/fragments/index.html
 * 
 * Heavily modified for use within this specific app,
 * but always give credit where credit is due.
 */
package com.hooapps.pca.cvilleart;

import java.util.HashMap;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TabHost.TabSpec;

import com.hooapps.pca.cvilleart.R;
import com.hooapps.pca.cvilleart.DataElems.PCAContentProvider;
import com.hooapps.pca.cvilleart.DataElems.VenueTable;
import com.hooapps.pca.cvilleart.Transportation.TransportationParkingFragment;
import com.hooapps.pca.cvilleart.Transportation.TransportationPublicFragment;
import com.hooapps.pca.cvilleart.Transportation.TransportationTaxiFragment;

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
	
	private static final String GOOGLE_MAP_BASE = "http://maps.google.com/maps?";
	private static final String GOOGLE_MAP_START = "saddr=";
	private static final String GOOGLE_MAP_END = "daddr=";
	
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
		
		return inflater.inflate(R.layout.transportation_view, container, false);
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
		
		loadVenueMap();
		bindListeners();
		
		
	}
	
	private void bindListeners() {
		Button getDirectionsButton = (Button) this.getActivity().findViewById(R.id.get_directions_button);
		getDirectionsButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String startAddress = startAutoComp.getText().toString();
				String endAddress = endAutoComp.getText().toString();
				
				createGoogleMapIntent(startAddress, endAddress);
			}
		});
		
		// Prepare the autocomplete textviews
		startAutoComp = (AutoCompleteTextView) this.getActivity().findViewById(R.id.start_auto_comp);
		endAutoComp = (AutoCompleteTextView) this.getActivity().findViewById(R.id.destination_auto_comp);
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
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		// TODO Save important info form the fragment here
		// Use the format 'outState.put[String/Boolean/Int/etc.](key, value);'
	}
	
	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		//tabHost = null;
	}
	
	private void createGoogleMapIntent(String startAddress, String endAddress) {
		
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
		
		Log.d("TRANS", uri);
		
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
}
