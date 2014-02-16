package com.hooapps.pca.cvilleart;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

public class AsyncJSONLoader extends AsyncTask<String, Void, String> {
	
	private AsyncJSONLoaderListener mCallback;
	
	public interface AsyncJSONLoaderListener {
		public void storeJSONData(String result);
	}
	
	public void setActivity(Activity activity) {
		try {
			mCallback = (AsyncJSONLoaderListener) activity;
		} catch (Exception e) {
			Log.d("setActivity", "Error: " + e.getLocalizedMessage());
		}
	}
	
	protected String doInBackground(String... urls) {
		return readJSONFeed(urls[0]);
	}
	
	protected void onPostExecute(String result) {
		try {
			mCallback.storeJSONData(result);
			
		} catch (Exception e) {
			Log.d("AsyncJSONLoader", e.getLocalizedMessage());
		}
	}
	
	private String readJSONFeed(String url) {
		StringBuilder stringBuilder = new StringBuilder();
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		
		try {
			HttpResponse response = httpClient.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			
			if(statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream inputStream = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
				String line;
				while ((line = reader.readLine()) != null) {
					stringBuilder.append(line);
				}
				inputStream.close();
			} else {
				Log.d("JSON", "Failed to load in JSON");
			}
		} catch (Exception e) {
			Log.d("ReadJSONFeed", "Error: " + e.getLocalizedMessage());
		}
		return stringBuilder.toString();
	}
}
