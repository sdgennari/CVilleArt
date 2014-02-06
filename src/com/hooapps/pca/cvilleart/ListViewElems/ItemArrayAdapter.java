package com.hooapps.pca.cvilleart.ListViewElems;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
* Custom adapter for the ListView in the NavDrawer. This adapter allows for
* different sections within the list (as specified in the Type enum), such
* as headers or text entries
* 
* @author Spencer Gennari
*/

public class ItemArrayAdapter extends ArrayAdapter<Item>{
	private LayoutInflater mInflater;
	
	/** All the types of entries in the list */
	public enum Type {
		TEXT_ITEM, HEADER_ITEM, GRID_ITEM, VENUE_ITEM, BOOKMARK_ITEM
	}
	
	public ItemArrayAdapter(Context context, List<Item> items) {
		super(context, 0, items);
		mInflater = LayoutInflater.from(context);
	}
	
	/**
	 * Provides the number of possible View types
	 * 
	 * @return The length of Type enum
	 */
	@Override
	public int getViewTypeCount() {
		return Type.values().length;
	}
	
	/**
	 * Retrieves the type of view based on position in the ListView
	 * 
	 * @param position The position of the View in the ListView
	 * @return The ordinal of the View from the Type enum
	 */
	@Override
	public int getItemViewType(int position) {
		return getItem(position).getViewType();
	}
	
	/**
	 * Retrieves the View at a specified position in the ListView
	 * 
	 * @param position The position of the View in the ListView
	 * @param convertView The old view (could be reused)
	 * @param parent The parent View to which this one will be attached
	 * @return The View based on the data at specified position
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getItem(position).getView(mInflater, convertView);
	}
}
