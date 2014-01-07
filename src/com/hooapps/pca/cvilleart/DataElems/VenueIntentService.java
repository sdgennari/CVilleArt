package com.hooapps.pca.cvilleart.DataElems;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;

/**
 * Service dedicated to updating the venues stored in the database.
 * 
 * @author Spencer Gennari
 *
 */

public class VenueIntentService extends IntentService {
	
	public VenueIntentService() {
		super("VenueIntentService");
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Get the data from the intent accordingly
		
		// Sample data below to test service, provider, data, etc.
		ContentValues data = new ContentValues();
		data.put(PCADatabase.VENUE_NAME, "name");
		data.put(PCADatabase.VENUE_DESCRIPTION, "description");
		data.put(PCADatabase.VENUE_IMG_URL, "https://lh4.googleusercontent.com/-NEkAkCdt41E/AAAAAAAAAAI/AAAAAAAAAAA/n0NL-BQik0k/photo.jpg");
		data.put(PCADatabase.VENUE_TYPE, "type");
		data.put(PCADatabase.VENUE_ADDRESS, "address");
		
		this.getContentResolver().insert(DataProvider.VENUE_CONTENT_URI, data);
	}
}
