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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hooapps.pca.cvilleart.DataElems.AlarmScheduleReceiver;
import com.hooapps.pca.cvilleart.DataElems.AsyncDataLoader;
import com.hooapps.pca.cvilleart.DataElems.DataIntentService;
import com.hooapps.pca.cvilleart.DataElems.EventTable;
import com.hooapps.pca.cvilleart.DataElems.ImageUtils;
import com.hooapps.pca.cvilleart.DataElems.PCAContentProvider;
import com.hooapps.pca.cvilleart.DataElems.VenueTable;
import com.hooapps.pca.cvilleart.ListViewElems.ArtVenue;
import com.hooapps.pca.cvilleart.ListViewElems.BookmarkItem;
import com.hooapps.pca.cvilleart.ListViewElems.HeaderItem;
import com.hooapps.pca.cvilleart.ListViewElems.Item;
import com.hooapps.pca.cvilleart.ListViewElems.ItemArrayAdapter;
import com.hooapps.pca.cvilleart.ListViewElems.TextItem;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
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
		NearMeFragment.OnInfoWindowSelectedListener,
		DiscoverItemFragment.OnDiscoverItemSelectedOpenMapListener {
	
	private final int MILLI_PER_FOUR_WEEK = 3 * 7 * 24 * 60 * 60 * 1000;
	private final String BASE_PATH = "https://www.googleapis.com/calendar/v3/calendars/";
	private final String TIME_MAX = "/events?singleEvents=true&timeMax=";
	private final String TIME_MIN = "T00%3A00%3A00Z&timeMin=";
	private final String API_KEY = "T00%3A00%3A00Z&key=AIzaSyDegSazDw-VcXQtWyVDmsDiV-xgwaT9ijE";
	
	private static final String IS_FIRST_LAUNCH = "is_first_launch";
	public static final String LAST_DATA_UPDATE = "last_data_update";
	private static final int MILLIS_PER_DAY = 24 * 60 * 60 * 1000;
	
	//private String venuePath 	=	"http://people.virginia.edu/~sdg6vt/CVilleArt/PCA_Data.json";
	private String venuePath = "http://charlottesvillearts.org/wp-content/uploads/2014/03/CVilleArtsData.txt";
	private String familyBasePath	=	"charlottesvillearts.org_1d75dtbvjd8adgei0borv0dp30@group.calendar.google.com";
	private String musicBasePath	=	"charlottesvillearts.org_9oapvu67eckm7hkbm22p8debtc@group.calendar.google.com";
	private String theatreBasePath	=	"charlottesvillearts.org_ob2g1r475vou79aa2piljkivm0@group.calendar.google.com";
	private String filmBasePath		= 	"charlottesvillearts.org_gmbfku7u83glhstgll6p4ikeh4@group.calendar.google.com";
	private String danceBasePath	= 	"charlottesvillearts.org_6j3aq5pd2t3ikhm4ms563h5hrs@group.calendar.google.com";
	private String galleryBasePath	= 	"charlottesvillearts.org_fci03o8i70o7ugjtchqll39ck0@group.calendar.google.com";
	private String literaryBasePath	=	"charlottesvillearts.org_1nvlsks9klme3evsf1cqhe2i64@group.calendar.google.com";
	
	
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
		
		// Initialize the ImageUtils
		ImageUtils.initialize(getApplicationContext(),
				getExternalFilesDir(ImageUtils.THUMB_PATH),
				getExternalFilesDir(ImageUtils.BLUR_PATH));

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
			//initializeRightNavDrawer();

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
		//AsyncJSONLoader JSONLoader = new AsyncJSONLoader();
		//JSONLoader.setActivity(this);
		//JSONLoader.execute(musicPath);
		//JSONLoader.execute(theatrePath);
		//JSONLoader.execute(filmPath);
		//JSONLoader.execute(dancePath);
		//JSONLoader.execute(galleryPath);
		//JSONLoader.execute(path);
		
		this.updateData();
		
		// Schedule the services to update the data
		//Will only execute when savedInstanceState == null
		//scheduleServices();
		
		// Adjust the settings for the ActionBar
		this.getActionBar().setHomeButtonEnabled(true);
	}
	
	/**
	 * Updates the data in the app if the data has not been updated for a long time
	 */
	private void updateData() {
		// Retrieve the time of last data update
		SharedPreferences prefs = this.getSharedPreferences("com.hooapps.pca", Context.MODE_PRIVATE);
		long modifiedMillis = prefs.getLong(LAST_DATA_UPDATE, 0);
		long currentMillis = System.currentTimeMillis();
		
		// If the data is more than one day old, update it
		if (modifiedMillis < (currentMillis - MILLIS_PER_DAY)) {
			Toast.makeText(this, "Loading data...", Toast.LENGTH_SHORT).show();
			
			AsyncDataLoader musicLoader = new AsyncDataLoader(this);
			musicLoader.execute(formatTimeRange(musicBasePath));
			
			AsyncDataLoader theatreLoader = new AsyncDataLoader(this);
			theatreLoader.execute(formatTimeRange(theatreBasePath));
			
			AsyncDataLoader filmLoader = new AsyncDataLoader(this);
			filmLoader.execute(formatTimeRange(filmBasePath));
			
			AsyncDataLoader danceLoader = new AsyncDataLoader(this);
			danceLoader.execute(formatTimeRange(danceBasePath));
			
			AsyncDataLoader galleryLoader = new AsyncDataLoader(this);
			galleryLoader.execute(formatTimeRange(galleryBasePath));
			
			AsyncDataLoader familyLoader = new AsyncDataLoader(this);
			familyLoader.execute(formatTimeRange(familyBasePath));
			
			AsyncDataLoader literaryLoader = new AsyncDataLoader(this);
			literaryLoader.execute(formatTimeRange(literaryBasePath));
			
			AsyncDataLoader venueLoader = new AsyncDataLoader(this);
			venueLoader.execute(venuePath);
			
			/*
			// Update the timestamp of last modified
			prefs.edit().putLong(LAST_DATA_UPDATE, currentMillis).apply();
			*/
		}
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
		
		//items.add(new TextItem("Home"));
		items.add(new HeaderItem("CVille Art"));
		items.add(new TextItem("Home"));
		items.add(new TextItem("Map"));
		items.add(new TextItem("Venues"));
		items.add(new TextItem("Events"));
		items.add(new TextItem("Transportation"));
		
		/*
		items.add(new HeaderItem("Community"));
		items.add(new TextItem("Streams"));
		items.add(new TextItem("Capture"));
		*/

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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Display the about section
		case R.id.action_settings:
			if (getSupportFragmentManager().findFragmentByTag("about") != null) {
				// Return to the previous screen as if back were pressed
				super.onBackPressed();
			} else {
				Fragment newFragment = new AboutFragment();
				Bundle args = new Bundle();
				launchFragment(newFragment, args, "about");
			}
			return true;
			
		// Display the nav drawer
		case android.R.id.home:
			if (drawerLayout.isDrawerOpen(leftNavDrawerList)) {
				drawerLayout.closeDrawer(leftNavDrawerList);
			} else {
				drawerLayout.openDrawer(leftNavDrawerList);
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
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
		case 1:
			newFragment = new HomeScreenFragment();
			args = new Bundle();
			break;
		// NearMeFragment
		case 2:
			newFragment = new NearMeFragment();
			args = new Bundle();
			// TODO Pass all relevant info to the fragment via args
			break;
		// DiscoverFragment
		case 3:
			newFragment = new DiscoverListFragment();
			args = new Bundle();
			// TODO Pass all relevant info to the fragment via args
			break;
		// EventFragment
		case 4:
			newFragment = new EventListFragment();
			args = new Bundle();
			// TODO Pass all relevant info to the fragment via args
			break;
		// TransportationFragment
		case 5:
			newFragment = new TransportationFragment();
			args = new Bundle();
			// TODO Pass all relevant info to the fragment via args
			break;
		}
		
		launchFragment(newFragment, args, null);
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
		
		launchFragment(newFragment, args, null);
	}
	
	private void launchFragment(Fragment newFragment, Bundle args, String tag) {
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

		transaction.replace(R.id.fragment_container, newFragment, tag);
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
		
		launchFragment(newFragment, args, null);
	}
	
	public void OnEventViewSelected(ListView l, View v, int position, long id) {
		Fragment newFragment = new EventItemFragment();
		Bundle args = new Bundle();
		
		Uri eventUri = Uri.parse(PCAContentProvider.EVENT_CONTENT_URI+"/"+id);
		args.putParcelable(PCAContentProvider.EVENT_CONTENT_ITEM_TYPE, eventUri);
		
		launchFragment(newFragment, args, null);
	}
	
	private String formatTimeRange(String calendarId) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		Calendar c = Calendar.getInstance();
		
		c.setTimeInMillis(System.currentTimeMillis());
		String minTime = sdf.format(c.getTime());
		
		c.setTimeInMillis(System.currentTimeMillis() + this.MILLI_PER_FOUR_WEEK);
		String maxTime = sdf.format(c.getTime());
		
		return BASE_PATH + calendarId + TIME_MAX + maxTime + TIME_MIN + minTime + API_KEY;
		
	}

	@Override
	public void onInfoWindowSelected(int id) {
		// Create a fragment based on the item that was clicked and swap to that
		// fragment
		// TODO Make sure that this is not stacking up fragments within the
		// code...
		Fragment newFragment = new DiscoverItemFragment();
		Bundle args = new Bundle();
		
		Uri venueUri = Uri.parse(PCAContentProvider.VENUE_CONTENT_URI+"/"+id);
		args.putParcelable(PCAContentProvider.VENUE_CONTENT_ITEM_TYPE, venueUri);
		
		launchFragment(newFragment, args, null);
	}

	@Override
	public void onDiscoverItemSelectedOpenMap(int id) {
		// TODO Auto-generated method stub
		Fragment newFragment = new NearMeFragment();
		Bundle args = new Bundle();
		
		args.putInt(VenueTable.COLUMN_ID, id);
		
		launchFragment(newFragment, args, null);
	}
}
