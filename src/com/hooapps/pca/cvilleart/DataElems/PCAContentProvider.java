package com.hooapps.pca.cvilleart.DataElems;

import java.util.Arrays;
import java.util.HashSet;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class PCAContentProvider extends ContentProvider {

	// Reference to the database
	private PCADatabaseHelper database;

	// Define the URIs
	private static final String BASE_PATH_VENUES = "venues";
	private static final String BASE_PATH_EVENTS = "events";
	
	private static final String AUTHORITY = "com.hooapps.pca.cvilleart.DataElems"; 

	public static final Uri VENUE_CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/"+BASE_PATH_VENUES);
	public static final Uri EVENT_CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/"+BASE_PATH_EVENTS);

	// Used for URI Matching
	private static final int VENUE_ITEMS = 10;
	private static final int VENUE_ITEM_ID = 11;
	private static final int EVENT_ITEMS = 20;
	private static final int EVENT_ITEM_ID = 21;

	public static final String VENUE_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+BASE_PATH_VENUES;
	public static final String VENUE_CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+BASE_PATH_VENUES;
	public static final String EVENT_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+BASE_PATH_EVENTS;
	public static final String EVENT_CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+BASE_PATH_EVENTS;

	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, BASE_PATH_VENUES, VENUE_ITEMS);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH_VENUES+"/#", VENUE_ITEM_ID);

		sURIMatcher.addURI(AUTHORITY, BASE_PATH_EVENTS, EVENT_ITEMS);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH_EVENTS+"/#", EVENT_ITEM_ID);
	}

	@Override
	public boolean onCreate() {
		database = new PCADatabaseHelper(getContext());
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

		// SQLiteQueryBuilder instead query()
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		// Make sure the columns are valid
		checkColumns(projection);

		// Set the table
		// TODO Update this based on the table being used
		// Possible use uriType to detect table called
		queryBuilder.setTables(VenueTable.TABLE_VENUES);
		
		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case VENUE_ITEMS:
			break;
		case VENUE_ITEM_ID:
			queryBuilder.appendWhere(VenueTable.COLUMN_ID+"="+uri.getLastPathSegment());
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: "+uri);
		}
		
		SQLiteDatabase db = database.getWritableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		
		return cursor;
	}
	
	@Override
	public String getType(Uri uri) {
		return null;
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		long id = 0;
		
		switch (uriType) {
		case VENUE_ITEMS:
			id = sqlDB.insert(VenueTable.TABLE_VENUES, null, values);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: "+uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return Uri.parse(BASE_PATH_VENUES+"/"+id);
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		int rowsDeleted = 0;
		
		switch (uriType) {
		case VENUE_ITEMS:
			rowsDeleted = sqlDB.delete(VenueTable.TABLE_VENUES, selection, selectionArgs);
			break;
		case VENUE_ITEM_ID:
			String id = uri.getLastPathSegment();
			if(TextUtils.isEmpty(selection)) {
				rowsDeleted = sqlDB.delete(VenueTable.TABLE_VENUES, VenueTable.COLUMN_ID+"="+id, null);
			} else {
				rowsDeleted = sqlDB.delete(VenueTable.TABLE_VENUES, VenueTable.COLUMN_ID+"="+id+" AND "+selection, selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: "+uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsDeleted;
	}
	
	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		int rowsUpdated = 0;
		
		switch (uriType) {
		case VENUE_ITEMS:
			rowsUpdated = sqlDB.update(VenueTable.TABLE_VENUES, values, selection, selectionArgs);
			break;
		case VENUE_ITEM_ID:
			String id = uri.getLastPathSegment();
			if(TextUtils.isEmpty(selection)) {
				rowsUpdated = sqlDB.update(VenueTable.TABLE_VENUES, values, VenueTable.COLUMN_ID+"="+id, null);
			} else {
				rowsUpdated = sqlDB.update(VenueTable.TABLE_VENUES, values, VenueTable.COLUMN_ID+"="+id+" AND "+selection, selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: "+uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsUpdated;
	}
	
	private void checkColumns(String[] projection) {
		String[] available = {
				VenueTable.COLUMN_ID, VenueTable.ORGANIZATION_NAME, VenueTable.EMAIL_ADDRESS,
				VenueTable.HOME_PAGE_URL, VenueTable.PHONE_NUMBER_PRIMARY, VenueTable.ADDRESS_HOME_STREET,
				VenueTable.ADDRESS_HOME_CITY, VenueTable.ADDRESS_HOME_POSTAL_CODE, VenueTable.ADDRESS_HOME_STATE,
				VenueTable.CATEGORY_ART_COMMUNITY_CATEGORIES, VenueTable.SECONDARY_CATEGORY, VenueTable.LAT_LNG_STRING,
				VenueTable.LON, VenueTable.LAT, VenueTable.IMAGE_URLS, VenueTable.DIRECTORY_DESCRIPTION_LONG,
				VenueTable.IMAGE_THUMB_PATH, VenueTable.IMAGE_MAIN_PATH };
		
		if(projection != null) {
			HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
			HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
			
			if(!availableColumns.containsAll(requestedColumns)) {
				throw new IllegalArgumentException("Unknown columns in projection");
			}
		}
	}
}