package com.hooapps.pca.cvilleart.ListViewElems;

import java.util.Calendar;
import java.util.Locale;

import com.hooapps.pca.cvilleart.R;
import com.hooapps.pca.cvilleart.DataElems.EventTable;
import com.hooapps.pca.cvilleart.DataElems.PCAContentProvider.Categories;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class EventCursorAdapter extends SimpleCursorAdapter {
	
	private static int SECONDS_PER_DAY = 60 * 60 * 24;
	
	private LayoutInflater layoutInflater;
	private Context context;
	private int layout;
	private double screenDensity;
	private Cursor cursor;
	
	public EventCursorAdapter(Context context, int layout, Cursor cursor, String[] from, int[] to) {
		super(context, layout, cursor, from, to, 0);
		this.context = context;
		this.layout = layout;
		this.layoutInflater = LayoutInflater.from(context);
		this.screenDensity = context.getResources().getDisplayMetrics().density;
		this.cursor = cursor;
	}
	
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View v = layoutInflater.inflate(layout, parent, false);
		return v;
	}
	
	@Override
	public void bindView(View v, Context context, Cursor cursor) {
		
		// Retrieve the column indexes from the cursor
		int colEventId = cursor.getColumnIndex(EventTable.EVENT_ID);
		int colCategory = cursor.getColumnIndex(EventTable.CATEGORY);
		int colSummary = cursor.getColumnIndex(EventTable.SUMMARY);
		int colStartTime = cursor.getColumnIndex(EventTable.START_TIME);
		
		// Retrieve the data from the cursor
		String eventId = cursor.getString(colEventId);
		String categoryString = cursor.getString(colCategory);
		String summary = cursor.getString(colSummary);
		int startTime = cursor.getInt(colStartTime);
		
		// Display the data in the view in the list
		TextView titleView = (TextView) v.findViewById(R.id.event_title);
		titleView.setText(summary);
		
		TextView categoryView = (TextView) v.findViewById(R.id.category);
		categoryView.setText(categoryString);
		
		// Convert the startTime to hours and minutes
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(startTime * 1000L);
		int hours = c.get(Calendar.HOUR_OF_DAY);
		int minutes = c.get(Calendar.MINUTE);
		
		// Update the timeView
		TextView timeView = (TextView) v.findViewById(R.id.time);
		timeView.setText(String.format("% 2d", hours%12) + ":" + String.format("%02d", minutes));
		
		TextView timePeriodView = (TextView) v.findViewById(R.id.time_period);
		if (hours/12 == 0) {
			timePeriodView.setText("AM");
		} else {
			timePeriodView.setText("PM");
		}
		
		// TODO CONSIDER MAKING STATIC VARIABLES FOR THE CATEGORIES IN PCAContentProvider
		// Set background color of the timeContainer
		RelativeLayout timeContainer = (RelativeLayout) v.findViewById(R.id.time_container);
		Categories category = Categories.valueOf(categoryString.replace(' ', '_').toUpperCase(Locale.ENGLISH));
		switch (category) {
		case DANCE: timeContainer.setBackgroundResource(R.drawable.green_bg);
			break;
		case MUSIC: timeContainer.setBackgroundResource(R.drawable.orange_bg);
			break;
		case THEATRE: timeContainer.setBackgroundResource(R.drawable.purple_bg);
		break;
		case GALLERY: timeContainer.setBackgroundResource(R.drawable.blue_bg);
			break;
		case VENUE:
		default: timeContainer.setBackgroundResource(R.drawable.teal_bg);
			break;
		}
		
		// Check to see if a header is needed
		RelativeLayout headerContainer = (RelativeLayout) v.findViewById(R.id.header_container);
		TextView header = (TextView) v.findViewById(R.id.header);
		headerContainer.setVisibility(View.GONE);
		
		// Set the header to the date
		int position = cursor.getPosition();
		if (position == 0) {
			headerContainer.setVisibility(View.VISIBLE);
			header.setText(createDateString(c));
		} else {
			
			// Get the current date
			int curDate = c.get(Calendar.DAY_OF_MONTH);
			
			// Get the previous date
			cursor.moveToPrevious();
			int prevStartTime = cursor.getInt(colStartTime);
			c.setTimeInMillis(prevStartTime * 1000L);
			int prevDate = c.get(Calendar.DAY_OF_MONTH);
			
			// Reset the calendar to the correct time
			c.setTimeInMillis(startTime * 1000L);
			
			if (prevDate != curDate) {
				headerContainer.setVisibility(View.VISIBLE);
				header.setText(createDateString(c));	
			}
		}
		
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
