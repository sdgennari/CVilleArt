package com.hooapps.pca.cvilleart;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AboutFragment extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup root, Bundle savedInstanceState) {
		Log.d("ABOUT", "createView");
		return inflater.inflate(R.layout.about_view, root, false);
	}
}
