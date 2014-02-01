package com.hooapps.pca.cvilleart.Transportation;

import com.hooapps.pca.cvilleart.R;
import com.hooapps.pca.cvilleart.R.id;
import com.hooapps.pca.cvilleart.R.layout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost.TabSpec;
/**
 * Loaded in when the appropriate tab is selected in the Transportation Fragment.
 * This Fragment itself contains two tabs at the bottom of its layout that load in
 * appropriate fragments for this section.
 * Tier 2 Fragment
 * @author Alex Ramey
*/
public class TransportationParkingFragment extends Fragment 
{
	private FragmentTabHost tabHost;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// If the activity is being recreated, restore previous version
		// Mostly, this is needed in a two-pane layout
		if (savedInstanceState != null) {
			// TODO Code to restore prior version here  
		}
		
		View rootView = inflater.inflate(R.layout.transportation_tab_view_layout, container, false);
		
		tabHost = (FragmentTabHost) rootView.findViewById(R.id.public_trans_tabhost);
		
		tabHost.setup(this.getActivity(),this.getChildFragmentManager(), android.R.id.tabcontent);
		
	
		TabSpec publicMap = tabHost.newTabSpec("Map");
		publicMap.setIndicator("Map");
		
		TabSpec publicInfo = tabHost.newTabSpec("Info");
		publicInfo.setIndicator("Info");
		
		tabHost.addTab(publicMap, TransportationParkingMapFragment.class, null);
		tabHost.addTab(publicInfo,TransportationParkingInfoFragment.class, null);
		
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
	public void onDestroyView()
	{
		super.onDestroyView();
		tabHost = null;
	}
}
