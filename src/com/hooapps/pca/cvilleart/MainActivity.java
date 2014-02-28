/*
 * Adapted from the online code samples found at:
 * http://developer.android.com/training/basics/fragments/index.html
 * 
 * Heavily modified for use within this specific app,
 * but always give credit where credit is due.
 */
package com.hooapps.pca.cvilleart;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import jxl.*;

import com.hooapps.pca.cvilleart.DataElems.AlarmReceiver;
import com.hooapps.pca.cvilleart.DataElems.EventTable;
import com.hooapps.pca.cvilleart.DataElems.PCAContentProvider;
import com.hooapps.pca.cvilleart.DataElems.VenueTable;
import com.hooapps.pca.cvilleart.ListViewElems.ArtVenue;
import com.hooapps.pca.cvilleart.ListViewElems.BookmarkItem;
import com.hooapps.pca.cvilleart.ListViewElems.HeaderItem;
import com.hooapps.pca.cvilleart.ListViewElems.Item;
import com.hooapps.pca.cvilleart.ListViewElems.ItemArrayAdapter;
import com.hooapps.pca.cvilleart.ListViewElems.TextItem;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
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

public class MainActivity extends FragmentActivity implements
		DiscoverListFragment.OnDiscoverViewSelectedListener,
		EventListFragment.OnEventViewSelectedListener,
		HomeScreenFragment.OnHomeScreenButtonSelectedListener,
		AsyncExcelLoader.AsyncExcelLoaderListener,
		AsyncJSONLoader.AsyncJSONLoaderListener {
	
	//private String path = "http://people.virginia.edu/~sdg6vt/CVilleArt/PCA_Data.json";
	private String musicPath =		"https://www.googleapis.com/calendar/v3/calendars/charlottesvillearts.org_9oapvu67eckm7hkbm22p8debtc%40group.calendar.google.com/events?singleEvents=true&timeMax=2014-02-28T11%3A59%3A00Z&timeMin=2014-02-19T00%3A00%3A00Z&key=AIzaSyDegSazDw-VcXQtWyVDmsDiV-xgwaT9ijE";
	private String theatrePath =	"https://www.googleapis.com/calendar/v3/calendars/charlottesvillearts.org_ob2g1r475vou79aa2piljkivm0%40group.calendar.google.com/events?singleEvents=true&timeMax=2014-02-28T11%3A59%3A00Z&timeMin=2014-02-19T00%3A00%3A00Z&key=AIzaSyDegSazDw-VcXQtWyVDmsDiV-xgwaT9ijE";
	private String filmPath = 		"https://www.googleapis.com/calendar/v3/calendars/charlottesvillearts.org_gmbfku7u83glhstgll6p4ikeh4%40group.calendar.google.com/events?singleEvents=true&timeMax=2014-02-28T11%3A59%3A00Z&timeMin=2014-02-19T00%3A00%3A00Z&key=AIzaSyDegSazDw-VcXQtWyVDmsDiV-xgwaT9ijE";
	private String dancePath = 		"https://www.googleapis.com/calendar/v3/calendars/charlottesvillearts.org_6j3aq5pd2t3ikhm4ms563h5hrs%40group.calendar.google.com/events?singleEvents=true&timeMax=2014-02-28T11%3A59%3A00Z&timeMin=2014-02-19T00%3A00%3A00Z&key=AIzaSyDegSazDw-VcXQtWyVDmsDiV-xgwaT9ijE";
	private String galleryPath = 	"https://www.googleapis.com/calendar/v3/calendars/charlottesvillearts.org_fci03o8i70o7ugjtchqll39ck0%40group.calendar.google.com/events?singleEvents=true&timeMax=2014-02-28T11%3A59%3A00Z&timeMin=2014-02-19T00%3A00%3A00Z&key=AIzaSyDegSazDw-VcXQtWyVDmsDiV-xgwaT9ijE";
	
	private DrawerLayout drawerLayout;
	private ListView leftNavDrawerList;
	private ListView rightNavDrawerList;
	private Integer lastBackgroundImage;
	private ArrayList<Item> venueItemList;
	
	private Uri venueUri;

	public Integer getLastBackgroundImage() {
		return lastBackgroundImage;
	}

	public void setLastBackgroundImage(Integer lastBackgroundImage) {
		this.lastBackgroundImage = lastBackgroundImage;
	}
	
	public ArrayList<Item> getVenueItemList()
	{
		if (venueItemList != null)
			return venueItemList;
		else
			return null;
	}

	/** Called when the app is first opened */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Make sure to set the theme here, before loading any views
		// this.setTheme(R.style.LightTheme);

		setContentView(R.layout.home_screen);

		// *****
		// TODO CITE THIS IMAGE LOADER AND EMAIL AUTHOR ON GITHUB AS PER
		// INSTRUCTIONS
		// *****
		// Initialize the image loader for async loading within the app
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext()).build();
		ImageLoader.getInstance().init(config);

		// Make sure that the home screen contains the fragment_container
		if (findViewById(R.id.fragment_container) != null) {

			// When device is rotated, Android destroys activity and then
			// recreates it.
			// Therefore, it is important to properly initialize the Nav Drawers
			// every time onCreate() is called.
			// To accomplish this, the following 3 lines of code needed to be
			// placed before the savedInstanceState
			// if statement, which returns out of onCreate() before the Nav
			// Drawers are ready to go after device
			// rotation, since savedInstanceState is not null in that scenario
			drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
			initializeLeftNavDrawer();
			initializeRightNavDrawer();

			// Only create new fragments if the app is not being restored
			// This prevents multiple, identical fragments from stacking up
			if (savedInstanceState != null) {
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
		
		//Will only execute when savedInstanceState == null
		/*
		AsyncExcelLoader excelLoader = new AsyncExcelLoader();
		excelLoader.setActivity(this);
		excelLoader.execute(new File(this.getFilesDir(),"PCAExcelData"));
		*/
		AsyncJSONLoader JSONLoader = new AsyncJSONLoader();
		JSONLoader.setActivity(this);
		//JSONLoader.execute(musicPath);
		//JSONLoader.execute(theatrePath);
		//JSONLoader.execute(filmPath);
		//JSONLoader.execute(dancePath);
		JSONLoader.execute(galleryPath);
		
		// Schedule the services to update the data
		//Will only execute when savedInstanceState == null
		scheduleServices();
	}

	/**
	 * Creates the items for the leftNavDrawer and places them inside of the
	 * custom ItemArrayAdapter. This adapter is then set as the adapter for the
	 * ListView.
	 */
	public void initializeLeftNavDrawer() {
		leftNavDrawerList = (ListView) findViewById(R.id.left_drawer);

		// Initialize the List of items for the ItemArrayAdapter
		List<Item> items = new ArrayList<Item>();
		items.add(new HeaderItem("Favorites"));
		//TODO: Write BookmarkItem Constructor to receive list of image URLs to display
		//Use sharedPreferences to keep track of what urls the user wants and what discoverItemFragment to display upon click
		//Make it easy to add/remove bookmarks
		//have a better default image
		items.add(new BookmarkItem());
		
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
		leftNavDrawerList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				onLeftDrawerViewSelected(position);
				drawerLayout.closeDrawer(leftNavDrawerList);
			}
		});
	}

	private void bookmark2setOnClickListener(MainActivity mainActivity) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Creates the items for the rightNavDrawer and places them inside of the
	 * custom ItemArrayAdapter. This adapter is then set as the adapter for the
	 * ListView.
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
		rightNavDrawerList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
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
	 * @param position
	 *            The position of the item selected in the HomeScreenFragment
	 *            list
	 */
	public void onLeftDrawerViewSelected(int position) {
		// Create a fragment based on the item that was clicked and swap to that
		// fragment
		// TODO Make sure that this is not stacking up fragments within the
		// code...
		// TODO Make this more dynamic (maybe?). I'd like to believe there is a
		// better way...
		Fragment newFragment = null;
		Bundle args = null;
		switch (position) {
		// Home
		case 3:
			newFragment = new HomeScreenFragment();
			args = new Bundle();
			break;
		// NearMeFragment
		case 7:
			newFragment = new NearMeFragment();
			args = new Bundle();
			// TODO Pass all relevant info to the fragment via args
			break;
		// DiscoverFragment
		case 8:
			newFragment = new DiscoverListFragment();
			args = new Bundle();
			// TODO Pass all relevant info to the fragment via args
			break;
		// TransportationFragment
		case 9:
			newFragment = new TransportationFragment();
			args = new Bundle();
			// TODO Pass all relevant info to the fragment via args
			break;
		// EventFragment
		case 10:
			newFragment = new EventListFragment();
			args = new Bundle();
			// TODO Pass all relevant info to the fragment via args
			break;

		}
		
		launchFragment(newFragment, args);
	}

	public void onRightDrawerViewSelected(int position) {
		switch (position) {
		// TODO: add functionality
		}
	}
	
	public void onHomeScreenButtonSelected(View v) {
		Fragment newFragment = null;
		Bundle args = new Bundle();
		
		switch((HomeScreenFragment.Buttons) v.getTag()) {
		case MAP:
			newFragment = new NearMeFragment();
			break;
		case VENUES:
			newFragment = new DiscoverListFragment();
			break;
		case EVENTS:
			newFragment = new EventListFragment();
			break;
		case TRANSPORTATION:
			newFragment = new TransportationFragment();
			break;
		}
		
		launchFragment(newFragment, args);
	}
	
	private void launchFragment(Fragment newFragment, Bundle args) {
		// Abort the mission if the fragment or args is still null to avoid
		// NullPointerException
		if (newFragment == null || args == null) {
			return;
		}

		newFragment.setArguments(args);
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

		// Replace the fragment in fragment_container with the new fragment
		// Add the transaction to the back stack to allow for navigation with
		// the back button

		transaction.replace(R.id.fragment_container, newFragment);
		// NOTE: This is fine as is. According to the API, this doesn't replace
		// the fragment_container. Instead, it replaces
		// all the fragments in the given container with the newFragment. In
		// other words, it removes everything in the container
		// and then puts the given fragment in the container, so all the
		// oldFragmnet nonsense I put in here was not necessary
		transaction.addToBackStack(null);

		// Commit the transaction
		transaction.commit();
	}
	
	/**
	 * Called when a user selects an item in the DiscoverFragment. This list is
	 * dynamic and needs to be handled accordingly. When an item is clicked, a
	 * new fragment with information about the attraction is displayed on the
	 * screen.
	 * 
	 * @param position
	 *            The position of the item selected in the DiscoverFragment list
	 */
	public void onDiscoverViewSelected(ListView l, View v, int position, long id) {
		// Create a fragment based on the item that was clicked and swap to that
		// fragment
		// TODO Make sure that this is not stacking up fragments within the
		// code...
		Fragment newFragment = new DiscoverItemFragment();
		Bundle args = new Bundle();
		
		Uri venueUri = Uri.parse(PCAContentProvider.VENUE_CONTENT_URI+"/"+id);
		args.putParcelable(PCAContentProvider.VENUE_CONTENT_ITEM_TYPE, venueUri);
		
		launchFragment(newFragment, args);
		/*
		newFragment.setArguments(args);
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

		// Replace the fragment in fragment_container with the new fragment
		// Add the transaction to the back stack to allow for navigation with
		// the back button
		transaction.replace(R.id.fragment_container, newFragment);
		transaction.addToBackStack(null);

		// Commit the transaction
		transaction.commit();
		*/
	}
	
	public void OnEventViewSelected(ListView l, View v, int position, long id) {
		Fragment newFragment = new EventItemFragment();
		Bundle args = new Bundle();
		
		Uri eventUri = Uri.parse(PCAContentProvider.EVENT_CONTENT_URI+"/"+id);
		args.putParcelable(PCAContentProvider.EVENT_CONTENT_ITEM_TYPE, eventUri);
		
		launchFragment(newFragment, args);
		/*
		newFragment.setArguments(args);
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		
		transaction.replace(R.id.fragment_container, newFragment);
		transaction.addToBackStack(null);
		
		transaction.commit();
		*/
	}

	/**
	 * Helper method to schedule services at the appropriate time to update the
	 * database.
	 */
	private void scheduleServices() {
		/*
		Context context = this.getApplicationContext();
		Intent intent = new Intent(context, AlarmReceiver.class);
		PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0,
				intent, 0);
		AlarmManager alarmMgr = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);

		// Set the alarm to a specific time (00:00:00 in this case)
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);

		// Repeat the alarm every day
		alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP,
				calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY,
				alarmIntent);

		// Repeat the alarm every minute to debug
		// alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP,
		// calendar.getTimeInMillis(), 60*1000, alarmIntent);
		*/
	}
	
	public void storeJSONData(String result) {
		try {
			JSONObject jObject = new JSONObject(result);
			String category = jObject.getString("summary");
			JSONArray jArray = jObject.getJSONArray("items");
			ContentValues values = new ContentValues();
			String[] id = new String[1];
			JSONObject event;
			Uri eventUri;
			String start = "";
			String end = "";
			
			for(int i = 0; i < jArray.length(); i++) {
				event = jArray.getJSONObject(i);
				values.clear();
				
				// Populate the ContentValues
				values.put(EventTable.EVENT_ID, event.getString("id"));
				values.put(EventTable.UPDATED, event.getString("updated")); // TODO CONVERT TO UNIX TIME
				values.put(EventTable.SUMMARY, event.getString("summary"));
				
				if(event.has("description")) {
					values.put(EventTable.DESCRIPTION, event.getString("description"));
				}
				
				values.put(EventTable.LOCATION, event.getString("location"));
				values.put(EventTable.START_TIME, parseUnixFromDate(event.getJSONObject("start").getString("dateTime")));
				values.put(EventTable.END_TIME, parseUnixFromDate(event.getJSONObject("end").getString("dateTime")));
				values.put(EventTable.CATEGORY, category);
				
				// Check to see if the item is already in the database
				id[0] = values.getAsString(EventTable.EVENT_ID);
				Cursor c = getContentResolver().query(PCAContentProvider.EVENT_CONTENT_URI, null, 
						EventTable.EVENT_ID+" = "+DatabaseUtils.sqlEscapeString(id[0]), null, null);
				
				// Insert if new object, otherwise update
				if (c.getCount() == 0) {
					eventUri = getContentResolver().insert(PCAContentProvider.EVENT_CONTENT_URI, values);
					Log.d("storeJSONData", "JSON Event added to "+id);
				} else {
					getContentResolver().update(PCAContentProvider.EVENT_CONTENT_URI, values, EventTable.EVENT_ID+" = ?", id);
					Log.d("storeJSONData", "JSON Event updated: " + id);
				}
				
				c.close();
				
			}
		} catch (JSONException e) {
			Log.d("storeJSONData", "Error: " + e.getLocalizedMessage());
		}
	}
	
	private int parseUnixFromDate(String date) {
		// DATE FORM 2014-01-19T04:00:00-05:00
		int year = Integer.parseInt(date.substring(0, 4));
		int month = Integer.parseInt(date.substring(5,7));
		int day = Integer.parseInt(date.substring(8, 10));
		int hour = Integer.parseInt(date.substring(11, 13));
		int minute = Integer.parseInt(date.substring(14, 16));
		int timeZoneMod = Integer.parseInt(date.substring(19,21));
		
		Calendar c = Calendar.getInstance();
		c.set(year, month-1, day, hour-timeZoneMod, minute);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		
		int unixTime = (int) (c.getTimeInMillis() / 1000L);
		
		Log.d("TIME", date);
		Log.d("TIME", year + " " + month + " " + day + " " + hour + " " + minute);
		Log.d("TIME", ""+unixTime);
		
		return unixTime;
	}
	
	/*
	public void storeJSONData(String result) {
		try {
			JSONArray jArray = new JSONArray(result);
			ContentValues values;
			JSONObject jObject;
			String[] name = new String[1];
			for (int i = 0; i < jArray.length(); i++) {
				jObject = jArray.getJSONObject(i);
				values = new ContentValues();
				
				// Populate the ContentValues
				values.put(VenueTable.ORGANIZATION_NAME, jObject.getString("Organization_Name"));
				values.put(VenueTable.EMAIL_ADDRESS, jObject.getString("Email_Address"));
				values.put(VenueTable.HOME_PAGE_URL, jObject.getString("Home_Page_URL"));
				values.put(VenueTable.DIRECTORY_DESCRIPTION_LONG, jObject.getString("Directory_Description_Long"));
				values.put(VenueTable.PHONE_NUMBER_PRIMARY, jObject.getString("Phone_Number_Primary"));
				values.put(VenueTable.ADDRESS_HOME_STREET, jObject.getString("Address_Home_Street"));
				values.put(VenueTable.ADDRESS_HOME_CITY, jObject.getString("Address_Home_City"));
				values.put(VenueTable.ADDRESS_HOME_POSTAL_CODE, jObject.getString("Address_Home_Postal_Code"));
				values.put(VenueTable.ADDRESS_HOME_STATE, jObject.getString("Address_Home_State"));
				values.put(VenueTable.CATEGORY_ART_COMMUNITY_CATEGORIES, jObject.getString("Category_Art_Community_Categories"));
				values.put(VenueTable.SECONDARY_CATEGORY, jObject.getString("Secondary Category"));
				values.put(VenueTable.LAT_LNG_STRING, jObject.getString("LatLngString ifNoAddress"));
				values.put(VenueTable.IMAGE_URLS, jObject.getString("Image URLs"));
				
				// Check to see if the item is already in the database
				name[0] = values.getAsString(VenueTable.ORGANIZATION_NAME);
				Cursor c = getContentResolver().query(PCAContentProvider.VENUE_CONTENT_URI, null, 
						VenueTable.ORGANIZATION_NAME+" = "+DatabaseUtils.sqlEscapeString(name[0]), null, null);
				
				// Insert if new object, otherwise update
				if (c.getCount() == 0) {
					venueUri = getContentResolver().insert(PCAContentProvider.VENUE_CONTENT_URI, values);
					Log.d("storeJSONData", "JSON Venue added to "+venueUri);
				} else {
					getContentResolver().update(PCAContentProvider.VENUE_CONTENT_URI, values, VenueTable.ORGANIZATION_NAME+" = ?", name);
					Log.d("storeJSONData", "JSON Venue updated: " + name);
				}
				
				c.close();
			}
		} catch (JSONException e) {
			Log.d("storeJSONData", "Error: " + e.getLocalizedMessage());
		}
	}
	*/
	public void loadInData(File f)
	{
		
		Log.d("Error",f.length()+"");
		venueItemList = new ArrayList<Item>();
		//File excelTable = new File("/Users/alexramey/Desktop/test");
		
		Workbook workbook = null;
		try{
			workbook = Workbook.getWorkbook(f);
		}
		catch (Exception e)
		{
			Log.d("Error", e.toString());
		}
		
		if (workbook == null)
		{
			Log.d("Error", "null workbook");
			return;
		}
		Sheet sheet = workbook.getSheet(0);
		
		ArrayList<String> stringHolder = new ArrayList<String>();
		for (int row = 1; row < sheet.getRows(); row++)
		{
			//In case blank rows at end are being counted by getRows() method
			//Breaks once row is encountered with first cell blank
			if (sheet.getCell(0,row).getContents().trim().equals("")){
				break;
			}
			for (int col = 0; col < 13; col++)
			{
				Cell temp = sheet.getCell(col, row);
				stringHolder.add(temp.getContents().trim());
			}
			
			ContentValues values = new ContentValues();
			values.put(VenueTable.ORGANIZATION_NAME, stringHolder.get(0));
			values.put(VenueTable.EMAIL_ADDRESS, stringHolder.get(1));
			values.put(VenueTable.HOME_PAGE_URL, stringHolder.get(2));
			values.put(VenueTable.DIRECTORY_DESCRIPTION_LONG, stringHolder.get(3));
			values.put(VenueTable.PHONE_NUMBER_PRIMARY, stringHolder.get(4));
			values.put(VenueTable.ADDRESS_HOME_STREET, stringHolder.get(5));
			values.put(VenueTable.ADDRESS_HOME_CITY, stringHolder.get(6));
			values.put(VenueTable.ADDRESS_HOME_POSTAL_CODE, stringHolder.get(7));
			values.put(VenueTable.ADDRESS_HOME_STATE, stringHolder.get(8));
			values.put(VenueTable.CATEGORY_ART_COMMUNITY_CATEGORIES, stringHolder.get(9));
			values.put(VenueTable.SECONDARY_CATEGORY, stringHolder.get(10));
			values.put(VenueTable.LAT_LNG_STRING, stringHolder.get(11));
			values.put(VenueTable.IMAGE_URLS, stringHolder.get(12));
			
			venueUri = getContentResolver().insert(PCAContentProvider.VENUE_CONTENT_URI, values);
			
			Log.d("MAIN ACTIVITY", "Venue added to "+venueUri);
			stringHolder.clear();
		}
		workbook.close();
	}

	public void onBookmarkClick(View v) {
		int id = v.getId();
		if (id == R.id.bookmark1)
			Log.d("Testing", "Image Button 1");
		else if (id == R.id.bookmark2)
			Log.d("Testing", "Image Button 2");
		else if (id == R.id.bookmark3)
			Log.d("Testing","Image Button 3");
		else if (id == R.id.bookmark4)
			Log.d("Testing","Image Button 4");
		else if (id == R.id.bookmark5)
			Log.d("Testing","Image Button 5");
		else if (id == R.id.bookmark6)
			Log.d("Testing","Image Button 6");
	}
}
