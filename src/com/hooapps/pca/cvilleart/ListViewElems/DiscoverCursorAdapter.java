package com.hooapps.pca.cvilleart.ListViewElems;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import org.apache.http.client.HttpClient;

import com.hooapps.pca.cvilleart.R;
import com.hooapps.pca.cvilleart.DataElems.ImageUtils;
import com.hooapps.pca.cvilleart.DataElems.PCAContentProvider;
import com.hooapps.pca.cvilleart.DataElems.VenueTable;
import com.hooapps.pca.cvilleart.DataElems.PCAContentProvider.Categories;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.http.HttpResponseCache;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
	private double screenDensity;
	private ImageUtils imageUtils;

	public DiscoverCursorAdapter(Context context, int layout, Cursor cursor, String[] from, int[] to) {
		super(context, layout, cursor, from, to, 0);
		this.context = context;
		this.layout = layout;
		layoutInflater = LayoutInflater.from(context);
		screenDensity = context.getResources().getDisplayMetrics().density;
		this.imageUtils = ImageUtils.getInstance();
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
		//int colImgThumb = cursor.getColumnIndex(VenueTable.IMAGE_THUMB_PATH);

		// Retrieve the data from the cursor
		String name = cursor.getString(colName);
		String categoryString = cursor.getString(colType);
		String imagePath = cursor.getString(colImgUrl);

		// Display the data on the views in the list
		TextView titleView = (TextView)v.findViewById(R.id.venue_title);
		titleView.setText(name);
		
		/*
		TextView typeView = (TextView)v.findViewById(R.id.type);
		typeView.setText(categoryString);
		*/

		ImageView imageView = (ImageView) v.findViewById(R.id.image);
		
		// Set the image based on category
		int drawableResId = 0;
		Categories category = Categories.valueOf(categoryString.replace(' ', '_').toUpperCase(Locale.ENGLISH));
		switch (category) {
		case DANCE: drawableResId = R.drawable.dance;
			break;
		case MUSIC: drawableResId = R.drawable.music;
			break;
		case THEATRE: drawableResId = R.drawable.theatre;
			break;
		case GALLERY: drawableResId = R.drawable.gallery;
			break;
		case VENUE:
		default: drawableResId = R.drawable.other;
			break;
		}
		
		int width = imageView.getLayoutParams().width;
		int height = imageView.getLayoutParams().height;
		
		// Get the file for the thumbnail
		File thumb = imageUtils.getThumbPath(name); 
		if (thumb.exists()) {
			//Log.d("THUMB", "Loading from phone...");
			Picasso.with(context).load(thumb).placeholder(drawableResId).resize(width, height).centerCrop().into(imageView);
		} else if (imagePath != null && !imagePath.isEmpty()) {
			//Log.d("THUMB", "Loading from url...");
			Picasso.with(context).load(imagePath).placeholder(drawableResId).resize(width, height).centerCrop().into(imageView);
		} else {
			//Log.d("THUMB", "Loading from default...");
			// Load a placeholder image if no url is provided
			Picasso.with(context).load(drawableResId).placeholder(drawableResId).resize(width, height).centerCrop().into(imageView);
		}
		
		// Check to see if a header is needed
		RelativeLayout headerContainer = (RelativeLayout) v.findViewById(R.id.header_container);
		TextView header = (TextView) v.findViewById(R.id.header);
		headerContainer.setVisibility(View.GONE);
		
		// Set the header to the date
		int position = cursor.getPosition();
		if (position == 0) {
			headerContainer.setVisibility(View.VISIBLE);
			header.setText(categoryString);
		} else {
			// Get the previous date
			cursor.moveToPrevious();
			String prevCategoryString = cursor.getString(cursor.getColumnIndex(VenueTable.CATEGORY_ART_COMMUNITY_CATEGORIES));
			
			if (!prevCategoryString.equals(categoryString)) {
				headerContainer.setVisibility(View.VISIBLE);
				header.setText(categoryString);	
			}
		}
	}
}
