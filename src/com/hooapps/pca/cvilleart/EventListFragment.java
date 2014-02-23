/*
 * Adapted from the online code samples found at:
 * http://developer.android.com/training/basics/fragments/index.html
 * 
 * Heavily modified for use within this specific app,
 * but always give credit where credit is due.
 */
package com.hooapps.pca.cvilleart;

import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.hooapps.pca.cvilleart.R;
import com.hooapps.pca.cvilleart.DataElems.EventTable;
import com.hooapps.pca.cvilleart.DataElems.PCAContentProvider;
import com.hooapps.pca.cvilleart.ListViewElems.EventCursorAdapter;


// TODO Update the JavaDoc description as the functionality increases

/**
 * Fragment to display upcoming events in CVille in a calendar-type format.
 * [MORE HERE]
 * 
 * @author Spencer Gennari
 *
 */

public class EventListFragment extends ListFragment implements 
		LoaderManager.LoaderCallbacks<Cursor> {
	
	OnEventViewSelectedListener mCallback;
	private EventCursorAdapter dataAdapter;
	
	public interface OnEventViewSelectedListener {
		/**
		 * Called by EventListFragment when a view is selected
		 * 
		 * @param position The position of the view in the list
		 */
		public void OnEventViewSelected(ListView l, View v, int position, long id);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		loadListView();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			mCallback = (OnEventViewSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnEventViewSelectedListener");		}
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		mCallback.OnEventViewSelected(l, v, position, id);
	}
	
	private void loadListView() {
		
		String[] columns = new String[] {
				EventTable.EVENT_ID,
				EventTable.CATEGORY,
				EventTable.SUMMARY,
				EventTable.START_TIME
		};
		
		int[] to = new int[] { R.id.event_title, R.id.category};
		
		//dataAdapter = new EventCursorAdapter(this.getActivity().getApplicationContext(), R.layout.event_list_item, null, columns, to);
		dataAdapter = new EventCursorAdapter(this.getActivity(), R.layout.event_list_item, null, columns, to);
		
		this.setListAdapter(dataAdapter);
		this.getActivity().getSupportLoaderManager().initLoader(PCAContentProvider.EVENT_ADAPTER_ID, null, this);
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		
		String[] projection = new String[] {
			EventTable.COLUMN_ID,
			EventTable.EVENT_ID,
			EventTable.CATEGORY,
			EventTable.SUMMARY,
			EventTable.START_TIME
		};
		
		CursorLoader cursorLoader = new CursorLoader(this.getActivity(), PCAContentProvider.EVENT_CONTENT_URI, projection, null, null, null);
		return cursorLoader;
	}
	
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		dataAdapter.swapCursor(cursor);
	}
	
	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		dataAdapter.swapCursor(null);
	}
}


