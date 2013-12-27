/*
 * Adapted from the online code samples found at:
 * http://developer.android.com/training/basics/fragments/index.html
 * 
 * Heavily modified for use within this specific app,
 * but always give credit where credit is due.
 */
package com.hooapps.pca.cvilleart;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.hooapps.pca.cvilleart.R;
import com.hooapps.pca.cvilleart.ListViewElems.DiscoverItem;
import com.hooapps.pca.cvilleart.ListViewElems.Item;
import com.hooapps.pca.cvilleart.ListViewElems.ItemArrayAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

// TODO Update the JavaDoc description as the functionality increases

/**
 * Fragment to list various art attractions in CVille. [MORE HERE]
 * 
 * @author Spencer Gennari
 *
 */

public class DiscoverFragment extends ListFragment {
	
	OnDiscoverViewSelectedListener mCallback;
	
	
	// List of items for the ItemArrayAdapter in the Fragment ListView
	private List<Item> venueList = new ArrayList<Item>();
	
	public interface OnDiscoverViewSelectedListener {
		/**
		 * Called by DiscoverFragment when a view is selected
		 * 
		 * @param position The position of the view in the list
		 * */
		public void onDiscoverViewSelected(int position);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Use a preset android list item layout
		int layout = android.R.layout.simple_list_item_activated_1;
		
		// Load data into items for the list
		loadDataIntoList();
		
		// Create an array adapter for the list view
		ItemArrayAdapter adapter = new ItemArrayAdapter(this.getActivity().getApplicationContext(), venueList);
		setListAdapter(adapter);
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
		mCallback.onDiscoverViewSelected(position);
	}
	
	public void loadDataIntoList() {
		// Currently hardcoded, to be loaded dynamically from the ContentProvider (when created)
		String[] titles = {"PVCC Fine Arts Department",
				"The Paramount Theater",
				"FIREFISH Gallery",
				"Para Coffee",
				"Four County Players"
				};
		
		String[] types = {"Dance",
				"Film",
				"Galleries",
				"Music",
				"Theatre"
				};
		
		String[] urls = {"http://directories.charlottesvillearts.org/container.php?file=title.jpg&lay=PHP%20-%20Primary%20Image&recid=4872&field=Directory_Primary_Image",
				"http://directories.charlottesvillearts.org/container.php?file=streetView.jpg&lay=PHP%20-%20Primary%20Image&recid=4907&field=Directory_Primary_Image",
				"http://directories.charlottesvillearts.org/container.php?file=sign.jpg&lay=PHP%20-%20Primary%20Image&recid=6926&field=Directory_Primary_Image",
				"http://directories.charlottesvillearts.org/container.php?file=filename-351-507749145306.jpg&lay=PHP%20-%20Primary%20Image&recid=7384&field=Directory_Primary_Image",
				"http://directories.charlottesvillearts.org/container.php?file=Screen+shot+2013-06-24+at+9.48.02+AM.png&lay=PHP%20-%20Primary%20Image&recid=4819&field=Directory_Primary_Image"
				};
		
		//TODO Replace titles.length with the dynamic query length
		DiscoverItem item = null;
		for(int i=0; i<titles.length; i++) {
			item = new DiscoverItem(titles[i], types[i], urls[i]);
			venueList.add(item);
		}
	}
}
