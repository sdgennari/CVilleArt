package com.hooapps.pca.cvilleart;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

/**
 * Simple fragment that initially fills the Main Activity's fragment container
 * Its layout consists of an ImageView, and every time it is created it sets
 * its background to a picture whose id is stored in the images array.
 * No repetition of the same image twice in a row occurs.
 * 
 * @author Spencer Gennari
 * @author Alex Ramey
**/

public class HomeScreenFragment extends Fragment
{
	
	OnHomeScreenButtonSelectedListener mCallback;
	
	public enum Buttons {
		MAP, VENUES, EVENTS, TRANSPORTATION
	}
	
	public interface OnHomeScreenButtonSelectedListener {
		public void onHomeScreenButtonSelected(View v);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		setButtonListeners();
	}
	
	private void setButtonListeners() {
		Button map = (Button) this.getActivity().findViewById(R.id.map_button);
		map.setTag(Buttons.MAP);
		map.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mCallback.onHomeScreenButtonSelected(v);
			}
		});
		
		Button venues = (Button) this.getActivity().findViewById(R.id.venue_button);
		venues.setTag(Buttons.VENUES);
		venues.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mCallback.onHomeScreenButtonSelected(v);
			}
		});
		
		Button events = (Button) this.getActivity().findViewById(R.id.event_button);
		events.setTag(Buttons.EVENTS);
		events.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mCallback.onHomeScreenButtonSelected(v);
			}
		});
		
		Button transportation = (Button) this.getActivity().findViewById(R.id.transportation_button);
		transportation.setTag(Buttons.TRANSPORTATION);
		transportation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mCallback.onHomeScreenButtonSelected(v);
			}
		});
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set the background color for the ActionBar
		//ActionBar bar = this.getActivity().getActionBar();
		//bar.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.orange_bg));
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			mCallback = (OnHomeScreenButtonSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnButtonSelectedListener");
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
		return inflater.inflate(R.layout.home_screen_fragment, container, false);
	}
}
