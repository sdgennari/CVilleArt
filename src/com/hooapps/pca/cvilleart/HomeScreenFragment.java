/*
 * Adapted from the online code samples found at:
 * http://developer.android.com/training/basics/fragments/index.html
 * 
 * Heavily modified for use within this specific app,
 * but always give credit where credit is due.
 */

package com.hooapps.pca.cvilleart;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

//TODO Update the JavaDoc description as the functionality increases

/**
* Fragment to greet first-time users to the app. This fragment pretty much acts
* as an enhanced splash screen and provides some initial navigation. [MORE 
* HERE]
* 
* @author Spencer Gennari
*/

public class HomeScreenFragment extends ListFragment {
	
	// A (temporary?) list of the views within the app
	static String[] Views = {
		"NearMe",
		"Discover",
		"Transportation",
		"Events"
	};
	
	// A (temporary?) list of description for the views within the app
	static String[] Desc = {
		"Investigate Local Art Near Your Current Location",
		"Explore Art Venues Around C-Ville",
		"Find Routes To and From Attractions",
		"Browse Upcoming Events"
	};
	
	OnHomeScreenViewSelectedListener mCallback;
	
	public interface OnHomeScreenViewSelectedListener {
		/**
		 * Called by HomeScreenFragment when a view is selected
		 * 
		 * @param position The position of the view in the list
		 * */
		public void onHomeScreenViewSelected(int position);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Use a preset android list item layout
		int layout = android.R.layout.simple_list_item_activated_1;
		
		// Create an array adapter for the list view
		setListAdapter(new ArrayAdapter<String>(getActivity(), layout, Views));
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		// Make sure that the activity implements OnViewSelectedListener
		// If not, throw an exception
		try {
			mCallback = (OnHomeScreenViewSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnViewSelectedListener");
		}
	}
	
	/**
	 * Called whenever an item in the list is selected
	 * 
	 * @param l	The parent ListView that detected the click
	 * @param v The child View that was clicked
	 * @param position The position of the View within the ListView
	 * @param id The row id of the View that was clicked  
	 */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// Notify the parent activity
		mCallback.onHomeScreenViewSelected(position);
	}

}
