package com.hooapps.pca.cvilleart.ListViewElems;

import java.io.File;
import java.io.IOException;

import org.apache.http.client.HttpClient;

import com.hooapps.pca.cvilleart.R;
import com.hooapps.pca.cvilleart.DataElems.VenueTable;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

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

	public DiscoverCursorAdapter(Context context, int layout, Cursor cursor, String[] from, int[] to) {
		super(context, layout, cursor, from, to, 0);
		this.context = context;
		this.layout = layout;
		layoutInflater = LayoutInflater.from(context);
		screenDensity = context.getResources().getDisplayMetrics().density;
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

		// Display the data on the views in the list
		TextView titleView = (TextView)v.findViewById(R.id.venue_title);
		titleView.setText(name);

		TextView typeView = (TextView)v.findViewById(R.id.type);
		typeView.setText(type);

		final ImageView imageView = (ImageView) v.findViewById(R.id.image);
		Log.d("IMAGE", "url: " + imagePath);
		
		// Make sure that the url is valid
		Picasso.with(context).setDebugging(true);
		if(imagePath != null && !imagePath.isEmpty()) {
			Picasso.with(context).load(imagePath).resize((int)(48*screenDensity), (int)(48*screenDensity)).centerCrop().placeholder(R.drawable.film).error(R.drawable.dance).into(imageView);
		} else {
			// Load a placeholder image if no url is provided
			Picasso.with(context).load(R.drawable.theatre).resize((int)(48*screenDensity), (int)(48*screenDensity)).centerCrop().into(imageView);
		}
	}
}
