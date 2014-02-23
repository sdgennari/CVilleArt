package com.hooapps.pca.cvilleart;

import com.hooapps.pca.cvilleart.DataElems.EventTable;
import com.hooapps.pca.cvilleart.DataElems.PCAContentProvider;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class EventItemFragment extends Fragment {
	
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
		} else {
			// TODO Setup the fragment according to other specifications
		}
	}
	
	private void updateEventItem(Bundle args) {
		Uri uri = args.getParcelable(PCAContentProvider.EVENT_CONTENT_ITEM_TYPE);
		
		String[] projection = {
				EventTable.SUMMARY,
				EventTable.DESCRIPTION,
				EventTable.START_TIME,
				EventTable.END_TIME,
				EventTable.LOCATION,
				EventTable.EVENT_ID
		};
		
		Cursor cursor = this.getActivity().getContentResolver().query(uri, projection, null, null, null);
		
		if (cursor != null) {
			cursor.moveToFirst();
			
			// Get the data from the cursor
			String summary = cursor.getString(cursor.getColumnIndex(EventTable.SUMMARY));
			String description = cursor.getString(cursor.getColumnIndex(EventTable.DESCRIPTION));
			String startTime = cursor.getString(cursor.getColumnIndex(EventTable.START_TIME));
			String endTime = cursor.getString(cursor.getColumnIndex(EventTable.END_TIME));
			String location = cursor.getString(cursor.getColumnIndex(EventTable.LOCATION));
			String eventId = cursor.getString(cursor.getColumnIndex(EventTable.EVENT_ID));
			
			// Retrieve the views from the layout
			Activity a = this.getActivity();
			// TODO LOAD ALL VIEWS HERE
			TextView descriptionView = (TextView) a.findViewById(R.id.description);
			TextView addressView = (TextView) a.findViewById(R.id.address);
			
			// Set the fields
			descriptionView.setText(description);
			addressView.setText(location);
			
			cursor.close();
		}
	}
}
