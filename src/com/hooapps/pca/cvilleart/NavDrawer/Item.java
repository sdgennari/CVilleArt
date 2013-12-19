/*
 * Adapted from the online code samples found at:
 * http://stackoverflow.com/questions/13590627/android-listview-headers
 * 
 * Heavily modified for use within this specific app,
 * but always give credit where credit is due.
 */

package com.hooapps.pca.cvilleart.NavDrawer;

import android.view.LayoutInflater;
import android.view.View;

/**
* Interface to be used with items in the custom ListView in the NavDrawer. Each
* view in the ListView must implement Item to work with the adapter.
* 
* @author Spencer Gennari
*/

public interface Item {
	public int getViewType();
	public View getView(LayoutInflater inflater, View convertView);
}
