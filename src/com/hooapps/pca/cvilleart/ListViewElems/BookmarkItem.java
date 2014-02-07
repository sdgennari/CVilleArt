package com.hooapps.pca.cvilleart.ListViewElems;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hooapps.pca.cvilleart.R;
import com.hooapps.pca.cvilleart.ListViewElems.ItemArrayAdapter.Type;

public class BookmarkItem implements Item {

	@Override
	public int getViewType() {
		return Type.BOOKMARK_ITEM.ordinal();
	}

	@Override
	public View getView(LayoutInflater inflater, View convertView) {
		View view;
		if (convertView == null) {
			view = (View) inflater.inflate(R.layout.drawer_list_bookmark, null);
		} else {
			view = convertView;
		}
		
		//Load in Correct Images based on shared preferences
		
		return view;
	}

}
