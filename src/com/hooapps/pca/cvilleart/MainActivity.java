/*
 * Adapted from the online code samples found at:
 * http://developer.android.com/training/basics/fragments/index.html
 * 
 * Heavily modified for use within this specific app,
 * but always give credit where credit is due.
 */
package com.hooapps.pca.cvilleart;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.hooapps.pca.cvilleart.DataElems.AlarmReceiver;
import com.hooapps.pca.cvilleart.ListViewElems.HeaderItem;
import com.hooapps.pca.cvilleart.ListViewElems.Item;
import com.hooapps.pca.cvilleart.ListViewElems.ItemArrayAdapter;
import com.hooapps.pca.cvilleart.ListViewElems.TextItem;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
		implements DiscoverListFragment.OnDiscoverViewSelectedListener {
	
	private DrawerLayout drawerLayout;
	private ListView leftNavDrawerList;
	private ListView rightNavDrawerList;
	private Integer lastBackgroundImage;
	
	public Integer getLastBackgroundImage() {
		return lastBackgroundImage;
	}

	public void setLastBackgroundImage(Integer lastBackgroundImage) {
		this.lastBackgroundImage = lastBackgroundImage;
	}

	/** Called when the app is first opened */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Make sure to set the theme here, before loading any views
        //this.setTheme(R.style.LightTheme);
        
        setContentView(R.layout.home_screen);
        
        //*****
        // TODO CITE THIS IMAGE LOADER AND EMAIL AUTHOR ON GITHUB AS PER INSTRUCTIONS
        //*****
        // Initialize the image loader for async loading within the app
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).build();
        ImageLoader.getInstance().init(config);
        
        // Make sure that the home screen contains the fragment_container 
        if(findViewById(R.id.fragment_container) != null) {
        	
        	//When device is rotated, Android destroys activity and then recreates it.
        	//Therefore, it is important to properly initialize the Nav Drawers every time onCreate() is called.
        	//To accomplish this, the following 3 lines of code needed to be placed before the savedInstanceState
        	//if statement, which returns out of onCreate() before the Nav Drawers are ready to go after device
        	//rotation, since savedInstanceState is not null in that scenario
        	drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        	initializeLeftNavDrawer();
        	initializeRightNavDrawer();
        	
        	// Only create new fragments if the app is not being restored
        	// This prevents multiple, identical fragments from stacking up 
        	if(savedInstanceState != null) {
        		return;
        	}
        	
        	// Create the initial HomeScreenFragment
        	HomeScreenFragment firstFragment = new HomeScreenFragment();
        	
        	// If special instructions were provided by the Intent,
        	// pass them onto the HomeScreenFragment
        	firstFragment.setArguments(getIntent().getExtras());
        	
        	// Add the fragment to the fragment_container FrameLayout
        	getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, firstFragment).commit();
        }
        
        // Schedule the services to update the data
        scheduleServices();
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
    	items.add(new HeaderItem("Menu"));
    	items.add(new TextItem("Home"));
    	items.add(new TextItem("Bookmarks"));
    	items.add(new TextItem("Recent"));
    	
    	items.add(new HeaderItem("CVille Art"));
    	items.add(new TextItem("Near Me"));
    	items.add(new TextItem("Discover"));
    	items.add(new TextItem("Transportation"));
    	items.add(new TextItem("Events"));
    	
    	items.add(new HeaderItem("Community"));
    	items.add(new TextItem("Streams"));
    	items.add(new TextItem("Capture"));
    	
    	
    	
    	// Set the ItemArrayAdapter as the ListView adapter
    	ItemArrayAdapter adapter = new ItemArrayAdapter(this, items);
    	leftNavDrawerList.setAdapter(adapter);
    	
    	// Respond accordingly when an item is clicked
    	leftNavDrawerList.setOnItemClickListener(new OnItemClickListener(){
    		public void onItemClick(AdapterView<?> parent, View view, int position, long id){
    			onLeftDrawerViewSelected(position);
    			drawerLayout.closeDrawer(leftNavDrawerList);
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
    	items.add(new HeaderItem("Help"));
    	items.add(new TextItem("Help"));
    	items.add(new TextItem("Terms/Conditions"));
    	items.add(new TextItem("Report a Problem"));
    	items.add(new TextItem("Rate this App"));
    	items.add(new TextItem("Share this App"));
    	
    	items.add(new HeaderItem("Settings"));
    	items.add(new TextItem("Theme Color"));
    	items.add(new TextItem("Sounds"));
    	items.add(new TextItem("Default Home Screen"));
    	
    	// Set the ItemArrayAdapter as the ListView adapter
    	ItemArrayAdapter adapter = new ItemArrayAdapter(this, items);
    	rightNavDrawerList.setAdapter(adapter);
    	
    	// Respond accordingly when an item is clicked
    	rightNavDrawerList.setOnItemClickListener(new OnItemClickListener(){
    		public void onItemClick(AdapterView<?> parent, View view, int position, long id){
    			onRightDrawerViewSelected(position);
    			drawerLayout.closeDrawer(rightNavDrawerList);
    		}
    	});
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
    public void onLeftDrawerViewSelected(int position) {
    	// Create a fragment based on the item that was clicked and swap to that fragment
    	// TODO Make sure that this is not stacking up fragments within the code...
    	// TODO Make this more dynamic (maybe?). I'd like to believe there is a better way...
    	Fragment newFragment = null;
    	Bundle args = null;
    	switch(position) {
    	//Home
    	case 1: newFragment = new HomeScreenFragment();
		args = new Bundle();
		break;
    	// NearMeFragment
    	case 5: newFragment = new NearMeFragment();
    		args = new Bundle();
    		// TODO Pass all relevant info to the fragment via args
    		break;
    	// DiscoverFragment
    	case 6: newFragment = new DiscoverListFragment();
			args = new Bundle();
			// TODO Pass all relevant info to the fragment via args
    		break;
    	// TransportationFragment
    	case 7: newFragment = new TransportationFragment();
			args = new Bundle();
			// TODO Pass all relevant info to the fragment via args
    		break;
    	// EventFragment
    	case 8: newFragment = new EventFragment();
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
    	
    	// Replace the fragment in fragment_container with the new fragment
    	// Add the transaction to the back stack to allow for navigation with the back button
    	
    	transaction.replace(R.id.fragment_container, newFragment);
    	//NOTE: This is fine as is. According to the API, this doesn't replace the fragment_container. Instead, it replaces
    	//all the fragments in the given container with the newFragment. In other words, it removes everything in the container
    	//and then puts the given fragment in the container, so all the oldFragmnet nonsense I put in here was not necessary
    	transaction.addToBackStack(null);
    	
    	//Commit the transaction
    	transaction.commit();
    }
    
    public void onRightDrawerViewSelected(int position)
    {
    	switch(position){
    		//TODO: add functionality
    	}
    }
    
    
    /**
     * Called when a user selects an item in the DiscoverFragment. This list 
     * is dynamic and needs to be handled accordingly. When an item is clicked,
     * a new fragment with information about the attraction is displayed on the
     * screen.
     * 
     * @param position The position of the item selected in the DiscoverFragment list
     */
	public void onDiscoverViewSelected(ListView l, View v, int position, long id) {
    	// Create a fragment based on the item that was clicked and swap to that fragment
    	// TODO Make sure that this is not stacking up fragments within the code...
    	Fragment newFragment = new DiscoverItemFragment();
    	Bundle args = new Bundle();
    	
    	String name = (String) v.getTag(R.id.venue_title);
    	String type = (String) v.getTag(R.id.type);
    	String imagePath = (String) v.getTag(R.id.image);
    	String description = (String) v.getTag(R.id.description);
    	String address = (String) v.getTag(R.id.address);
    	
    	args.putString("title", name);
    	args.putString("type", type);
    	args.putString("imagePath", imagePath);
    	args.putString("description", description);
    	args.putString("address", address);
    	
    	newFragment.setArguments(args);
    	FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

    	// Replace the fragment in fragment_container with the new fragment
    	// Add the transaction to the back stack to allow for navigation with the back button
    	transaction.replace(R.id.fragment_container, newFragment);
    	transaction.addToBackStack(null);

    	//Commit the transaction
    	transaction.commit();
    }
	
	/**
	 * Helper method to schedule services at the appropriate time to update 
	 * the database.
	 */
	private void scheduleServices() {
		Context context = this.getApplicationContext();
		Intent intent = new Intent(context, AlarmReceiver.class);
		PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
		AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

		// Set the alarm to a specific time (00:00:00 in this case)
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		
		// Repeat the alarm every day
		alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
		
		// Repeat the alarm every minute to debug 
		//alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 60*1000, alarmIntent);
	}
}
