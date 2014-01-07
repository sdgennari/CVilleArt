package com.hooapps.pca.cvilleart.DataElems;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Extended BroadcastReceiver to handle alarm calls for schedule database 
 * updates. Information is pulled (eventually) from the PCA website.
 * 
 * @author Spencer Gennari
 *
 */

public class AlarmReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Check to see which service should be called
		Intent venueServiceIntent = new Intent(context, VenueIntentService.class);
		
		// TODO Set the url data here...
		
		context.startService(venueServiceIntent);
	}
}
