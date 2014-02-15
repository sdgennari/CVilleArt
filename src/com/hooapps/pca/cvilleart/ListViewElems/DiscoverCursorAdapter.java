package com.hooapps.pca.cvilleart.ListViewElems;

import com.hooapps.pca.cvilleart.R;
import com.hooapps.pca.cvilleart.CustomElems.RoundedImageView;
import com.hooapps.pca.cvilleart.DataElems.VenueTable;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Custom adapter for the ListView in the DiscoverListFragment. This adapter 
 * allows
 * 
 * @author Spencer Gennari
 *
 */

public class DiscoverCursorAdapter extends SimpleCursorAdapter {

	private LayoutInflater layoutInflater;
	private Context context;
	private int layout;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	
	@SuppressWarnings("deprecation")
	public DiscoverCursorAdapter(Context context, int layout, Cursor cursor, String[] from, int[] to) {
		super(context, layout, cursor, from, to);
		this.context = context;
		this.layout = layout;
		layoutInflater = LayoutInflater.from(context);
	}
	
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View v = layoutInflater.inflate(layout, parent, false);
		return v;
	}
	
	@Override
	public void bindView(View v, Context context, Cursor cursor) {
		// Retrieve the columns indexes from the cursor
		int colName = cursor.getColumnIndex(VenueTable.ORGANIZATION_NAME);
		int colType = cursor.getColumnIndex(VenueTable.CATEGORY_ART_COMMUNITY_CATEGORIES);
		int colImgUrl = cursor.getColumnIndex(VenueTable.IMAGE_URLS);
		int colAddress = cursor.getColumnIndex(VenueTable.ADDRESS_HOME_STREET);
		int colDescription = cursor.getColumnIndex(VenueTable.DIRECTORY_DESCRIPTION_LONG);
		
		// Retrieve the data from the cursor
		String name = cursor.getString(colName);
		String type = cursor.getString(colType);
		String imagePath = cursor.getString(colImgUrl);
		String address = cursor.getString(colAddress);
		String description = cursor.getString(colDescription);
		
		// Store the data in the view via the tags
		v.setTag(R.id.venue_title, name);
		v.setTag(R.id.type, type);
		v.setTag(R.id.image, imagePath);
		v.setTag(R.id.description, description);
		v.setTag(R.id.address, address);
		
		// Display the data on the views in the list
		TextView titleView = (TextView)v.findViewById(R.id.venue_title);
		titleView.setText(name);
		
		TextView typeView = (TextView)v.findViewById(R.id.type);
		typeView.setText(type);
		
		final ImageView imageView = (RoundedImageView)v.findViewById(R.id.image);
		// TODO CACHE IMAGES
		// Async load the the images then set them in the ListView
		imageLoader.loadImage(imagePath, new SimpleImageLoadingListener() {
		    @Override
		    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				//imageView.setImageBitmap(loadedImage);
		    }
		});
	}
}
