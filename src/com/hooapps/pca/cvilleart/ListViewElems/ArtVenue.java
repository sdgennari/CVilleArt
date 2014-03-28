package com.hooapps.pca.cvilleart.ListViewElems;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hooapps.pca.cvilleart.R;
import com.hooapps.pca.cvilleart.CustomElems.RoundedImageView;
import com.hooapps.pca.cvilleart.ListViewElems.ItemArrayAdapter.Type;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/** @author Alex Ramey*/
/**Should implement Item*/
/**We no longer need Cursor to read from local database; Instead we save excel document to the phone.
 *We add the ArtVenues to a custom adapter like the DiscoverCursorAdapter that sets up the layout
 * for each item correctly. Then, when the items are clicked, the listFragment's callback to the main activity
 * will occur and, using the item's fields, we can launch a Discover Item Fragment*/


public class ArtVenue implements Item
{
	private final String organizationName;
	private final String emailAddress;
	private final String URL;
	private final String description;
	private final String phoneNumber;
	private final String streetAddress;
	private final String city;
	private final String zipCode;
	private final String stateAbv;
	private final String primaryCategory;
	private final String secondaryCategory;
	private final String LatLngString;
	private final String imagePath;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	
	public ArtVenue(String organizationName, String emailAddress, String URL, String description, 
			String phoneNumber, String streetAddress, String city, String zipCode, String stateAbv,
			String primaryCategory, String secondaryCategory, String LatLngString)
	{
		this.organizationName = organizationName;
		this.emailAddress = emailAddress;
		this.URL = URL;
		this.description = description;
		this.phoneNumber = phoneNumber;
		this.streetAddress = streetAddress;
		this.city = city;
		this.zipCode = zipCode;
		this.stateAbv = stateAbv;
		this.primaryCategory = primaryCategory;
		this.secondaryCategory = secondaryCategory;
		this.LatLngString = LatLngString;
		this.imagePath = "http://directories.charlottesvillearts.org/container.php?file=sign.jpg&lay=PHP%20-%20Primary%20Image&recid=6926&field=Directory_Primary_Image";
	}
	
	/**
	 * @return the organizationName
	 */
	public String getOrganizationName() {
		return organizationName;
	}


	/**
	 * @return the emailAddress
	 */
	public String getEmailAddress() {
		return emailAddress;
	}


	/**
	 * @return the uRL
	 */
	public String getURL() {
		return URL;
	}


	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}


	/**
	 * @return the phoneNumber
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}


	/**
	 * @return the streetAddress
	 */
	public String getStreetAddress() {
		return streetAddress;
	}


	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}


	/**
	 * @return the zipCode
	 */
	public String getZipCode() {
		return zipCode;
	}


	/**
	 * @return the stateAbv
	 */
	public String getStateAbv() {
		return stateAbv;
	}


	/**
	 * @return the primaryCategory
	 */
	public String getPrimaryCategory() {
		return primaryCategory;
	}


	/**
	 * @return the secondaryCategory
	 */
	public String getSecondaryCategory() {
		return secondaryCategory;
	}


	/**
	 * @return the latLngString
	 */
	public String getLatLngString() {
		return LatLngString;
	}
	
	public MarkerOptions getMarkerOptions()
	{
		int index = LatLngString.indexOf("");
		String Latitude = LatLngString.substring(0, index).trim();
		String Longitude = LatLngString.substring(index+1).trim();
		float Lat = Float.parseFloat(Latitude);
		float Long = Float.parseFloat(Longitude);
		
		return new MarkerOptions().title(this.organizationName).position(new LatLng(Lat, Long))
				.icon(BitmapDescriptorFactory.fromResource(this.getMarkerId())).anchor((float).50,(float).50);
	}
	
	private int getMarkerId()
	{
		if (this.primaryCategory.equals("Film"))
		{
			return R.drawable.other_marker;
		}
		else if (this.primaryCategory.equals("Gallery"))
		{
			return R.drawable.gallery_marker;
		}
		else if (this.primaryCategory.equals("Music"))
		{
			return R.drawable.music_marker;
		}
		else if (this.primaryCategory.equals("Theatre"))
		{
			return R.drawable.theatre_marker;
		}
		else if (this.primaryCategory.equals("Venue"))
		{
			return R.drawable.other_marker;
		}
		else
		{
			return R.drawable.dance_marker;
		}
	}
	
	public String getImagePath()
	{
		return imagePath;
	}
	
	@Override
	public String toString()
	{
		return "Organization: " + organizationName + "\n\tLatLng: " + LatLngString + "\n";
	}

	@Override
	public int getViewType() {
		return Type.VENUE_ITEM.ordinal();
	}

	@Override
	public View getView(LayoutInflater inflater, View convertView) {
		View view;
		if (convertView == null) {
			view = (View) inflater.inflate(R.layout.discover_list_item, null);
		} else {
			view = convertView;
		}
		
		final ImageView imageView = (RoundedImageView)view.findViewById(R.id.image);
		// TODO Connect this to DiscoverCursorAdapter
		imageLoader.loadImage(imagePath, new SimpleImageLoadingListener() {
		    @Override
		    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				imageView.setImageBitmap(loadedImage);
		    }
		});
		
		TextView title = (TextView) view.findViewById(R.id.venue_title);
		title.setText(this.organizationName);
		/*
		TextView type = (TextView) view.findViewById(R.id.type);
		type.setText(this.primaryCategory);
		*/
		
		return view;
	}
}
