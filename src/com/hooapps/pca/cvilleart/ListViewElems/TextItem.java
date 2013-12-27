package com.hooapps.pca.cvilleart.ListViewElems;

import com.hooapps.pca.cvilleart.R;
import com.hooapps.pca.cvilleart.ListViewElems.ItemArrayAdapter.Type;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
* Item for the custom ListView in the NavDrawer. Holds basic text entries with
* icons.

* @author Spencer Gennari
*/

public class TextItem implements Item {
	private final String text;
	// TODO Add an icon here
	
	public TextItem(String text) {
		this.text = text;
	}
	
	/**
	 * Returns the type of View based on the Type enum
	 * 
	 * @return The ordinal from the Type enum
	 */
	@Override
	public int getViewType() {
		return Type.TEXT_ITEM.ordinal();
	}
	
	/**
	 * Retrieves or creates a view (depending on the situation) that is then
	 * populated with data.
	 * 
	 * @return The updated View with the new information
	 */
	@Override
	public View getView(LayoutInflater inflater, View convertView)
	{
		View view;
		if (convertView == null) {
			view = (View) inflater.inflate(R.layout.drawer_list_item, null);
		} else {
			view = convertView;
		}
		
		TextView item = (TextView) view.findViewById(R.id.item);
		item.setText(this.text);
		TextView arrow = (TextView) view.findViewById(R.id.arrow);
		arrow.setText(">");
		
		return view;
	}
}