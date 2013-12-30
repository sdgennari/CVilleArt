/*
 * Adapted from the online code samples found at:
 * http://developer.android.com/training/basics/fragments/index.html
 * 
 * Heavily modified for use within this specific app,
 * but always give credit where credit is due.
 */
package com.hooapps.pca.cvilleart;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.hooapps.pca.cvilleart.R;

// TODO Update the JavaDoc description as the functionality increases

/**
 * Fragment to provide information about various transportation routes to and
 * from art venues. This Fragment has a TabHost which contains a TabWidget wrapped
 * in a HorizontalScrollView that holds tabs and a FrameLayout in which tab content is displayed.
 * Tabs are added by invoking TabHost.addTab(TabSpec), where the TabSpec defines
 * the tab's tag that is passed to the TabContentFactory's public View createTabContent(String tag)
 * method, the tab's content (an instance of a TabContentFactory), and the tab's 
 * indicator (String Label).
 * 
 * @author Spencer Gennari
 * @author Alex Ramey
 *
 */

public class TransportationFragment extends Fragment {
	private TabHost tabHost;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// If the activity is being recreated, restore previous version
		// Mostly, this is needed in a two-pane layout
		if (savedInstanceState != null) {
			// TODO Code to restore prior version here  
		}
		
		// Inflate the layout for this fragment
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
		
		tabHost = (TabHost) this.getView().findViewById(R.id.tabhost);
		TransportationViewFinder finder = new TransportationViewFinder(LayoutInflater.from(this.getActivity()));
		
		TabSpec publicTrans = tabHost.newTabSpec("public");
		publicTrans.setIndicator("Public Transportation");
		publicTrans.setContent(finder);
		
		TabSpec parking = tabHost.newTabSpec("parking");
		parking.setIndicator("Parking");
		parking.setContent(finder);
		
		TabSpec taxi = tabHost.newTabSpec("taxi");
		taxi.setIndicator("Taxi");
		taxi.setContent(finder);
		
		TabSpec bike = tabHost.newTabSpec("bike");
		bike.setIndicator("Bike Rental");
		bike.setContent(finder);
		
		tabHost.setup();
		tabHost.addTab(publicTrans);
		tabHost.addTab(parking);
		tabHost.addTab(taxi);
		tabHost.addTab(bike);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		// TODO Save important info form the fragment here
		// Use the format 'outState.put[String/Boolean/Int/etc.](key, value);'
	}
}
