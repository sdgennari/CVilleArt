package com.hooapps.pca.cvilleart;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Simple fragment that initially fills the Main Activity's fragment container
 * Its layout consists of an ImageView, and every time it is created it sets
 * its background to a picture whose id is stored in the images array.
 * No repetition of the same image twice in a row occurs.
 * 
 * @author Alex Ramey
**/

public class HomeScreenFragment extends Fragment
{
	private final int[] images = 
		{
			R.drawable.homescreen1, R.drawable.homescreen2, R.drawable.homescreen3
		};
	
	private ImageView background;
	private int imageIndex;
	MainActivity parentActivity;
	
	//Uses parentActivity to store last imageIndex and reads from that to make
	//sure the same image isn't repeated twice.
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (parentActivity == null)
		{
			FragmentActivity parent = this.getActivity();
			if (parent instanceof MainActivity)
			{
				this.parentActivity = (MainActivity) parent;
			}
		}
		
		imageIndex = (int)(Math.random()*images.length);
		
		if (parentActivity.getLastBackgroundImage() != null){
			int lastImage = parentActivity.getLastBackgroundImage();
			while (lastImage == imageIndex)
			{
				imageIndex = (int)(Math.random()*images.length);
			}
		}
		
		parentActivity.setLastBackgroundImage(imageIndex);
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
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		background = (ImageView)(this.getView().findViewById(R.id.homesceen_background));
		
		background.setBackgroundResource(images[imageIndex]);
		
	}
}
