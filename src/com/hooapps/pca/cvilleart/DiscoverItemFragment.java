/*
 * Adapted from the online code samples found at:
 * http://developer.android.com/training/basics/fragments/index.html
 * 
 * Heavily modified for use within this specific app,
 * but always give credit where credit is due.
 */
package com.hooapps.pca.cvilleart;

import android.app.Activity;
import android.content.Context;
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
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hooapps.pca.cvilleart.R;
import com.hooapps.pca.cvilleart.CustomElems.BlurTransformation;
import com.hooapps.pca.cvilleart.CustomElems.RoundedImageView;
import com.hooapps.pca.cvilleart.DataElems.PCAContentProvider;
import com.hooapps.pca.cvilleart.DataElems.PCADatabaseHelper;
import com.hooapps.pca.cvilleart.DataElems.VenueTable;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.squareup.picasso.Picasso;

// TODO Update the JavaDoc description as the functionality increases

/**
 * Fragment to display information about various attractions in CVille. Loaded 
 * using info from items in the DiscoverListFragment.
 * [MORE HERE]
 * 
 * @author Spencer Gennari
 */

public class DiscoverItemFragment extends Fragment {
	
	//private ImageView imageView;
	//private ImageLoader imageLoader = ImageLoader.getInstance();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// If the activity is being recreated, restore previous version
		// Mostly, this is needed in a two-pane layout
		if (savedInstanceState != null) {
			// TODO Code to restore prior version here  
		}
		
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.discover_item_view, container, false);
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
		} else {
			// TODO Setup the fragment according to other specifications
		}
	}
	
	/**
	 * Updates the views on the fragment with information about the venue.
	 * TODO UPDATE THIS DESCRIPTION
	 * @param args The Bundle passed to the fragment
	 */
	private void updateDiscoverItem(Bundle args) {
		// Retrieve the data from the args bundle
		//int id = args.getInt("column_ID");
		Uri uri = args.getParcelable(PCAContentProvider.VENUE_CONTENT_ITEM_TYPE);
		
		String[] projection = {
				VenueTable.ORGANIZATION_NAME,
				VenueTable.CATEGORY_ART_COMMUNITY_CATEGORIES,
				VenueTable.DIRECTORY_DESCRIPTION_LONG,
				VenueTable.ADDRESS_HOME_STREET,
				VenueTable.IMAGE_URLS };
		
		Cursor cursor = this.getActivity().getContentResolver().query(uri, projection, null, null, null);
		
		if (cursor != null) {
			cursor.moveToFirst();
			
			// Get the data from the cursor
			String name = cursor.getString(cursor.getColumnIndex(VenueTable.ORGANIZATION_NAME));
			String type = cursor.getString(cursor.getColumnIndex(VenueTable.CATEGORY_ART_COMMUNITY_CATEGORIES));
			String description = cursor.getString(cursor.getColumnIndex(VenueTable.DIRECTORY_DESCRIPTION_LONG));
			String address = cursor.getString(cursor.getColumnIndex(VenueTable.ADDRESS_HOME_STREET));
			String imagePath = cursor.getString(cursor.getColumnIndex(VenueTable.IMAGE_URLS));
			
			// Retrieve the views from the layout
			Activity a = this.getActivity();
			TextView titleView = (TextView)a.findViewById(R.id.venue_name);
			TextView descriptionView = (TextView)a.findViewById(R.id.description);
			TextView addressView = (TextView)a.findViewById(R.id.address);
			ImageView imageView = (ImageView)a.findViewById(R.id.venue_image);
			ImageView bgImageView = (ImageView)a.findViewById(R.id.venue_image_background);
			
			titleView.setText(name);
			descriptionView.setText(description);
			addressView.setText(address);
			
			// Process the images
			// TODO FIND A WAY TO USE getWidth() AND getHeight() for ImageViews
			// LOOK INTO ViewTreeObserver
			Context context = this.getActivity().getApplicationContext();
			BlurTransformation blur = new BlurTransformation(context);
			double screenDensity = context.getResources().getDisplayMetrics().density;
			
			if (imagePath != null && !imagePath.isEmpty()) {
				Picasso.with(context).load(imagePath).resize((int)(336*screenDensity), (int)(112*screenDensity)).transform(blur).into(bgImageView);
				Picasso.with(context).load(imagePath).resize((int)(48*screenDensity), (int)(48*screenDensity)).centerCrop().into(imageView);
			} else {
				Picasso.with(context).load(R.drawable.film).resize((int)(336*screenDensity), (int)(112*screenDensity)).transform(blur).into(bgImageView);
				Picasso.with(context).load(R.drawable.theatre).resize((int)(48*screenDensity), (int)(48*screenDensity)).centerCrop().into(imageView);
			}
			
			/*
			imageLoader.loadImage(imagePath, new SimpleImageLoadingListener() {
			    @Override
			    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			        // Do whatever you want with Bitmap
			    	if(imageView != null) {
			    		//imageView.setImageBitmap(createImage(loadedImage));	
			    	}
			    }
			});
			*/
			
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
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		// TODO Save important info form the fragment here
		// Use the format 'outState.put[String/Boolean/Int/etc.](key, value);'
	}
}
