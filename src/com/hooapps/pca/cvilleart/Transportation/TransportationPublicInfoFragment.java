package com.hooapps.pca.cvilleart.Transportation;

import com.hooapps.pca.cvilleart.R;
import com.hooapps.pca.cvilleart.R.id;
import com.hooapps.pca.cvilleart.R.layout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.widget.TextView;
/**
 * Loaded in when the appropriate tab is selected on the TransportationPublicFragment.
 * Tier 3 Fragment
 * @author Alex Ramey
*/
public class TransportationPublicInfoFragment extends Fragment 
{
	private TextView description;
	private static final String idea = "Load in information about CAT and maybe UTS; Link to sources for current info";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// If the activity is being recreated, restore previous version
		// Mostly, this is needed in a two-pane layout
		if (savedInstanceState != null) {
			// TODO Code to restore prior version here  
		}
		
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.public_info, container, false);
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
