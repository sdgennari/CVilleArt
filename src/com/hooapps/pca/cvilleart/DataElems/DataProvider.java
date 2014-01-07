package com.hooapps.pca.cvilleart.DataElems;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * ContentProvider for the PCADatabase.
 * 
 * @author Spencer Gennari
 *
 */
public class DataProvider extends ContentProvider {
	private PCADatabase DB;

	private static final String AUTHORITY = "com.hooapps.pca.cvilleart.DataElems.DataProvider";
	private static final int VENUES = 10;
	private static final int VENUE_ID = 11;
	
	private static final String VENUE_TABLE_BASE_PATH = PCADatabase.TABLE_VENUES;
	public static final Uri VENUE_CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/"+VENUE_TABLE_BASE_PATH);
	
	private static final String VENUE_CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE+"/venue";
	private static final String VENUE_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE+"/venue";
	
	// UriMatcher to classify the URIs accordingly
	public static final UriMatcher sUriMatcher;
	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(AUTHORITY, VENUE_TABLE_BASE_PATH, VENUES);
		sUriMatcher.addURI(AUTHORITY, VENUE_TABLE_BASE_PATH+"/#", VENUE_ID);
	}
	
	@Override
	public boolean onCreate() {
		DB = new PCADatabase(this.getContext());
		return true;
	}
	
	/**
	 * Queries the database for data depending on a uri and a projection.
	 * (param descriptions from developer.android.com)
	 * 
	 * @param uri The URI to query. This will be the full URI sent by the client; 
	 * if the client is requesting a specific record, the URI will end in a record number that the implementation 
	 * should parse and add to a WHERE or HAVING clause, specifying that _id value.
	 * @param projection The list of columns to put into the cursor. If null all columns are included.
	 * @param selection A selection criteria to apply when filtering rows. If null then all rows are included.
	 * @param selectionArgs You may include ?s in selection, which will be replaced by the values from selectionArgs, 
	 * in order that they appear in the selection. The values will be bound as Strings.
	 * @param sortOrder How the rows in the cursor should be sorted. If null then the provider is free to define the sort order.
	 * @return A Cursor based on the query; may be null
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		
		int uriType = sUriMatcher.match(uri);
		switch(uriType) {
		case VENUES:
			builder.setTables(PCADatabase.TABLE_VENUES);
			break;
		case VENUE_ID:
			builder.setTables(PCADatabase.TABLE_VENUES);
			builder.appendWhere(PCADatabase.VENUE_ID+"="+uri.getLastPathSegment());
			break;
		default:
			throw new IllegalArgumentException("Unknown URI");
		}
		
		Cursor cursor = builder.query(DB.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(this.getContext().getContentResolver(), uri);
		return cursor;
	}
	
	/**
	 * Deletes data from the database.
	 * (param descriptions from developer.android.com) 
	 * 
	 * @param uri The full URI to query, including a row ID (if a specific record is requested).
	 * @param selection An optional restriction to apply to rows when deleting.
	 * @return The number of rows deleted
	 */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int uriType = sUriMatcher.match(uri);
		SQLiteDatabase sqlDB = DB.getWritableDatabase();
		int numRowsDeleted = 0;
		
		switch(uriType) {
		case VENUES:
			numRowsDeleted = sqlDB.delete(PCADatabase.TABLE_VENUES, selection, selectionArgs);
			break;
		case VENUE_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				numRowsDeleted = sqlDB.delete(PCADatabase.TABLE_VENUES,
						PCADatabase.VENUE_ID+"="+id, null);
			} else {
				numRowsDeleted = sqlDB.delete(PCADatabase.TABLE_VENUES,
						selection+" AND "+PCADatabase.VENUE_ID+"="+id, selectionArgs);
			}
		default:
			throw new IllegalArgumentException("Unknown or Invalid URI: "+uri);
		}
		
		this.getContext().getContentResolver().notifyChange(uri, null);
		return numRowsDeleted;
	}
	
	/**
	 * Returns the MIME type of the data for a specific Uri.
	 * 
	 * @param The uri to query
	 * @return The MIME type of the Uri
	 */
	@Override
	public String getType(Uri uri) {
		int uriType = sUriMatcher.match(uri);
		
		switch(uriType) {
		case VENUES:
			return VENUE_CONTENT_TYPE;
		case VENUE_ID:
			return VENUE_CONTENT_ITEM_TYPE;
		default:
			return null;
		}
	}
	
	/**
	 * Inserts data into the database.
	 * (param descriptions from developer.android.com)
	 * 
	 * @param uri The content:// URI of the insertion request. This must not be null.
	 * @param values A set of column_name/value pairs to add to the database. This must not be null.
	 * @return The Uri of the item that was inserted
	 */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int uriType = sUriMatcher.match(uri);
		
		// TODO Add || for other data types
		if(uriType != VENUES) {
			throw new IllegalArgumentException("Invalid URI for INSERT");
		}
		
		// TODO Check which table needs to be accessed for insert		
		SQLiteDatabase sqlDB = DB.getWritableDatabase();
		try {
			long newID = sqlDB.insertOrThrow(PCADatabase.TABLE_VENUES, null, values);
			if (newID > 0) {
				Uri newUri = ContentUris.withAppendedId(uri, newID);
				this.getContext().getContentResolver().notifyChange(newUri, null);
				return newUri;
			} else {
				throw new SQLException("Failed to insert row into "+uri);
			}
		} catch (SQLiteConstraintException e) {
			Log.d("TAG", "Ignoring constraint failure.");
		}
		return null;
	}
	
	/**
	 * Updates data in the database.
	 * (param descriptions from developer.android.com)
	 * 
	 * @param uri The URI to query. This can potentially have a record ID if this is an update request for a specific record.
	 * @param values A set of column_name/value pairs to update in the database. This must not be null.
	 * @param selection An optional filter to match rows to update.
	 * @return The number of rows affected.
	 */
	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		int uriType = sUriMatcher.match(uri);
		SQLiteDatabase sqlDB = DB.getWritableDatabase();
		int numRowsUpdated = 0;
		
		switch(uriType) {
		case VENUES:
			numRowsUpdated = sqlDB.update(PCADatabase.TABLE_VENUES, values, selection, selectionArgs);
			break;
		case VENUE_ID:
			String id = uri.getLastPathSegment();
			StringBuilder modSelection = new StringBuilder(PCADatabase.VENUE_ID+"="+id);
			
			if (!TextUtils.isEmpty(selection)) {
				modSelection.append(" AND "+selection);
			}
			
			// TODO Possibly change selectionArgs to null
			numRowsUpdated = sqlDB.update(PCADatabase.TABLE_VENUES, values, modSelection.toString(), selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI");
		}
		
		this.getContext().getContentResolver().notifyChange(uri, null);
		return numRowsUpdated;
	}
	
	
	
	

}
