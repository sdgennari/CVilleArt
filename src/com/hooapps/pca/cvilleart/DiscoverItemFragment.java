/*
 * Adapted from the online code samples found at:
 * http://developer.android.com/training/basics/fragments/index.html
 * 
 * Heavily modified for use within this specific app,
 * but always give credit where credit is due.
 */
package com.hooapps.pca.cvilleart;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Bitmap.Config;
import android.net.Uri;
import android.os.Bundle;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.hooapps.pca.cvilleart.R;
import com.hooapps.pca.cvilleart.CustomElems.BlurTransformation;
import com.hooapps.pca.cvilleart.CustomElems.RoundedImageView;
import com.hooapps.pca.cvilleart.DataElems.ImageUtils;
import com.hooapps.pca.cvilleart.DataElems.PCAContentProvider;
import com.hooapps.pca.cvilleart.DataElems.PCADatabaseHelper;
import com.hooapps.pca.cvilleart.DataElems.VenueTable;
import com.hooapps.pca.cvilleart.DataElems.PCAContentProvider.Categories;
import com.hooapps.pca.cvilleart.DiscoverListFragment.OnDiscoverViewSelectedListener;
import com.hooapps.pca.cvilleart.ListViewElems.Item;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

// TODO Update the JavaDoc description as the functionality increases

/**
 * Fragment to display information about various attractions in CVille. Loaded 
 * using info from items in the DiscoverListFragment.
 * [MORE HERE]
 * 
 * @author Spencer Gennari
 */

public class DiscoverItemFragment extends Fragment {
	
	public interface OnDiscoverItemSelectedOpenMapListener {
		/**
		 * Called by DiscoverItemFragment when a view is selected
		 * 
		 * @param id The id of the venue
		 * */
		public void onDiscoverItemSelectedOpenMap(int id);
	}
	
	private OnDiscoverItemSelectedOpenMapListener mCallback;
	
	//private ImageView imageView;
	//private ImageLoader imageLoader = ImageLoader.getInstance();
	private int id;
	private ImageUtils imageUtils;
	private String latLngString;
	
	private static final String GOOGLE_MAP_BASE = "http://maps.google.com/maps?saddr=&daddr=";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// If the activity is being recreated, restore previous version
		// Mostly, this is needed in a two-pane layout
		if (savedInstanceState != null) {
			// TODO Code to restore prior version here  
		}
		
		Log.d("ON_CREATE_VIEW", "View created");
		
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.discover_item_view, container, false);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		imageUtils = ImageUtils.getInstance();

		// Make sure that the activity implements OnViewSelectedListener
		// If not, throw an exception
		try {
			mCallback = (OnDiscoverItemSelectedOpenMapListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnDiscoverItemSelectedOpenMapListener");
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		
		// During startup, check if there are arguments passed to the fragment.
        // onStart is a good place to do this because the layout has already been
        // applied to the fragment at this point so we can safely call the method
        // below that sets the article text.
		Bundle args = getArguments();
		if (args != null) {
			updateDiscoverItem(args);
			bindListeners();
		} else {
			// TODO Setup the fragment according to other specifications
		}
	}
	
	private void bindListeners() {
		Button viewMapButton = (Button)this.getActivity().findViewById(R.id.view_map_button);
		viewMapButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCallback.onDiscoverItemSelectedOpenMap(id);
			}
		});
		
		Button getDirectionsButton = (Button)this.getActivity().findViewById(R.id.get_directions_button);
		getDirectionsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				launchGoogleMapIntent();
			}
		});
	}
	
	/**
	 * Updates the views on the fragment with information about the venue.
	 * TODO UPDATE THIS DESCRIPTION
	 * @param args The Bundle passed to the fragment
	 */
	private void updateDiscoverItem(Bundle args) {
		// Retrieve the data from the args bundle
		Uri uri = args.getParcelable(PCAContentProvider.VENUE_CONTENT_ITEM_TYPE);
		id = Integer.parseInt(uri.getLastPathSegment());
		
		String[] projection = {
				VenueTable.ORGANIZATION_NAME,
				VenueTable.DIRECTORY_DESCRIPTION_LONG,
				VenueTable.ADDRESS_HOME_STREET,
				VenueTable.CATEGORY_ART_COMMUNITY_CATEGORIES,
				VenueTable.SECONDARY_CATEGORY,
				VenueTable.PHONE_NUMBER_PRIMARY,
				VenueTable.LAT_LNG_STRING,
				VenueTable.IMAGE_URLS };
		
		Cursor cursor = this.getActivity().getContentResolver().query(uri, projection, null, null, null);
		
		if (cursor != null) {
			cursor.moveToFirst();
			
			// Get the data from the cursor
			String name = cursor.getString(cursor.getColumnIndex(VenueTable.ORGANIZATION_NAME));
			String description = cursor.getString(cursor.getColumnIndex(VenueTable.DIRECTORY_DESCRIPTION_LONG));
			String address = cursor.getString(cursor.getColumnIndex(VenueTable.ADDRESS_HOME_STREET));
			String imagePath = cursor.getString(cursor.getColumnIndex(VenueTable.IMAGE_URLS));
			String categoryString = cursor.getString(cursor.getColumnIndex(VenueTable.CATEGORY_ART_COMMUNITY_CATEGORIES));
			String secondaryCategoryString = cursor.getString(cursor.getColumnIndex(VenueTable.SECONDARY_CATEGORY));
			String phoneNumber = cursor.getString(cursor.getColumnIndex(VenueTable.PHONE_NUMBER_PRIMARY));
			latLngString = cursor.getString(cursor.getColumnIndex(VenueTable.LAT_LNG_STRING));
			
			// Retrieve the views from the layout
			Activity a = this.getActivity();
			TextView titleView = (TextView)a.findViewById(R.id.venue_name);
			TextView descriptionView = (TextView)a.findViewById(R.id.description);
			TextView addressView = (TextView)a.findViewById(R.id.address);
			ImageView imageView = (ImageView)a.findViewById(R.id.venue_image);
			ImageView bgImageView = (ImageView)a.findViewById(R.id.venue_image_background);
			TextView categoryView = (TextView)a.findViewById(R.id.category);
			TextView secondaryCategoryView = (TextView)a.findViewById(R.id.secondary_category);
			TextView phoneView = (TextView)a.findViewById(R.id.phone_number);
			
			// Retrieve the view containers from the layout
			LinearLayout addressContainer = (LinearLayout)a.findViewById(R.id.address_container);
			LinearLayout eventContainer = (LinearLayout)a.findViewById(R.id.event_container);
			RelativeLayout imageContainer = (RelativeLayout)a.findViewById(R.id.image_container);
			LinearLayout categoryContainer = (LinearLayout)a.findViewById(R.id.category_container);
			LinearLayout secondaryCategoryContainer = (LinearLayout)a.findViewById(R.id.secondary_category_container);
			LinearLayout phoneContainer = (LinearLayout)a.findViewById(R.id.phone_number_container);
			
			// Set the fields
			titleView.setText(name);
			descriptionView.setText(description);
			categoryView.setText(categoryString);
			
			// Hide the address container if no address is provided
			if (address == null || address.isEmpty()) {
				addressContainer.setVisibility(View.GONE);
			} else {
				addressView.setText(address);
			}
			
			// Hide the Secondary Category if it does not exist
			if (secondaryCategoryString == null || secondaryCategoryString.isEmpty()) {
				secondaryCategoryContainer.setVisibility(View.GONE);
			} else {
				secondaryCategoryView.setText(secondaryCategoryString);
			}
			
			// Hide the phone number if it does not exist
			if (phoneNumber == null || phoneNumber.isEmpty()) {
				phoneContainer.setVisibility(View.GONE);
			} else {
				phoneView.setText(phoneNumber.trim().replace(' ', '-'));
			}
			
			// TODO ADD EVENTS ACCORDINGLY
			if (true) {
				eventContainer.setVisibility(View.GONE);
			}
			
			// Process the images
			Context context = this.getActivity().getApplicationContext();
			BlurTransformation blurTrans = new BlurTransformation(context);
			
			// Set the color based on the category
			int colorResId = 0;
			int drawableResId = 0;
			Categories category = Categories.valueOf(categoryString.replace(' ', '_').toUpperCase(Locale.ENGLISH));
			switch (category) {
			case DANCE: colorResId = R.color.green;
				drawableResId = R.drawable.dance;
				break;
			case MUSIC: colorResId = R.color.orange;
				drawableResId = R.drawable.music;
				break;
			case THEATRE: colorResId = R.color.purple;
				drawableResId = R.drawable.theatre;
				break;
			case GALLERY: colorResId = R.color.blue;
				drawableResId = R.drawable.gallery;
				break;
			case VENUE:
			default: colorResId = R.color.indigo;
				drawableResId = R.drawable.other;
				break;
			}
			
			// Set the default color of the image container background
			imageContainer.setBackgroundResource(colorResId);
			
			// If an image exists, load it
			// Else load the placeholder image
			File blur = imageUtils.getBlurPath(name);
			
			if (blur.exists()) {
				Log.d("BLUR", "PHONE");
				Picasso.with(context).load(blur).into(bgImageView);
			} else if (imagePath != null && !imagePath.isEmpty()) {
				Log.d("BLUR", "URL");
				Picasso.with(context).load(imagePath).resize(800, 400).centerCrop().transform(blurTrans).into(bgImageView);
			} else {
				//Picasso.with(context).load(drawableResId).transform(blur).into(bgImageView);
				//bgImageView.setBackgroundResource(colorResId);
				Log.d("BLUR", "DEFAULT");
				Picasso.with(context).load(drawableResId).into(imageView);
			}
			
			cursor.close();
		}
	}
	
	/**
	 * Creates the bitmap for the ImageView at the top of the screen. Combines 
	 * a blurred background image with the circular one from the ListView into
	 * on single image.
	 * 
	 * @param bmp The source bitmap
	 * @return The finished bitmap with all elements combined
	 */
	/*
	private Bitmap createImage(Bitmap bmp) {
		// Retrieve the blurred background image
		Bitmap background = processBackgroundImage(bmp);
		
		// Retrieve the circular image with border
		Bitmap main = RoundedImageView.scaleAndCropBitmap(bmp);
		main = RoundedImageView.getModifiedBitmap(main);
		
		// Combine the images into a single bitmap
		Bitmap output = Bitmap.createBitmap(imageView.getWidth(), imageView.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		//Paint paint = new Paint();
		
		// Draw the background image
		canvas.drawBitmap(background, 0, 0, null);
		
		// Draw the main image, horizontally centered on the canvas
		canvas.drawBitmap(main, (canvas.getWidth()-main.getWidth())/2, 0, null);
		
		return output;
	}
	*/
	
	/**
	 * Modifies the source bitmap to crop, scale, and blur it to form the 
	 * background image for the ImageView.
	 * 
	 * @param bmp The unmodified source bitmap image
	 * @return A cropped and blurred version of the bitmap
	 */
	/*
	private Bitmap processBackgroundImage(Bitmap bmp) {
		int width = imageView.getWidth();
		int height = imageView.getHeight();
		double ratio = width/height;
		
		int bmpW = bmp.getWidth();
		int bmpH = bmp.getHeight();
		
		Bitmap dest;
		// If the width is larger, scale based on height
		// Else, scale based on width
		if(bmpW/ratio < bmpH) {
			int h_new = (int)(bmpW/ratio);
			dest = Bitmap.createBitmap(bmp, 0, (bmpH-h_new)/2, bmpW, h_new);
		} else {
			int w_new = (int)(bmpH*ratio);
			dest = Bitmap.createBitmap(bmp, (bmpW-w_new)/2, 0, w_new, bmpH);
		}
		
		dest = Bitmap.createScaledBitmap(dest, width, height, false);
		
		// Blur the background image
		// TODO Make sure this isn't too computationally heavy...
		RenderScript rs = RenderScript.create(this.getActivity().getApplicationContext());
		Allocation input = Allocation.createFromBitmap(rs, dest);
		Allocation output = Allocation.createTyped(rs, input.getType());
		
		ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
		blurScript.setInput(input);
		blurScript.setRadius(20);
		blurScript.forEach(output);
		output.copyTo(dest);
		
		return dest;
	}
	*/
	
	private void launchGoogleMapIntent() {
		
		// Make sure the latLngString is valid
		if (latLngString == null || latLngString.isEmpty()) {
			return;
		}
		
		// Encode the uri
		String uri = GOOGLE_MAP_BASE + latLngString;
		
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
		intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
		startActivity(intent);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		// TODO Save important info form the fragment here
		// Use the format 'outState.put[String/Boolean/Int/etc.](key, value);'
	}
}
