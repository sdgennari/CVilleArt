/*
 * Adapted from the online code samples found at:
 * http://developer.android.com/training/basics/fragments/index.html
 * 
 * Heavily modified for use within this specific app,
 * but always give credit where credit is due.
 */
package com.hooapps.pca.cvilleart;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;

import com.hooapps.pca.cvilleart.R;
import com.hooapps.pca.cvilleart.DataElems.DataProvider;
import com.hooapps.pca.cvilleart.DataElems.PCADatabase;
import com.hooapps.pca.cvilleart.ListViewElems.DiscoverCursorAdapter;

// TODO Update the JavaDoc description as the functionality increases

/**
 * Fragment to list various art attractions in CVille. [MORE HERE]
 * 
 * @author Spencer Gennari
 *
 */

public class DiscoverListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
	
	OnDiscoverViewSelectedListener mCallback;
	
	private DiscoverCursorAdapter dataAdapter;
	
	public interface OnDiscoverViewSelectedListener {
		/**
		 * Called by DiscoverListFragment when a view is selected
		 * 
		 * @param position The position of the view in the list
		 * */
		public void onDiscoverViewSelected(ListView l, View v, int position, long id);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		loadListView();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		// Make sure that the activity implements OnViewSelectedListener
		// If not, throw an exception
		try {
			mCallback = (OnDiscoverViewSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnViewSelectedListener");
		}
	}
	
	/**
	 * Called whenever an item in the list is selected
	 * 
	 * @param l	The parent ListView that detected the click
	 * @param v The child View that was clicked
	 * @param position The position of the View within the ListView
	 * @param id The row id of the View that was clicked  
	 */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// Notify the parent activity
		mCallback.onDiscoverViewSelected(l, v, position, id);
	}
	
	private void loadListView() {
		String[] columns = new String[] {
				PCADatabase.VENUE_NAME,
				PCADatabase.VENUE_DESCRIPTION,
				PCADatabase.VENUE_TYPE,
				PCADatabase.VENUE_IMG_URL,
				PCADatabase.VENUE_ADDRESS
		};
		
		int[] to = new int[] {
				R.id.venue_name,
				R.id.type,
				R.id.image
		};
		
		dataAdapter = new DiscoverCursorAdapter(this.getActivity().getApplicationContext(), R.layout.discover_list_item, null, columns, to);
		
		this.setListAdapter(dataAdapter);
		this.getActivity().getSupportLoaderManager().initLoader(0, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		String[] projection = new String[] {
				PCADatabase.VENUE_ID,
				PCADatabase.VENUE_NAME,
				PCADatabase.VENUE_DESCRIPTION,
				PCADatabase.VENUE_TYPE,
				PCADatabase.VENUE_IMG_URL,
				PCADatabase.VENUE_ADDRESS
		};
		
		CursorLoader cursorLoader = new CursorLoader(this.getActivity(), DataProvider.VENUE_CONTENT_URI, projection, null, null, null);
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
