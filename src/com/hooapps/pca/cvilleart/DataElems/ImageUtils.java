package com.hooapps.pca.cvilleart.DataElems;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Locale;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Picasso.LoadedFrom;

public class ImageUtils {

	private static final ImageUtils INSTANCE = new ImageUtils();

	private ImageUtils() {}

	public static ImageUtils getInstance() {
		return INSTANCE;
	}

	public static final String THUMB_PATH = "thumbnails";
	public static final String BLUR_PATH = "blurred_images";

	private static Context context;
	private static File thumbDir;
	private static File blurDir;

	
	/**
	 * Method to initialize the instance of ImageUtils 
	 * @param initContext The context of the application
	 * @param initThumbDir The directory of the thumbnails
	 * @param initBlurDir The directory of the blurred images
	 */
	public static void initialize(Context initContext, File initThumbDir, File initBlurDir) {
		context = initContext;
		thumbDir = initThumbDir;
		blurDir = initBlurDir;
	}
	
	/**
	 * Method to save thumbnails for each image
	 * @param sourcePath The url of the image
	 * @param venueName The name of the venue, used for the file path
	 * @param width The desired width of the image thumb
	 * @param height The desired height of the image thumb
	 * @return The absolute path of the saved image, null if the source was not valid
	 */
	public String saveImageThumb(String sourcePath, String venueName, int width, int height) {
		String formattedName = venueName.toLowerCase(Locale.ENGLISH).trim().replace(' ', '_');
		final File thumb = new File(thumbDir, formattedName + "_thumb.png");

		Target target = new Target() {
			@Override
			public void onBitmapFailed(Drawable arg0) {
				Log.d("FAIL", "Bitmap failed to load");
			}

			@Override
			public void onBitmapLoaded(Bitmap bitmap, LoadedFrom from) {
				try {
					FileOutputStream out = new FileOutputStream(thumb);
					bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
					out.flush();
					out.close();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onPrepareLoad(Drawable arg0) {
				// TODO Auto-generated method stub
			}

		};

		if(sourcePath != null && !sourcePath.isEmpty()) {
			Picasso.with(context).load(sourcePath).resize(width, height).centerCrop().into(target);
			return thumb.getPath();
		}
		
		return null;
	}
}
