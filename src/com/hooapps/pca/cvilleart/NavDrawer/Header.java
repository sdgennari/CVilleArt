package com.hooapps.pca.cvilleart.NavDrawer;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.hooapps.pca.cvilleart.R;
import com.hooapps.pca.cvilleart.NavDrawer.ItemArrayAdapter.Type;

/**
* Item for the custom ListView in the NavDrawer. Acts as a separator to divide
* sections of the ListView into subsections.
* 
* @author Spencer Gennari
*/

public class Header implements Item {
	private final String text;
	
	public Header(String text) {
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
			view = (View) inflater.inflate(R.layout.header, null);
		} else {
			view = convertView;
		}
		
		TextView textView = (TextView) view.findViewById(R.id.separator);
		textView.setText(text);
		
		return view;
	}

}
