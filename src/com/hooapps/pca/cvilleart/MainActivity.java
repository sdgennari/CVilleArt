/*
 * Adapted from the online code samples found at:
 * http://developer.android.com/training/basics/fragments/index.html
 * 
 * Heavily modified for use within this specific app,
 * but always give credit where credit is due.
 */
package com.hooapps.pca.cvilleart;

import java.util.ArrayList;
import java.util.List;

import com.hooapps.pca.cvilleart.NavDrawer.Header;
import com.hooapps.pca.cvilleart.NavDrawer.Item;
import com.hooapps.pca.cvilleart.NavDrawer.ItemArrayAdapter;
import com.hooapps.pca.cvilleart.NavDrawer.TextItem;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.view.View;

//TODO Update the JavaDoc description as the functionality increases

/**
* MainActivity that is run when the app is initially started. The main activity
* creates the fragement_container as well as the left and right sliding drawers
* that house the navigation and option menus. OnItem[MORE HERE]
* 
* @author Spencer Gennari
* @author Alex Ramey
*/

public class MainActivity extends FragmentActivity
		implements HomeScreenFragment.OnViewSelectedListener{
	
	private DrawerLayout drawerLayout;
	private ListView leftNavDrawerList;
	private ListView rightNavDrawerList;
	private Fragment oldFragment;
	
	/** Called when the app is first opened */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Make sure to set the theme here, before loading any views
        //this.setTheme(R.style.LightTheme);
        
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
        	
        	// Initialize the NavDrawers
        	drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        	initializeLeftNavDrawer();
        	initializeRightNavDrawer();
        }
        
    }
    
    /**
     * Creates the items for the leftNavDrawer and places them inside of the 
     * custom ItemArrayAdapter. This adapter is then set as the adapter for 
     * the ListView.
     */
    public void initializeLeftNavDrawer() {
    	leftNavDrawerList = (ListView) findViewById(R.id.left_drawer);
    	
    	// Initialize the List of items for the ItemArrayAdapter
    	List<Item> items = new ArrayList<Item>();
    	items.add(new Header("Menu"));
    	items.add(new TextItem("Home"));
    	items.add(new TextItem("Bookmarks"));
    	items.add(new TextItem("Recent"));
    	
    	items.add(new Header("CVille Art"));
    	items.add(new TextItem("Near Me"));
    	items.add(new TextItem("Discover"));
    	items.add(new TextItem("Transportation"));
    	items.add(new TextItem("Events"));
    	
    	items.add(new Header("Community"));
    	items.add(new TextItem("Streams"));
    	items.add(new TextItem("Capture"));
    	
    	
    	
    	// Set the ItemArrayAdapter as the ListView adapter
    	ItemArrayAdapter adapter = new ItemArrayAdapter(this, items);
    	leftNavDrawerList.setAdapter(adapter);
    	leftNavDrawerList.setOnItemClickListener(new OnItemClickListener(){
    		public void onItemClick(AdapterView<?> parent, View view, int position, long id){
    			if (position > 4 && position < 9)
    			{
    				onViewSelected(position - 5);
    			}
    			else if (position == 1)
    			{
    				onViewSelected(4);
    			}
    		}
    	});
    }
    
    /**
     * Creates the items for the rightNavDrawer and places them inside of the 
     * custom ItemArrayAdapter. This adapter is then set as the adapter for 
     * the ListView.
     */
    public void initializeRightNavDrawer() {
    	rightNavDrawerList = (ListView) findViewById(R.id.right_drawer);
    	
    	// Initialize the List of items for the ItemArrayAdapter
    	List<Item> items = new ArrayList<Item>();
    	items.add(new Header("Help"));
    	items.add(new TextItem("Help"));
    	items.add(new TextItem("Terms/Conditions"));
    	items.add(new TextItem("Report a Problem"));
    	items.add(new TextItem("Rate this App"));
    	items.add(new TextItem("Share this App"));
    	
    	items.add(new Header("Settings"));
    	items.add(new TextItem("Theme Color"));
    	items.add(new TextItem("Sounds"));
    	items.add(new TextItem("Default Home Screen"));
    	
    	// Set the ItemArrayAdapter as the ListView adapter
    	ItemArrayAdapter adapter = new ItemArrayAdapter(this, items);
    	rightNavDrawerList.setAdapter(adapter);
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
    	case 4: newFragment = new HomeScreenFragment();
    		args = new Bundle();
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
    	
    	if (oldFragment != null) //it will be null the first time
    	transaction.remove(oldFragment);//prevents fragments from stacking up. Is this unnecessary?
    	
    	oldFragment = newFragment;
    	transaction.replace(R.id.fragment_container, newFragment); //Is fragment_container redrawn when oldFragment is removed?
    	transaction.addToBackStack(null);
    	
    	//Commit the transaction
    	transaction.commit();
    }
    
}
