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
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost.TabSpec;

import com.hooapps.pca.cvilleart.R;
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
	private FragmentTabHost tabHost;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// If the activity is being recreated, restore previous version
		// Mostly, this is needed in a two-pane layout
		if (savedInstanceState != null) {
			// TODO Code to restore prior version here  
		}
		
		
		View rootView = inflater.inflate(R.layout.transportation_view, container, false);
		
		tabHost = (FragmentTabHost) rootView.findViewById(R.id.tabhost);
		Log.d("Test","A");
		
		tabHost.setup(this.getActivity(),this.getChildFragmentManager(), android.R.id.tabcontent);
		Log.d("Test","B");
		
		
		TabSpec publicTrans = tabHost.newTabSpec("Public Transportation");
		publicTrans.setIndicator("Public Transportation");
		
		TabSpec parking = tabHost.newTabSpec("Parking");
		parking.setIndicator("Parking");
		
		TabSpec taxi = tabHost.newTabSpec("Taxi");
		taxi.setIndicator("Taxi");
		
		tabHost.addTab(publicTrans,TransportationPublicFragment.class, null);
		tabHost.addTab(parking, TransportationParkingFragment.class, null);
		tabHost.addTab(taxi, TransportationTaxiFragment.class, null);
		
		return rootView;
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
		tabHost = null;
	}
}
