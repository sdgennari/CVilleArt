/*
 * Adapted from the online code samples found at:
 * http://developer.android.com/training/basics/fragments/index.html
 * 
 * Heavily modified for use within this specific app,
 * but always give credit where credit is due.
 */
package com.hooapps.pca.cvilleart;

import java.util.ArrayList;

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
import com.hooapps.pca.cvilleart.DataElems.PCAContentProvider;
import com.hooapps.pca.cvilleart.DataElems.VenueTable;
import com.hooapps.pca.cvilleart.ListViewElems.DiscoverCursorAdapter;
import com.hooapps.pca.cvilleart.ListViewElems.Item;
import com.hooapps.pca.cvilleart.ListViewElems.ItemArrayAdapter;

// TODO Update the JavaDoc description as the functionality increases

/**
 * Fragment to list various art attractions in CVille. [MORE HERE]
 * 
 * @author Spencer Gennari
 * 
 */

public class DiscoverListFragment extends ListFragment implements
		LoaderManager.LoaderCallbacks<Cursor> {

	OnDiscoverViewSelectedListener mCallback;
	private DiscoverCursorAdapter dataAdapter;

	public interface OnDiscoverViewSelectedListener {
		/**
		 * Called by DiscoverListFragment when a view is selected
		 * 
		 * @param position The position of the view in the list
		 * */
		public void onDiscoverViewSelected(ListView l, View v, int position, long id);
		public ArrayList<Item> getVenueItemList();
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
	 * @param l
	 *            The parent ListView that detected the click
	 * @param v
	 *            The child View that was clicked
	 * @param position
	 *            The position of the View within the ListView
	 * @param id
	 *            The row id of the View that was clicked
	 */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// Notify the parent activity
		mCallback.onDiscoverViewSelected(l, v, position, id);
	}

	private void loadListView() {
		
		String[] columns = new String[] {
				VenueTable.ORGANIZATION_NAME,
				VenueTable.DIRECTORY_DESCRIPTION_LONG,
				VenueTable.IMAGE_URLS,
				VenueTable.CATEGORY_ART_COMMUNITY_CATEGORIES,
				VenueTable.ADDRESS_HOME_STREET
		};
		
		int[] to = new int[] { R.id.venue_name, /*R.id.type,*/ R.id.image };

		dataAdapter = new DiscoverCursorAdapter(this.getActivity(), R.layout.discover_list_item, null, columns, to);

		this.setListAdapter(dataAdapter);
		this.getActivity().getSupportLoaderManager().initLoader(PCAContentProvider.VENUE_ADAPTER_ID, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		
		String[] projection = new String[] {
				VenueTable.COLUMN_ID,
				VenueTable.ORGANIZATION_NAME,
				VenueTable.DIRECTORY_DESCRIPTION_LONG,
				VenueTable.CATEGORY_ART_COMMUNITY_CATEGORIES,
				VenueTable.IMAGE_URLS,
				VenueTable.ADDRESS_HOME_STREET,
				VenueTable.IMAGE_THUMB_PATH
		};

		CursorLoader cursorLoader = new CursorLoader(this.getActivity(), PCAContentProvider.VENUE_CONTENT_URI, projection,
				VenueTable.IS_DELETED + " = 0", null, VenueTable.CATEGORY_ART_COMMUNITY_CATEGORIES + " ASC, UPPER(" + VenueTable.ORGANIZATION_NAME + ") ASC");
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
