package com.hooapps.pca.cvilleart.ListViewElems;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hooapps.pca.cvilleart.R;
import com.hooapps.pca.cvilleart.CustomElems.RoundedImageView;
import com.hooapps.pca.cvilleart.ListViewElems.ItemArrayAdapter.Type;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

/**
 * Item for the ListView in the Discover section of the App. Holds an image, 
 * title, and description of the type of attraction displayed.
 * 
 * @author Spencer Gennari
 */

public class DiscoverItem implements Item {
	private final String title;
	private final String type;
	private final String imagePath;
	private ImageView imageView = null;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	
	public DiscoverItem(String title, String type, String imagePath) {
		this.title = title;
		this.type = type;
		this.imagePath = imagePath;
	}
	
	/**
	 * Returns the type of View based on the Type enum
	 * 
	 * @return The ordinal from the Type enum
	 */
	@Override
	public int getViewType() {
		return Type.DISCOVER_ITEM.ordinal();
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
			view = (View) inflater.inflate(R.layout.discover_list_item, null);
		} else {
			view = convertView;
		}
		
		imageView = (RoundedImageView) view.findViewById(R.id.image);

		// TODO CACHE IMAGES
		// Async load the the images then set them in the ListView
		imageLoader.loadImage(imagePath, new SimpleImageLoadingListener() {
		    @Override
		    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
		        // Do whatever you want with Bitmap
				imageView.setImageBitmap(loadedImage);
		    }
		});

		TextView titleView = (TextView) view.findViewById(R.id.venue_title);
		titleView.setText(this.title);
		
		TextView typeView = (TextView) view.findViewById(R.id.type);
		typeView.setText(this.type);
		
		return view;
	}
	
}
