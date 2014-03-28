package com.hooapps.pca.cvilleart.DataElems;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Locale;

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
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.Target;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

/**
 * Service dedicated to updating the venues stored in the database.
 * 
 * @author Spencer Gennari
 *
 */

public class DataIntentService extends IntentService {
	
	public DataIntentService() {
		super("VenueIntentService");
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		// Get the data from the intent
		String url = intent.getStringExtra("url");
		int type = intent.getIntExtra("type", -1);
		
		// Process the data
		String result = this.readJSONFeed(url);
		switch (type) {
		case PCAContentProvider.EVENT_ADAPTER_ID:
			storeEventData(result);
			deleteOldEvents();
			break;
		case PCAContentProvider.VENUE_ADAPTER_ID:
			storeVenueData(result);
			break;
		default:
			Log.e("Error", "Invalid path type: " + type);
		}
	}
	
	private void storeVenueData(String result) {
		try {
			JSONArray jArray = new JSONArray(result);
			ContentValues values;
			JSONObject jObject;
			String[] name = new String[1];
			for (int i = 0; i < jArray.length(); i++) {
				jObject = jArray.getJSONObject(i);
				values = new ContentValues();
				
				// Populate the ContentValues
				values.put(VenueTable.ORGANIZATION_NAME, jObject.getString("Organization Name"));
				values.put(VenueTable.EMAIL_ADDRESS, jObject.getString("Email Address"));
				values.put(VenueTable.HOME_PAGE_URL, jObject.getString("Home Page URL"));
				values.put(VenueTable.DIRECTORY_DESCRIPTION_LONG, jObject.getString("Description"));
				values.put(VenueTable.PHONE_NUMBER_PRIMARY, jObject.getString("Phone"));
				values.put(VenueTable.ADDRESS_HOME_STREET, jObject.getString("Street Address"));
				values.put(VenueTable.ADDRESS_HOME_CITY, jObject.getString("City"));
				values.put(VenueTable.ADDRESS_HOME_POSTAL_CODE, jObject.getString("Zip"));
				values.put(VenueTable.ADDRESS_HOME_STATE, jObject.getString("State"));
				
				String categoryString = jObject.getString("Primary Category");
				try {
					Categories.valueOf(categoryString.replace(' ', '_').toUpperCase(Locale.ENGLISH));
				} catch (IllegalArgumentException e) {
					if (categoryString.equals("Gallery")) {
						categoryString = "Visual Arts";
					} else {
						categoryString = "Venue";
					}
				}
				values.put(VenueTable.CATEGORY_ART_COMMUNITY_CATEGORIES, categoryString);
				
				values.put(VenueTable.SECONDARY_CATEGORY, jObject.getString("Secondary Category"));
				
				
				// Retrieve the lat & lon as a string
				String latLngString = jObject.getString("LatLng");
				
				// Parse the lat & lon as integers (10^6 * value) to store in database
				int lat = (int)(1000000 * Double.parseDouble(latLngString.split(",")[0].trim()));
				int lng = (int)(1000000 * Double.parseDouble(latLngString.split(",")[1].trim()));
				values.put(VenueTable.LAT_LNG_STRING, latLngString);
				values.put(VenueTable.LAT, lat);
				values.put(VenueTable.LON, lng);
				Log.d("LAT_LON", lat + ", " + lng);
				
				values.put(VenueTable.IMAGE_URLS, jObject.getString("Image URL"));
				
				// Check to see if the item is already in the database
				name[0] = values.getAsString(VenueTable.ORGANIZATION_NAME);
				Cursor c = getContentResolver().query(PCAContentProvider.VENUE_CONTENT_URI, null, 
						VenueTable.ORGANIZATION_NAME+" = "+DatabaseUtils.sqlEscapeString(name[0]), null, null);
				
				// Insert if new object, otherwise update
				if (c.getCount() == 0) {
					getContentResolver().insert(PCAContentProvider.VENUE_CONTENT_URI, values);
					//Log.d("storeJSONData", "JSON Venue added: " + name);
				} else {
					getContentResolver().update(PCAContentProvider.VENUE_CONTENT_URI, values, VenueTable.ORGANIZATION_NAME+" = ?", name);
					//Log.d("storeJSONData", "JSON Venue updated: " + name);
				}
				
				// TODO ADD 'isDeleted' and 'UNIX Timestamp'
				
				c.close();
			}
		} catch (JSONException e) {
			Log.d("storeJSONData", "Error: " + e.getLocalizedMessage());
		}
	}
	
	private void storeEventData(String result) {
		try {
			JSONObject jObject = new JSONObject(result);
			String categoryString = jObject.getString("summary");
			JSONArray jArray = jObject.getJSONArray("items");
			ContentValues values = new ContentValues();
			String[] id = new String[1];
			JSONObject event;
			Uri eventUri;
			
			Log.d("EVENT", "Category: " + categoryString);
			
			for(int i = 0; i < jArray.length(); i++) {
				event = jArray.getJSONObject(i);
				values.clear();
				
				// Populate the ContentValues
				
				// Time the event was last updated on the calendar
				values.put(EventTable.UPDATED, event.getString("updated"));
				
				// Unique id associated with the event from the Google calendar
				values.put(EventTable.EVENT_ID, event.getString("id"));
				
				// Title of the event
				values.put(EventTable.SUMMARY, event.getString("summary"));
				
				// Description of the event, if it exists
				if (event.has("description")) {
					values.put(EventTable.DESCRIPTION, event.getString("description"));
				}
				
				// Location of the event, if it exists
				if (event.has("location")) {
					values.put(EventTable.LOCATION, event.getString("location"));	
				}
				
				// Make sure the event has a valid start and end time
				// If not, skip adding it and continue to the other events
				if (event.getJSONObject("start").has("dateTime") && event.getJSONObject("end").has("dateTime")) {
					values.put(EventTable.START_TIME, parseUnixFromDate(event.getJSONObject("start").getString("dateTime")));
					values.put(EventTable.END_TIME, parseUnixFromDate(event.getJSONObject("end").getString("dateTime")));
				} else {
					continue;
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
				Cursor c = getContentResolver().query(PCAContentProvider.EVENT_CONTENT_URI, null, 
						EventTable.EVENT_ID + " = " + DatabaseUtils.sqlEscapeString(id[0]), null, null);
				
				// Insert if new object, otherwise update
				if (c.getCount() == 0) {
					eventUri = getContentResolver().insert(PCAContentProvider.EVENT_CONTENT_URI, values);
				} else {
					getContentResolver().update(PCAContentProvider.EVENT_CONTENT_URI, values, EventTable.EVENT_ID+" = ?", id);
				}
				
				c.close();
				
			}
		} catch (JSONException e) {
			Log.d("storeJSONData", "Error: " + e.getLocalizedMessage());
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
		getContentResolver().delete(PCAContentProvider.EVENT_CONTENT_URI, EventTable.START_TIME + " < " + (c.getTimeInMillis()/1000), null);
	}
	
	private String readJSONFeed(String url) {
		StringBuilder stringBuilder = new StringBuilder();
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		
		try {
			HttpResponse response = httpClient.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			
			if(statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream inputStream = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
				String line;
				while ((line = reader.readLine()) != null) {
					stringBuilder.append(line);
				}
				inputStream.close();
			} else {
				//Log.d("JSON", "Failed to load in JSON");
			}
		} catch (Exception e) {
			Log.d("ReadJSONFeed", "Error: " + e.getLocalizedMessage());
		}
		return stringBuilder.toString();
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
		return unixTime;
	}
}