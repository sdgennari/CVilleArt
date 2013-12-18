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
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;

//TODO Update the JavaDoc description as the functionality increases

/**
* MainActivity that is run when the app is initially started. [MORE HERE]
* 
* @author Spencer Gennari
*/

public class MainActivity extends FragmentActivity
		implements HomeScreenFragment.OnViewSelectedListener{
	
	/** Called when the app is first opened */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);
        
        // Make sure that the home screen contains the fragment_container 
        if(findViewById(R.id.fragment_container) != null) {
        	
        	// Only create new fragments if the app is not being restored
        	// This prevents multiple, identical fragments from stacking up 
        	if(savedInstanceState != null) {
        		return;
        	}
        	
        	// Create the initial HomeScreenFragment
        	HomeScreenFragment firstFragment = new HomeScreenFragment();
        	
        	// If special instructions were provided by the Intend,
        	// pass them onto the HomeScreenFragment
        	firstFragment.setArguments(getIntent().getExtras());
        	
        	// Add the fragment to the fragment_container FrameLayout
        	getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, firstFragment).commit();
        }
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    /**
     * Called when a user selects an item in the HomeScreenFragment
     * 
     * @param position The position of the item selected in the HomeScreenFragment list
     */
    public void onViewSelected(int position) {
    	// Create a fragment based on the item that was clicked and swap to that fragment
    	// TODO Make sure that this is not stacking up fragments within the code...
    	// TODO Make this more dynamic (maybe?). I'd like to believe there is a better way...
    	Fragment newFragment = null;
    	Bundle args = null;
    	switch(position) {
    	// NearMeFragment
    	case 0: newFragment = new NearMeFragment();
    		args = new Bundle();
    		// TODO Pass all relevant info to the fragment via args
    		break;
    	// DiscoverFragment
    	case 1: newFragment = new DiscoverFragment();
			args = new Bundle();
			// TODO Pass all relevant info to the fragment via args
    		break;
    	// TransportationFragment
    	case 2: newFragment = new TransportationFragment();
			args = new Bundle();
			// TODO Pass all relevant info to the fragment via args
    		break;
    	// EventFragment
    	case 3: newFragment = new EventFragment();
			args = new Bundle();
			// TODO Pass all relevant info to the fragment via args
    		break;
    	}
    	
    	// Abort the mission if the fragment or args is still null to avoid NullPointerException
    	if (newFragment == null || args == null) {
    		return;
    	}
    	
    	newFragment.setArguments(args);
    	FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    	
    	// Replace the fragment in fragment_container with the new fragemnt
    	// Add the transaction to the back stack to allow for navigation with the back button
    	transaction.replace(R.id.fragment_container, newFragment);
    	transaction.addToBackStack(null);
    	
    	//Commit the transaction
    	transaction.commit();
    }
    
}
