package com.hooapps.pca.cvilleart.DataElems;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Extended BroadcastReceiver to handle alarm calls for schedule database 
 * updates. Information is pulled  from the PCA website.
 * 
 * @author Spencer Gennari
 *
 */

public class AlarmScheduleReceiver extends BroadcastReceiver {
	
	private final int MILLI_PER_TWO_WEEK = 2 * 7 * 24 * 60 * 60 * 1000;
	private final String BASE_PATH = "https://www.googleapis.com/calendar/v3/calendars/";
	private final String TIME_MAX = "/events?singleEvents=true&timeMax=";
	private final String TIME_MIN = "T00%3A00%3A00Z&timeMin=";
	private final String API_KEY = "T00%3A00%3A00Z&key=AIzaSyDegSazDw-VcXQtWyVDmsDiV-xgwaT9ijE";
	
	private String venuePath 	=	"http://people.virginia.edu/~sdg6vt/CVilleArt/PCA_Data.json";
	private String familyBasePath	=	"charlottesvillearts.org_1d75dtbvjd8adgei0borv0dp30@group.calendar.google.com";
	private String musicBasePath	=	"charlottesvillearts.org_9oapvu67eckm7hkbm22p8debtc@group.calendar.google.com";
	private String theatreBasePath	=	"charlottesvillearts.org_ob2g1r475vou79aa2piljkivm0@group.calendar.google.com";
	private String filmBasePath		= 	"charlottesvillearts.org_gmbfku7u83glhstgll6p4ikeh4@group.calendar.google.com";
	private String danceBasePath	= 	"charlottesvillearts.org_6j3aq5pd2t3ikhm4ms563h5hrs@group.calendar.google.com";
	private String galleryBasePath	= 	"charlottesvillearts.org_fci03o8i70o7ugjtchqll39ck0@group.calendar.google.com";
	private String literaryBasePath	=	"charlottesvillearts.org_1nvlsks9klme3evsf1cqhe2i64@group.calendar.google.com";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("ALARM", "onReceive method reached!");
		
		// Format the url requests based on the current time
		String familyCalendarPath = formatTimeRange(familyBasePath);
		String musicCalendarPath = formatTimeRange(musicBasePath);
		String theatreCalendarPath = formatTimeRange(theatreBasePath);
		String filmCalendarPath = formatTimeRange(filmBasePath);
		String danceCalendarPath = formatTimeRange(danceBasePath);
		String galleryCalendarPath = formatTimeRange(galleryBasePath);
		String literaryCalendarPath = formatTimeRange(literaryBasePath);
		
		Log.d("SERVICES", "Scheduling services...");
		// Make an intent for each feed
		Intent venueIntent = new Intent(context, DataIntentService.class);
		venueIntent.putExtra("url", venuePath);
		venueIntent.putExtra("type", PCAContentProvider.VENUE_ADAPTER_ID);
		
		Intent familyIntent = new Intent(context, DataIntentService.class);
		familyIntent.putExtra("url", familyCalendarPath);
		familyIntent.putExtra("type", PCAContentProvider.EVENT_ADAPTER_ID);
		
		Intent danceIntent = new Intent(context, DataIntentService.class);
		danceIntent.putExtra("url", danceCalendarPath);
		danceIntent.putExtra("type", PCAContentProvider.EVENT_ADAPTER_ID);
		
		Intent filmIntent = new Intent(context, DataIntentService.class);
		filmIntent.putExtra("url", filmCalendarPath);
		filmIntent.putExtra("type", PCAContentProvider.EVENT_ADAPTER_ID);
		
		Intent galleryIntent = new Intent(context, DataIntentService.class);
		galleryIntent.putExtra("url", galleryCalendarPath);
		galleryIntent.putExtra("type", PCAContentProvider.EVENT_ADAPTER_ID);
		
		Intent musicIntent = new Intent(context, DataIntentService.class);
		musicIntent.putExtra("url", musicCalendarPath);
		musicIntent.putExtra("type", PCAContentProvider.EVENT_ADAPTER_ID);
		
		Intent theatreIntent = new Intent(context, DataIntentService.class);
		theatreIntent.putExtra("url", theatreCalendarPath);
		theatreIntent.putExtra("type", PCAContentProvider.EVENT_ADAPTER_ID);
		
		Intent literaryIntent = new Intent(context, DataIntentService.class);
		literaryIntent.putExtra("url", literaryCalendarPath);
		literaryIntent.putExtra("type", PCAContentProvider.EVENT_ADAPTER_ID);
		
		// Start the service by queuing up the intents
		context.startService(venueIntent);
		context.startService(familyIntent);
		context.startService(danceIntent);
		context.startService(filmIntent);
		context.startService(galleryIntent);
		context.startService(musicIntent);
		context.startService(theatreIntent);
		context.startService(literaryIntent);

		Log.d("SERVICES", "Services scheduled.");
	}
	
	private String formatTimeRange(String calendarId) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		Calendar c = Calendar.getInstance();
		
		c.setTimeInMillis(System.currentTimeMillis());
		String minTime = sdf.format(c.getTime());
		
		c.setTimeInMillis(c.getTimeInMillis() + this.MILLI_PER_TWO_WEEK);
		String maxTime = sdf.format(c.getTime());
		
		Log.d("MAX", maxTime);
		
		return BASE_PATH + calendarId + TIME_MAX + maxTime + TIME_MIN + minTime + API_KEY;
		
	}
}
