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

public class DiscoverListFragment extends ListFragment {
	
	OnDiscoverViewSelectedListener mCallback;
	
	
	// List of items for the ItemArrayAdapter in the Fragment ListView
	private List<Item> venueList = new ArrayList<Item>();
	
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
		mCallback.onDiscoverViewSelected(l, v, position, id);
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
		
		String[] imageUrls = {"http://directories.charlottesvillearts.org/container.php?file=title.jpg&lay=PHP%20-%20Primary%20Image&recid=4872&field=Directory_Primary_Image",
				"http://directories.charlottesvillearts.org/container.php?file=streetView.jpg&lay=PHP%20-%20Primary%20Image&recid=4907&field=Directory_Primary_Image",
				"http://directories.charlottesvillearts.org/container.php?file=sign.jpg&lay=PHP%20-%20Primary%20Image&recid=6926&field=Directory_Primary_Image",
				"http://directories.charlottesvillearts.org/container.php?file=filename-351-507749145306.jpg&lay=PHP%20-%20Primary%20Image&recid=7384&field=Directory_Primary_Image",
				"http://directories.charlottesvillearts.org/container.php?file=Screen+shot+2013-06-24+at+9.48.02+AM.png&lay=PHP%20-%20Primary%20Image&recid=4819&field=Directory_Primary_Image"
				};
		
		String[] descriptions ={"The purpose of the Humanities, Fine Arts & Social Sciences Division is to offer world-class coursework applicable to the needs of the students whether their goal is transfer to a four-year college, moving into a college curriculum, improving job skills or fostering artistic abilities for personal development.",
				"For most of the middle of the 20th century, Charlottesville’s Paramount Theater had taken a leading role in the community. Created by Chicago architectural firm Rapp & Rapp, architects of The Paramount-Publix chain and its flagship theater in New York City’s Times Square, Charlottesville’s Paramount was part of the golden age of cinema.",
				"FIREFISH Gallery, located on the Downtown Mall in Charlottesville, hosts a variety of classes for adults, teens, and children. Mosaic lessons teach participants how to create decorative objects of art using rich colors, reflective surfaces and timeless permanence.",
				"Para Cofee is located on the Corner near the University of Virginia. It is a coffee shop that hosts occasional concerts and art shows.",
				"Founded in 1973, Four County Players is Central Virginia's longest continuously operating community theater. For more than 35 years, the players have delighted audiences with a full range of theater experiences."
				};
		
		String[] addresses ={"V. Earl Dickinson Building, 501 College Drive, Charlottesville, VA 22902",
				"215 East Main St., Charlottesville, VA 22902",
				"108 2nd St NW, Charlottesville, VA 22902",
				"19 Elliewood Avenue, Charlottesville, VA 22903",
				"5256 Governor Barbour St., Barboursville, VA 22923"
				};
		
		//TODO Replace titles.length with the dynamic query length
		DiscoverItem item = null;
		for(int i=0; i<titles.length; i++) {
			item = new DiscoverItem(titles[i], types[i], imageUrls[i], descriptions[i], addresses[i]);
			venueList.add(item);
		}
	}
}
