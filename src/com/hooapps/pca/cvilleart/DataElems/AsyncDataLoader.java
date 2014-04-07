package com.hooapps.pca.cvilleart.DataElems;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hooapps.pca.cvilleart.DataElems.PCAContentProvider.Categories;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class AsyncDataLoader extends AsyncTask<String, Void, HashMap<String, String>>{
	
	Context context;
	private static final String CALENDAR_MATCH = "www.googleapis.com/calendar/";
	
	/**
	 * Constructor used to set the context 
	 * @param context The context, usually an Activity
	 */
	public AsyncDataLoader(Context context) {
		this.context = context;
	}
	
	@Override
	protected void onPreExecute() {}
	
	@Override
	protected HashMap<String, String> doInBackground(String... urls) {
		
		// TODO
		
		// Create a placeholder string for the result
		String result;
		
		// Create a HashMap to store the name and url pairs
		HashMap<String, String> nameUrlPairs = new HashMap<String, String>();
		
		// Loop through every url and download the data accordingly
		for (int i = 0; i < urls.length; i++) {
			// Get the JSON data from the url
			result = this.fetchJSONFeed(urls[i]);
			
			// Based on the url, save the data in the appropriate spot
			// If the url contains the calendar string, process the data as a list of events
			if (urls[i].contains(CALENDAR_MATCH)) {
				this.storeEventJSONData(result);
				this.deleteOldEvents();
				
				// Set the nameUrlPairs to null since no images have to be saved
				nameUrlPairs = null;
			}
			// Else store the data as a list of venues
			else {
				// Set the nameUrlPairs based on the venues
				nameUrlPairs = this.storeVenueJSONData(result);
			}
		}
		
		return nameUrlPairs;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onPostExecute(HashMap<String, String> nameUrlPairs) {
		
		if (nameUrlPairs != null) {
			// Save the image thumbs to the phone
			AsyncImageSaver imageSaver = new AsyncImageSaver(context);
			imageSaver.execute(nameUrlPairs);
		}
	}
	
	@Override
	protected void onProgressUpdate(Void... values) {}
	
	// Private methods to retrieve the data
	
	private void deleteUnusedImages(String venueName) {
		ImageUtils imageUtils = ImageUtils.getInstance();
		File blur = imageUtils.getBlurPath(venueName);
		File thumb = imageUtils.getBlurPath(venueName);
		
		if (blur.exists()) {
			blur.delete();
		}
		
		if (thumb.exists()) {
			thumb.delete();
		}
	}
	
	private void deleteOldEvents() {
		// Get the current time
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(System.currentTimeMillis());
		
		// Set the calendar to the morning of the current day
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		
		// Delete events that occured before today
		context.getContentResolver().delete(PCAContentProvider.EVENT_CONTENT_URI, EventTable.START_TIME + " < " + (c.getTimeInMillis()/1000), null);
	}
	
	/**
	 * Fetches JSON data from a specified url
	 * @param url The url to retreive the JSON data from
	 * @return The text of the the JSON
	 */
	private String fetchJSONFeed(String url) {
		// Create a StringBuilder to store the data
		StringBuilder stringBuilder = new StringBuilder();
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		
		// Try to get the data from the url
		try {
			// Execute the HttpGet statement
			HttpResponse response = httpClient.execute(httpGet);
			
			// Retrieve the status info about the response
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			
			// If successful, proceed
			if (statusCode == 200) {
				// Get the message from the response
				HttpEntity entity = response.getEntity();
				
				InputStream inputStream = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
				
				// Loop through the message
				String line;
				while ((line = reader.readLine()) != null) {
					stringBuilder.append(line);
				}
				inputStream.close();
			}
			// Url not successfully reached
			else {
				Log.d("JSON", "Failed to load JSON");
			}
		} catch (Exception e) {
			Log.d("JSON", "Error: " + e.getLocalizedMessage());
		}
		
		return stringBuilder.toString();
	}
	
	/**
	 * Parse Venue information from a JSON string and store it in the ContentProvider
	 */
	private HashMap<String, String> storeVenueJSONData(String result) {
		// Create a HashMap to store the name and url pairs
		HashMap<String, String> nameUrlPairs = new HashMap<String, String>();
		
		// Try to parse the data from the JSON
		try {
			// Load the result into a JSONArray
			JSONArray jArray = new JSONArray(result);
			
			// Prepare a blank ContentValues and JSONObject to hold the data
			ContentValues values;
			JSONObject jVenue;
			String[] name = new String[1];
			
			// Loop through the JSONArray and save each object
			for (int i = 0; i < jArray.length(); i++) {
				// Make a new version of jObject and values to populate
				jVenue = jArray.getJSONObject(i);
				values = new ContentValues();
				
				// Populate the ContentValues
				values.put(VenueTable.ORGANIZATION_NAME, jVenue.getString("Organization Name"));
				values.put(VenueTable.EMAIL_ADDRESS, jVenue.getString("Email Address"));
				values.put(VenueTable.HOME_PAGE_URL, jVenue.getString("Home Page URL"));
				values.put(VenueTable.DIRECTORY_DESCRIPTION_LONG, jVenue.getString("Description"));
				values.put(VenueTable.PHONE_NUMBER_PRIMARY, jVenue.getString("Phone"));
				values.put(VenueTable.ADDRESS_HOME_STREET, jVenue.getString("Street Address"));
				values.put(VenueTable.ADDRESS_HOME_CITY, jVenue.getString("City"));
				values.put(VenueTable.ADDRESS_HOME_POSTAL_CODE, jVenue.getString("Zip"));
				values.put(VenueTable.ADDRESS_HOME_STATE, jVenue.getString("State"));
				values.put(VenueTable.SECONDARY_CATEGORY, jVenue.getString("Secondary Category"));
				values.put(VenueTable.IMAGE_URLS, jVenue.getString("Image URL"));
				
				// Check to see if the venue has been deleted
				String deleted = jVenue.getString("isDeleted");
				if (("YES").equals(deleted)) {
					values.put(VenueTable.IS_DELETED, 1);
				} else {
					values.put(VenueTable.IS_DELETED, 0);
				}
				
				// Parse the category string separately for processing
				String categoryString = jVenue.getString("Primary Category");
				
				// Try to cast the category as an enum
				try {
					Categories.valueOf(categoryString.replace(' ', '_').toUpperCase(Locale.ENGLISH));
				}
				// If cannot be cast, set the value of String accordingly and proceed
				catch (IllegalArgumentException e) {
					categoryString = "Venue";
				}
				// Save the modified categoryString in the database
				values.put(VenueTable.CATEGORY_ART_COMMUNITY_CATEGORIES, categoryString);
				
				// Retrieve the lat & lon as a string
				String latLngString = jVenue.getString("LatLng");
				
				// Parse the lat & lon as integers (10^6 * value) to store in database
				int lat = (int)(1000000 * Double.parseDouble(latLngString.split(",")[0].trim()));
				int lng = (int)(1000000 * Double.parseDouble(latLngString.split(",")[1].trim()));
				values.put(VenueTable.LAT_LNG_STRING, latLngString);
				values.put(VenueTable.LAT, lat);
				values.put(VenueTable.LON, lng);
				
				// Check to see if the item is already in the database
				name[0] = values.getAsString(VenueTable.ORGANIZATION_NAME);
				Cursor c = context.getContentResolver().query(PCAContentProvider.VENUE_CONTENT_URI, null, 
						VenueTable.ORGANIZATION_NAME+" = "+DatabaseUtils.sqlEscapeString(name[0]), null, null);
				
				// Insert if new object, otherwise update
				if (c.getCount() == 0) {
					context.getContentResolver().insert(PCAContentProvider.VENUE_CONTENT_URI, values);
				} else {
					context.getContentResolver().update(PCAContentProvider.VENUE_CONTENT_URI, values, VenueTable.ORGANIZATION_NAME+" = ?", name);
				}
				
				// Add the name url pair to the map
				nameUrlPairs.put(jVenue.getString("Organization Name"), jVenue.getString("Image URL"));
				
				// Delete the image on the phone if the venue has been deleted
				if (deleted.equals("YES")) {
					deleteUnusedImages(jVenue.getString("Organization Name"));
				}
				
				c.close();
			}
			
		} catch (Exception e) {
			Log.d("JSON", "Error: " + e.getLocalizedMessage());
		}
		
		return nameUrlPairs;
	}
	
	/**
	 * Parse Event information from a JSON string and store it in the ContentProvider
	 */
	private void storeEventJSONData(String result) {
		// Try to parse the event from the JSON data
		try {
			// Load the base object from the JSON
			JSONObject baseJObject = new JSONObject(result);
			
			// Get the category from the base object
			String categoryString = baseJObject.getString("summary");
			
			// Get the list of venues associated with the category
			JSONArray jArray = baseJObject.getJSONArray("items");
			
			// Prepare a blank ContentValues and JSONObject to hold the data
			ContentValues values = new ContentValues();
			JSONObject jEvent;
			String[] id = new String[1];
			
			// Loop through all of the events in the array
			for (int i = 0; i < jArray.length(); i++) {
				// Get the event from the jArray
				jEvent = jArray.getJSONObject(i);
				
				// Clear the data in the ContentValues
				values.clear();
				
				// Time the event was last updated on the calendar
				values.put(EventTable.UPDATED, jEvent.getString("updated"));
				
				// Unique id associated with the event from the Google calendar
				values.put(EventTable.EVENT_ID, jEvent.getString("id"));
				
				// Title of the event
				values.put(EventTable.SUMMARY, jEvent.getString("summary"));
				
				// Description of the event, if it exists
				if (jEvent.has("description")) {
					values.put(EventTable.DESCRIPTION, jEvent.getString("description"));
				}
				
				// Location of the event, if it exists
				if (jEvent.has("location")) {
					values.put(EventTable.LOCATION, jEvent.getString("location"));	
				}
				
				// Make sure the event has a valid start and end time
				// If not, skip adding it and continue to the other events
				if (jEvent.getJSONObject("start").has("dateTime") && jEvent.getJSONObject("end").has("dateTime")) {
					values.put(EventTable.START_TIME, parseUnixFromDate(jEvent.getJSONObject("start").getString("dateTime")));
					values.put(EventTable.END_TIME, parseUnixFromDate(jEvent.getJSONObject("end").getString("dateTime")));
				} else {
					continue;
				}
				
				// Special exception, replace visual arts with gallery
				if (categoryString.equals("Visual Arts")) {
					categoryString = "Gallery";
				}
				
				// If the category is not one we are searching for, set it to 'Venue' and continue
				try {
					Categories.valueOf(categoryString.replace(' ', '_').toUpperCase(Locale.ENGLISH));
				} catch (IllegalArgumentException e) {
					categoryString = "Venue";
				}
				values.put(EventTable.CATEGORY, categoryString);
				
				// Check to see if the item is already in the database
				id[0] = values.getAsString(EventTable.EVENT_ID);
				Cursor c = context.getContentResolver().query(PCAContentProvider.EVENT_CONTENT_URI, null, 
						EventTable.EVENT_ID + " = " + DatabaseUtils.sqlEscapeString(id[0]), null, null);
				
				// Insert if new object, otherwise update
				if (c.getCount() == 0) {
					context.getContentResolver().insert(PCAContentProvider.EVENT_CONTENT_URI, values);
				} else {
					context.getContentResolver().update(PCAContentProvider.EVENT_CONTENT_URI, values, EventTable.EVENT_ID+" = ?", id);
				}
				
				c.close();
			}
			
		} catch (JSONException e) {
			Log.d("JSON", "Error: " + e.getLocalizedMessage());
		}
	}
	
	/**
	 * Convert date strings into unix time
	 * @param date A string containing the data
	 * @return The unix value of the date
	 */
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
		return unixTime;
	}
}
