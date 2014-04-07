package com.hooapps.pca.cvilleart.DataElems;

import java.util.HashMap;
import java.util.Map;

import com.hooapps.pca.cvilleart.MainActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class AsyncImageSaver extends AsyncTask<HashMap<String, String>, Void, Void> {
	
	private static final int THUMB_SIZE = 48 * 2;
	private static final int BLUR_WIDTH = 800;
	private static final int BLUR_HEIGHT = 400;
	
	private Context context;
	private ImageUtils utils;
	
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
				//Log.d("IMAGE", "Saved image for " + name);
			}
		}
		
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		// Update the timestamp of last modified
		SharedPreferences prefs = context.getSharedPreferences("com.hooapps.pca", Context.MODE_PRIVATE);
		prefs.edit().putLong(MainActivity.LAST_DATA_UPDATE, System.currentTimeMillis()).apply();
	}
	
	@Override
	protected void onProgressUpdate(Void... values) {}
	
}
