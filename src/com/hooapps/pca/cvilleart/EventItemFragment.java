package com.hooapps.pca.cvilleart;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import com.hooapps.pca.cvilleart.DataElems.EventTable;
import com.hooapps.pca.cvilleart.DataElems.PCAContentProvider;
import com.hooapps.pca.cvilleart.DataElems.PCAContentProvider.Categories;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Instances;
import android.provider.CalendarContract.Reminders;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class EventItemFragment extends Fragment {
	
	private static final long MS_PER_HOUR = 60 * 60 * 1000L; 
	
	private String summary;
	private String description;
	private String location;
	private String eventId;
	private int startTime;
	private int endTime;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.event_item_view, container, false);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		Bundle args = getArguments();
		if (args != null) {
			updateEventItem(args);
			bindListeners();
		} else {
			// TODO Setup the fragment according to other specifications
		}
	}
	
	private void bindListeners() {
		Button calendarAddButton = (Button) this.getActivity().findViewById(R.id.add_to_calendar_button);
		calendarAddButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addEventToCalendar();
				Toast.makeText(getActivity(), R.string.event_added_to_calendar, Toast.LENGTH_SHORT).show();
			}
			
		});
	}
	
	private void updateEventItem(Bundle args) {
		Uri uri = args.getParcelable(PCAContentProvider.EVENT_CONTENT_ITEM_TYPE);
		
		String[] projection = {
				EventTable.SUMMARY,
				EventTable.DESCRIPTION,
				EventTable.START_TIME,
				EventTable.END_TIME,
				EventTable.LOCATION,
				EventTable.CATEGORY,
				EventTable.EVENT_ID
		};
		
		Cursor cursor = this.getActivity().getContentResolver().query(uri, projection, null, null, null);
		
		if (cursor != null) {
			cursor.moveToFirst();
			
			// Get the data from the cursor
			summary = cursor.getString(cursor.getColumnIndex(EventTable.SUMMARY));
			description = cursor.getString(cursor.getColumnIndex(EventTable.DESCRIPTION));
			startTime = cursor.getInt(cursor.getColumnIndex(EventTable.START_TIME));
			endTime = cursor.getInt(cursor.getColumnIndex(EventTable.END_TIME));
			location = cursor.getString(cursor.getColumnIndex(EventTable.LOCATION));
			eventId = cursor.getString(cursor.getColumnIndex(EventTable.EVENT_ID));
			String categoryString = cursor.getString(cursor.getColumnIndex(EventTable.CATEGORY));
			
			// Set the color based on the category
			int colorResId = 0;
			int drawableResId = 0;
			Categories category = Categories.valueOf(categoryString.replace(' ', '_').toUpperCase(Locale.ENGLISH));
			switch (category) {
			case DANCE: colorResId = R.color.green;
				drawableResId = R.drawable.dance;
				break;
			case MUSIC: colorResId = R.color.orange;
				drawableResId = R.drawable.music;
				break;
			case THEATRE: colorResId = R.color.purple;
				drawableResId = R.drawable.theatre;
				break;
			case GALLERY: colorResId = R.color.blue;
				drawableResId = R.drawable.gallery;
				break;
			case VENUE:
			default: colorResId = R.color.indigo;
				drawableResId = R.drawable.other;
				break;
			}
			
			// Retrieve the views from the layout
			Activity a = this.getActivity();
			TextView titleView = (TextView) a.findViewById(R.id.event_name);
			TextView descriptionView = (TextView) a.findViewById(R.id.description);
			TextView addressView = (TextView) a.findViewById(R.id.address);
			TextView timeView = (TextView) a.findViewById(R.id.date_time);
			ImageView imageView = (ImageView) a.findViewById(R.id.event_image);
			
			// Retrieve the containers
			RelativeLayout imageContainer = (RelativeLayout) a.findViewById(R.id.image_container);
			
			// Parse the start and end times as a string
			String dateRange = "";
			String timeRange = "";
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(startTime * 1000L);
			dateRange = createDateString(c);
			
			timeRange += createTimeString(c);
			timeRange += " - ";
			c.setTimeInMillis(endTime * 1000L);
			timeRange += createTimeString(c);
			
			// Set the fields
			titleView.setText(summary);
			descriptionView.setText(description);
			addressView.setText(location);
			timeView.setText(dateRange + "\n" + timeRange);
			
			imageView.setImageResource(drawableResId);
			imageContainer.setBackgroundResource(colorResId);
			
			cursor.close();
		}
	}
	
	private void addEventToCalendar() {		
		// Load the information into ContentValues
		String eventUriString = "content://com.android.calendar/events";
		
		ContentValues eventValues = new ContentValues();
		eventValues.put(Events.CALENDAR_ID, 1);
		eventValues.put(Events.TITLE, summary);
		eventValues.put(Events.DESCRIPTION, description);
		eventValues.put(Events.EVENT_LOCATION, location);
		eventValues.put(Events.DTSTART, startTime * 1000L);
		eventValues.put(Events.DTEND, endTime * 1000L);
		eventValues.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());

		Uri eventUri = this.getActivity().getApplicationContext().getContentResolver().insert(Uri.parse(eventUriString), eventValues);
		long eventId = Long.parseLong(eventUri.getLastPathSegment());
		
		String reminderUriString = "content://com.android.calendar/reminders";
		ContentValues reminderValues = new ContentValues();
		reminderValues.put(Reminders.EVENT_ID, eventId);
		reminderValues.put(Reminders.MINUTES, 20);
		reminderValues.put(Reminders.METHOD, 0);
		
		Uri reminderUri = this.getActivity().getApplicationContext().getContentResolver().insert(Uri.parse(reminderUriString), reminderValues);
	}
	
	private String createTimeString(Calendar c) {
		String result = "";
		
		int hours = c.get(Calendar.HOUR_OF_DAY);
		int minutes = c.get(Calendar.MINUTE);
		result = String.format("% 2d", hours%12) + ":" + String.format("%02d", minutes);
		
		if (hours/12 == 0) {
			result += " AM";
		} else {
			result += " PM";
		}
		
		return result;
	}
	
	private String createDateString(Calendar c) {
		String result = "";
		
		// Find the day of the week
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		switch (dayOfWeek) {
		case Calendar.MONDAY:
			result += "Monday";
			break;
		case Calendar.TUESDAY:
			result += "Tuesday";
			break;
		case Calendar.WEDNESDAY:
			result += "Wednesday";
			break;
		case Calendar.THURSDAY:
			result += "Thursday";
			break;
		case Calendar.FRIDAY:
			result += "Friday";
			break;
		case Calendar.SATURDAY:
			result += "Saturday";
			break;
		case Calendar.SUNDAY:
			result += "Sunday";
			break;
		}
		
		result += ", ";
		
		// Find the month
		int month = c.get(Calendar.MONTH);
		switch (month) {
		case Calendar.JANUARY:
			result += "January";
			break;
		case Calendar.FEBRUARY:
			result += "February";
			break;
		case Calendar.MARCH:
			result += "March";
			break;
		case Calendar.APRIL:
			result += "April";
			break;
		case Calendar.MAY:
			result += "May";
			break;
		case Calendar.JUNE:
			result += "June";
			break;
		case Calendar.JULY:
			result += "July";
			break;
		case Calendar.AUGUST:
			result += "August";
			break;
		case Calendar.SEPTEMBER:
			result += "September";
			break;
		case Calendar.OCTOBER:
			result += "October";
			break;
		case Calendar.NOVEMBER:
			result += "November";
			break;
		case Calendar.DECEMBER:
			result += "December";
			break;
		}
		
		result += " ";
		
		// Add the date
		result += c.get(Calendar.DAY_OF_MONTH);
		
		return result;
	}
	
	
}
