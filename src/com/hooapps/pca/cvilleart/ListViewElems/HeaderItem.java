package com.hooapps.pca.cvilleart.ListViewElems;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.hooapps.pca.cvilleart.R;
import com.hooapps.pca.cvilleart.ListViewElems.ItemArrayAdapter.Type;

/**
* Item for the custom ListView in the NavDrawer. Acts as a separator to divide
* sections of the ListView into subsections.
* 
* @author Spencer Gennari
*/

public class HeaderItem implements Item {
	private final String text;
	
	

	public HeaderItem(String text) {
		this.text = text;
	}
	
	/**
	 * Returns the type of View based on the Type enum
	 * 
	 * @return The ordinal from the Type enum
	 */
	@Override
	public int getViewType() {
		return Type.HEADER_ITEM.ordinal();
	}
	
	/**
	 * Retrieves or creates a view (depending on the situation) that is then
	 * populated with data.
	 * 
	 * @return The updated View with the new information
	 */
	@Override
	public View getView(LayoutInflater inflater, View convertView) {
		View view;
		if (convertView == null) {
			view = (View) inflater.inflate(R.layout.drawer_list_header, null);
		} else {
			view = convertView;
		}
		
		TextView header = (TextView) view.findViewById(R.id.header);
		header.setText(this.text);
		
		return view;
	}

}
