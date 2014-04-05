package com.hooapps.pca.cvilleart.DataElems;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class AsyncImageSaver extends AsyncTask<HashMap<String, String>, Void, Void> {
	
	private static final int THUMB_SIZE = 48 * 3;
	private static final int BLUR_WIDTH = 800;
	private static final int BLUR_HEIGHT = 400;
	
	private Context context;
	private ImageUtils utils;
	
	/*
	ImageUtils utils = ImageUtils.getInstance();
	String path = utils.saveImageThumb(imagePath, name, width, height);
	
	ContentValues values = new ContentValues();
	values.put(VenueTable.IMAGE_THUMB_PATH, path);
	context.getContentResolver().update(PCAContentProvider.VENUE_CONTENT_URI, values, VenueTable.ORGANIZATION_NAME+" = ?", new String[] {name});
	*/
	
	public AsyncImageSaver(Context context) {
		this.context = context;
		this.utils = ImageUtils.getInstance();
	}
	
	@Override
	protected void onPreExecute() {}
	
	@Override
	protected Void doInBackground(HashMap<String, String>... nameUrlPairs) {
		
		// Loop through all the maps
		for (HashMap<String, String> map : nameUrlPairs) {
			
			// Loop through all the organizations in each map
			for (String name : map.keySet()) {
				
				// Make sure the task has not been canceled
				if (this.isCancelled()) {
					break;
				}
				
				// Save the image thumb to the phone memory
				utils.saveImageThumb(map.get(name), name, THUMB_SIZE, THUMB_SIZE);
				utils.saveImageBlur(map.get(name), name, BLUR_WIDTH, BLUR_HEIGHT);
			}
		}
		
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {}
	
	@Override
	protected void onProgressUpdate(Void... values) {}
	
}
