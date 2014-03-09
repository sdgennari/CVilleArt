package com.hooapps.pca.cvilleart.DataElems;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
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
		// TODO Get the data from the intent accordingly
		
		// Get the data from the intent
		String url = intent.getStringExtra("url");
		int type = intent.getIntExtra("type", -1);
		
		//Log.d("I_SERVICE", "Url: " + url);
		
		// Process the data
		String result = this.readJSONFeed(url);
		switch (type) {
		case PCAContentProvider.EVENT_ADAPTER_ID:
			storeEventData(result);
			break;
		case PCAContentProvider.VENUE_ADAPTER_ID:
			storeVenueData(result);
			break;
		default:
			Log.e("Error", "Invalid path type: " + type);
		}
		/*
		// Sample data below to test service, provider, data, etc.
		ContentValues data = new ContentValues();
		data.put(VenueTable.ORGANIZATION_NAME, "name");
		data.put(VenueTable.DIRECTORY_DESCRIPTION_LONG, "description");
		data.put(VenueTable.IMAGE_URLS, "https://lh4.googleusercontent.com/-NEkAkCdt41E/AAAAAAAAAAI/AAAAAAAAAAA/n0NL-BQik0k/photo.jpg");
		data.put(VenueTable.CATEGORY_ART_COMMUNITY_CATEGORIES, "type");
		data.put(VenueTable.ADDRESS_HOME_STREET, "street address");
		
		this.getContentResolver().insert(PCAContentProvider.VENUE_CONTENT_URI, data);
		*/
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
					getContentResolver().insert(PCAContentProvider.VENUE_CONTENT_URI, values);
					//Log.d("storeJSONData", "JSON Venue added: " + name);
				} else {
					getContentResolver().update(PCAContentProvider.VENUE_CONTENT_URI, values, VenueTable.ORGANIZATION_NAME+" = ?", name);
					//Log.d("storeJSONData", "JSON Venue updated: " + name);
				}
				
				c.close();
			}
		} catch (JSONException e) {
			Log.d("storeJSONData", "Error: " + e.getLocalizedMessage());
		}
	}
	
	private void storeEventData(String result) {
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
					//Log.d("storeJSONData", "JSON Event added to "+id);
				} else {
					getContentResolver().update(PCAContentProvider.EVENT_CONTENT_URI, values, EventTable.EVENT_ID+" = ?", id);
					//Log.d("storeJSONData", "JSON Event updated: " + id);
				}
				
				c.close();
				
			}
		} catch (JSONException e) {
			Log.d("storeJSONData", "Error: " + e.getLocalizedMessage());
		}
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