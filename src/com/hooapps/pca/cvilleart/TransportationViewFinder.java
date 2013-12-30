package com.hooapps.pca.cvilleart;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;

/** 
 * This object returns the appropriate View object to the TabHost of the Transportation
 * Fragment based on which tab is selected in the TabWidget. This View is then displayed
 * in the TabHost's tab content FrameLayout below the tab widget, both of which are 
 * contained in the layout of the Transportation Fragment.
 * 
 * @author Alex Ramey
 * 
 * TODO: Setup Views 
 */
public class TransportationViewFinder implements TabHost.TabContentFactory{

	private View view;
	private LayoutInflater mInflater;
	
	public TransportationViewFinder(LayoutInflater inflater)
	{
		this.mInflater = inflater;
	}
	
	@Override
	public View createTabContent(String tag) {
		if (tag.equals("public"))
			return publicView();
		else if (tag.equals("parking"))
			return parkingView();
		else if (tag.equals("taxi"))
			return taxiView();
		else
			return bikeView();
	}
	
	private View publicView()
	{
		view = (View) mInflater.inflate(R.layout.transportation_public, null);
		return view;
	}
	
	private View parkingView()
	{
		view = (View) mInflater.inflate(R.layout.transportation_parking, null);
		return view;
	}
	
	private View taxiView()
	{
		view = (View) mInflater.inflate(R.layout.transportation_taxi, null);
		return view;
	}
	
	private View bikeView()
	{
		view = (View) mInflater.inflate(R.layout.transportation_bike, null);
		return view;
	}
	
}
