package com.hooapps.pca.cvilleart.DataElems;

import android.database.sqlite.SQLiteDatabase;

public class VenueTable {

	// Table and column names
	public static final String TABLE_VENUES = "venues";
	public static final String COLUMN_ID = "_id";
	public static final String ORGANIZATION_NAME = "organization_name";
	public static final String EMAIL_ADDRESS = "email_Address";
	public static final String HOME_PAGE_URL = "home_page_url";
	public static final String PHONE_NUMBER_PRIMARY = "phone_number_proimary";
	public static final String ADDRESS_HOME_STREET = "address_home_street";
	public static final String ADDRESS_HOME_CITY = "address_home_city";
	public static final String ADDRESS_HOME_POSTAL_CODE = "address_home_postal_code";
	public static final String ADDRESS_HOME_STATE = "address_home_state";
	public static final String CATEGORY_ART_COMMUNITY_CATEGORIES = "category_art_community_categories";
	public static final String SECONDARY_CATEGORY = "secondary_category";	
	public static final String LAT_LNG_STRING = "lat_lng_string";
	public static final String LAT = "lat";			// Extra col for numeric lat
	public static final String LON = "lon";			// Extra col for numeric lon
	public static final String IMAGE_URLS = "image_urls";
	public static final String DIRECTORY_DESCRIPTION_LONG = "directory_description_long";

	// Possible extra fields for image paths
	public static final String IMAGE_THUMB_PATH = "image_thumb_path";
	public static final String IMAGE_MAIN_PATH = "image_main_path";

	// Database create statement
	private static final String DATABASE_CREATE = "CREATE TABLE "
		+ TABLE_VENUES
		+ " ( "
		+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
		+ ORGANIZATION_NAME + " VARCHAR(255) NOT NULL, "
		+ EMAIL_ADDRESS + " VARCHAR(60), "
		+ HOME_PAGE_URL + " VARCHAR(255), "
		+ DIRECTORY_DESCRIPTION_LONG + " TEXT, "
		+ PHONE_NUMBER_PRIMARY + " VARCHAR(15), "
		+ ADDRESS_HOME_STREET + " VARCHAR(255), "
		+ ADDRESS_HOME_CITY + " VARCHAR(255), "
		+ ADDRESS_HOME_POSTAL_CODE + " INTEGER, "
		+ ADDRESS_HOME_STATE + " VARCHAR(2), "				// Only two letters b/c state code
		+ CATEGORY_ART_COMMUNITY_CATEGORIES + " VARCHAR(20), "
		+ SECONDARY_CATEGORY + " VARCHAR(20), "
		+ LAT_LNG_STRING + " VARCHAR(255), "
		+ LAT + " INTEGER, "
		+ LON + " INTEGER, "
		+ IMAGE_URLS + " VARCHAR(255), "
		+ IMAGE_THUMB_PATH + " VARCHAR(255), "
		+ IMAGE_MAIN_PATH + " VARCHAR(255) "
		+ ");";

	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop the current table and replace it with a new one
		// All data will be lost using this method
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_VENUES);
		onCreate(db);
	}
}