package com.hooapps.pca.cvilleart;
/**
 * @author Alex Ramey
 * Big Time Thanks http://stackoverflow.com/questions/12585263/save-file-to-internal-memory-in-android
 * 
 */

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import com.hooapps.pca.cvilleart.DiscoverListFragment.OnDiscoverViewSelectedListener;

import jxl.Sheet;
import jxl.Workbook;
import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

public class AsyncExcelLoader extends AsyncTask<File, Void, File> {

	private AsyncExcelLoaderListener mCallback;
	
	public interface AsyncExcelLoaderListener
	{
		public void loadInData(File f);
	}
	@Override
	protected File doInBackground(File... writeable) {
		File file = writeable[0];
		try
	        {
	        	String path = "https://dl.dropboxusercontent.com/s/0ukmtjijx32llbb/PCA%20Data.xls?dl=1&token_hash=AAETCWNPuftXVriFELeOKP1IDdAoTiC4AfhTd8-A2HxL-Q";
	            URL url = new URL(path);

	            URLConnection ucon = url.openConnection();
	            ucon.setReadTimeout(5000);
	            ucon.setConnectTimeout(10000);

	            InputStream is = ucon.getInputStream();
	            BufferedInputStream inStream = new BufferedInputStream(is, 1024 * 5);

	            if (file.exists())
	            {
	                file.delete();
	            }
	            file.createNewFile();

	            FileOutputStream outStream = new FileOutputStream(file);
	            byte[] buff = new byte[5 * 1024];

	            int len;
	            while ((len = inStream.read(buff)) != -1)
	            {
	                outStream.write(buff, 0, len);
	            }

	            outStream.flush();
	            outStream.close();
	            inStream.close();

	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
		return file;
	    }
		
	
	@Override
	protected void onPostExecute(File excelTable)
	{
		Log.d("Error", "called loadInData");
		mCallback.loadInData(excelTable);
	}
	
	public void setActivity(Activity activity)
	{
		// Make sure that the activity implements OnViewSelectedListener
		// If not, throw an exception
		try {
			mCallback = (AsyncExcelLoaderListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnViewSelectedListener");
		}
	}
	
}