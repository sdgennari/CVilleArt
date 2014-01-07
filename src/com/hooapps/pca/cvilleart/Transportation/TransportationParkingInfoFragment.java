package com.hooapps.pca.cvilleart.Transportation;

import com.hooapps.pca.cvilleart.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
/**
 * Loaded in when the appropriate tab is selected on the TransportationParkingFragment.
 * Tier 3 Fragment
 * @author Alex Ramey
*/
public class TransportationParkingInfoFragment extends Fragment
{
	private TextView description;
	private static final String idea = "This should be a ListFragment. It will have a Location Client that"
			+ "notifies it of the user's location frequently. The List will be re-sorted, listing closest options first"
			+ "with each update. We will need to have a List of ParkingLocation Objects that have a LatLng field, then"
			+ "we can use the static Location.distanceBetween() method in a comparator class for sorting the list."
			+ "It will be a lot like the Discover Section. Each parking lot item will have a name, distance away, and "
			+ "info on rates/times. When a parking lot item in the list is clicked, it will launch"
			+ "a map fragment and pass it the LatLng of the ParkingLot with rates/times/info in the marker label";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// If the activity is being recreated, restore previous version
		// Mostly, this is needed in a two-pane layout
		if (savedInstanceState != null) {
			// TODO Code to restore prior version here  
		}
		
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.parking_info, container, false);
	}
	
	@Override
	public void onStart()
	{
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
		
		description = (TextView) this.getView().findViewById(R.id.description);
		description.setText(idea);
	}
}
