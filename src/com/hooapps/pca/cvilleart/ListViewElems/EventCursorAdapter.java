package com.hooapps.pca.cvilleart.ListViewElems;

import java.util.Calendar;

import com.hooapps.pca.cvilleart.R;
import com.hooapps.pca.cvilleart.DataElems.EventTable;

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
		String category = cursor.getString(colCategory);
		String summary = cursor.getString(colSummary);
		int startTime = cursor.getInt(colStartTime);
		
		// Display the data in the view in the list
		TextView titleView = (TextView) v.findViewById(R.id.event_title);
		titleView.setText(summary);
		
		TextView categoryView = (TextView) v.findViewById(R.id.category);
		categoryView.setText(category);
		
		// Convert the startTime to hours and minutes
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(startTime * 1000L);
		int hours = c.get(Calendar.HOUR_OF_DAY);
		int minutes = c.get(Calendar.MINUTE);
		
		// TODO REMOVE AFTER DEBUGGING
		categoryView.setText(""+c.get(Calendar.DAY_OF_MONTH));
		
		TextView timeView = (TextView) v.findViewById(R.id.time);
		timeView.setText(String.format("% 2d", hours%12) + ":" + String.format("%02d", minutes));
		
		TextView timePeriodView = (TextView) v.findViewById(R.id.time_period);
		if (hours%12 == 0) {
			timePeriodView.setText("AM");
		} else {
			timePeriodView.setText("PM");
		}
		
		// Set background color of the timeContainer
		RelativeLayout timeContainer = (RelativeLayout) v.findViewById(R.id.time_container);
		if (category.equalsIgnoreCase("music")) {
			timeContainer.setBackgroundResource(R.drawable.orange_bg);
		} else if (category.equalsIgnoreCase("theatre")) {
			timeContainer.setBackgroundResource(R.drawable.purple_bg);
		} else if (category.equalsIgnoreCase("film")) {
			timeContainer.setBackgroundResource(R.drawable.teal_bg);
		} else if (category.equalsIgnoreCase("dance")) {
			timeContainer.setBackgroundResource(R.drawable.green_bg);
		} else if (category.equalsIgnoreCase("visual arts")) {
			timeContainer.setBackgroundResource(R.drawable.blue_bg);
		} else {	// Other category
			// TODO MAKE MORE COLORS
		}
		
		// Check to see if a header is needed
		RelativeLayout headerContainer = (RelativeLayout) v.findViewById(R.id.header_container);
		TextView header = (TextView) v.findViewById(R.id.header);
		Log.d("HEADER", "Check 0");
		int position = cursor.getPosition();
		if (position - 1 < 0) {
			Log.d("HEADER", "Check 1");
			headerContainer.setVisibility(View.VISIBLE);
			Log.d("HEADER", "Check 2");
			header.setText(""+c.get(Calendar.DAY_OF_WEEK));
		}
	}
}
