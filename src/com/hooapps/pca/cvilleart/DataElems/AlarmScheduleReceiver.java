package com.hooapps.pca.cvilleart.DataElems;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Extended BroadcastReceiver to handle alarm calls for schedule database 
 * updates. Information is pulled (eventually) from the PCA website.
 * 
 * @author Spencer Gennari
 *
 */

public class AlarmScheduleReceiver extends BroadcastReceiver {
	
	private String venuePath 	=	"http://people.virginia.edu/~sdg6vt/CVilleArt/PCA_Data.json";
	private String musicPath	=	"https://www.googleapis.com/calendar/v3/calendars/charlottesvillearts.org_9oapvu67eckm7hkbm22p8debtc%40group.calendar.google.com/events?singleEvents=true&timeMax=2014-02-28T11%3A59%3A00Z&timeMin=2014-02-19T00%3A00%3A00Z&key=AIzaSyDegSazDw-VcXQtWyVDmsDiV-xgwaT9ijE";
	private String theatrePath	=	"https://www.googleapis.com/calendar/v3/calendars/charlottesvillearts.org_ob2g1r475vou79aa2piljkivm0%40group.calendar.google.com/events?singleEvents=true&timeMax=2014-02-28T11%3A59%3A00Z&timeMin=2014-02-19T00%3A00%3A00Z&key=AIzaSyDegSazDw-VcXQtWyVDmsDiV-xgwaT9ijE";
	private String filmPath		= 	"https://www.googleapis.com/calendar/v3/calendars/charlottesvillearts.org_gmbfku7u83glhstgll6p4ikeh4%40group.calendar.google.com/events?singleEvents=true&timeMax=2014-02-28T11%3A59%3A00Z&timeMin=2014-02-19T00%3A00%3A00Z&key=AIzaSyDegSazDw-VcXQtWyVDmsDiV-xgwaT9ijE";
	private String dancePath	= 	"https://www.googleapis.com/calendar/v3/calendars/charlottesvillearts.org_6j3aq5pd2t3ikhm4ms563h5hrs%40group.calendar.google.com/events?singleEvents=true&timeMax=2014-02-28T11%3A59%3A00Z&timeMin=2014-02-19T00%3A00%3A00Z&key=AIzaSyDegSazDw-VcXQtWyVDmsDiV-xgwaT9ijE";
	private String galleryPath	= 	"https://www.googleapis.com/calendar/v3/calendars/charlottesvillearts.org_fci03o8i70o7ugjtchqll39ck0%40group.calendar.google.com/events?singleEvents=true&timeMax=2014-02-28T11%3A59%3A00Z&timeMin=2014-02-19T00%3A00%3A00Z&key=AIzaSyDegSazDw-VcXQtWyVDmsDiV-xgwaT9ijE";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("ALARM", "onReceive method reached!");
		
		//TODO FORMAT STRINGS BASED ON THE CURRENT TIME
		
		
		Log.d("SERVICES", "Scheduling services...");
		// Make an intent for each feed
		Intent venueIntent = new Intent(context, DataIntentService.class);
		venueIntent.putExtra("url", this.venuePath);
		venueIntent.putExtra("type", PCAContentProvider.VENUE_ADAPTER_ID);
		
		Intent danceIntent = new Intent(context, DataIntentService.class);
		danceIntent.putExtra("url", this.dancePath);
		danceIntent.putExtra("type", PCAContentProvider.EVENT_ADAPTER_ID);
		
		Intent filmIntent = new Intent(context, DataIntentService.class);
		filmIntent.putExtra("url", this.filmPath);
		filmIntent.putExtra("type", PCAContentProvider.EVENT_ADAPTER_ID);
		
		Intent galleryIntent = new Intent(context, DataIntentService.class);
		galleryIntent.putExtra("url", this.galleryPath);
		galleryIntent.putExtra("type", PCAContentProvider.EVENT_ADAPTER_ID);
		
		Intent musicIntent = new Intent(context, DataIntentService.class);
		musicIntent.putExtra("url", this.musicPath);
		musicIntent.putExtra("type", PCAContentProvider.EVENT_ADAPTER_ID);
		
		Intent theatreIntent = new Intent(context, DataIntentService.class);
		theatreIntent.putExtra("url", this.theatrePath);
		theatreIntent.putExtra("type", PCAContentProvider.EVENT_ADAPTER_ID);
		
		// Start the service by queuing up the intents
		context.startService(venueIntent);
		context.startService(danceIntent);
		context.startService(filmIntent);
		context.startService(galleryIntent);
		context.startService(musicIntent);
		context.startService(theatreIntent);

		Log.d("SERVICES", "Services scheduled.");
	}
}
