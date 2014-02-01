package com.hooapps.pca.cvilleart.Transportation;

import com.hooapps.pca.cvilleart.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
/**
 * Loaded in when the appropriate tab is selected on the TransportationParkingFragment.
 * Tier 3 Fragment
 * @author Alex Ramey
*/
public class TransportationParkingInfoFragment extends Fragment
{
	private TextView text;
	private SpannableString content;
	private static final String MARKET_STREET_GARAGE_TITLE = "Market Street Garage";
	private static final String MARKET_STREET_GARAGE = "504 E Market St., Charlottesville VA\n"
			+ "$2.50 per Hour\n";
	private static final String WATER_STREET_GARAGE_TITLE =  "Water Street Parking Garage";
	private static final String WATER_STREET_GARAGE = "200 E Water St., Charlottesville, VA\n"
			+ "$2.00 per Hour\n";
	private static final String WATER_STREET_LOT_TITLE = "Water Street Lot";
	private static final String WATER_STREET_LOT = "100 E Water St., Charlottesville, VA\n"
			+ "$2.50 per Hour\n";
	private static final String MORE_INFO_TITLE = "More Information";
	private static final String MORE_INFO_INFO = "Monday-Wednesday: 6am - Midnight\n"
			+ "Thursday-Saturday 6am - 1am\n"
			+ "Sunday: Noon - 10pm\n\n"
			+ "Note: Prices & Hours Subject to Change";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// If the activity is being recreated, restore previous version
		// Mostly, this is needed in a two-pane layout
		if (savedInstanceState != null) {
			// TODO Code to restore prior version here  
		}
		
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.parking_info, container, false);
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		// During startup, check if there are arguments passed to the fragment.
        // onStart is a good place to do this because the layout has already been
        // applied to the fragment at this point so we can safely call the method
        // below that sets the article text.
		Bundle args = getArguments();
		if (args != null) {
			// TODO Modify fragment according to settings
		} else {
			// TODO Setup the fragment according to other specifications
		}
		
		text = (TextView) this.getView().findViewById(R.id.market_garage_title);
		content = new SpannableString(MARKET_STREET_GARAGE_TITLE);
		content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
		text.setText(content);
		
		text = (TextView) this.getView().findViewById(R.id.market_garage_info);
		text.setText(MARKET_STREET_GARAGE);
		
		text = (TextView) this.getView().findViewById(R.id.water_garage_title);
		content = new SpannableString(WATER_STREET_GARAGE_TITLE);
		content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
		text.setText(content);
		
		text = (TextView) this.getView().findViewById(R.id.water_garage_info);
		text.setText(WATER_STREET_GARAGE);
		
		text = (TextView) this.getView().findViewById(R.id.water_lot_title);
		content = new SpannableString(WATER_STREET_LOT_TITLE);
		content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
		text.setText(content);
		
		text = (TextView) this.getView().findViewById(R.id.water_lot_info);
		text.setText(WATER_STREET_LOT);
		
		text = (TextView) this.getView().findViewById(R.id.more_info_title);
		content = new SpannableString(MORE_INFO_TITLE);
		content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
		text.setText(content);
		
		text = (TextView) this.getView().findViewById(R.id.more_info_info);
		text.setText(MORE_INFO_INFO);
		
		text = (TextView) this.getView().findViewById(R.id.more_info_link);
		text.setClickable(true);
		text.setMovementMethod(LinkMovementMethod.getInstance());
		String url = "<a href='http://www.charlottesvilleparking.com/index.php/locations'> Click Here For More Information </a>";
		text.setText(Html.fromHtml(url));
	}
}
