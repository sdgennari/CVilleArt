package com.hooapps.pca.cvilleart.ListViewElems;

import com.hooapps.pca.cvilleart.R;
import com.hooapps.pca.cvilleart.DataElems.EventTable;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class EventCursorAdapter extends SimpleCursorAdapter {
	
	private LayoutInflater layoutInflater;
	private Context context;
	private int layout;
	private double screenDensity;
	
	public EventCursorAdapter(Context context, int layout, Cursor cursor, String[] from, int[] to) {
		super(context, layout, cursor, from, to, 0);
		this.context = context;
		this.layout = layout;
		layoutInflater = LayoutInflater.from(context);
		screenDensity = context.getResources().getDisplayMetrics().density;
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
		String startTime = cursor.getString(colStartTime);
		
		// Display the data in the view in the list
		TextView titleView = (TextView) v.findViewById(R.id.event_title);
		titleView.setText(summary);
		
		TextView categoryView = (TextView) v.findViewById(R.id.category);
		categoryView.setText(category);
	}
}
