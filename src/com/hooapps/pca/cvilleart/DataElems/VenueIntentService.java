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
		data.put(VenueTable.ORGANIZATION_NAME, "name");
		data.put(VenueTable.DIRECTORY_DESCRIPTION_LONG, "description");
		data.put(VenueTable.IMAGE_URLS, "https://lh4.googleusercontent.com/-NEkAkCdt41E/AAAAAAAAAAI/AAAAAAAAAAA/n0NL-BQik0k/photo.jpg");
		data.put(VenueTable.CATEGORY_ART_COMMUNITY_CATEGORIES, "type");
		data.put(VenueTable.ADDRESS_HOME_STREET, "street address");
		
		this.getContentResolver().insert(PCAContentProvider.VENUE_CONTENT_URI, data);
	}
}