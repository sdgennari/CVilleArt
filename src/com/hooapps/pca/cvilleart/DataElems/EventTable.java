package com.hooapps.pca.cvilleart.DataElems;

import android.database.sqlite.SQLiteDatabase;

public class EventTable {
	
	// Table and column names
	public static final String TABLE_EVENTS = "events";
	public static final String COLUMN_ID = "_id";
	public static final String EVENT_ID = "event_id";
	public static final String CATEGORY = "category";
	public static final String UPDATED = "updated";
	public static final String SUMMARY = "summary";
	public static final String DESCRIPTION = "description";
	public static final String LOCATION = "location";
	public static final String START_TIME = "start_time";
	public static final String END_TIME = "end_time";
	
	private static final String DATABASE_CREATE = "CREATE TABLE "
			+ TABLE_EVENTS
			+ " ( "
			+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ EVENT_ID + " VARCHAR(60) NOT NULL, "
			+ CATEGORY + " VARCHAR(20), "
			+ UPDATED + " INTEGER, "
			+ SUMMARY + " VARCHAR(255) NOT NULL, "
			+ DESCRIPTION + " TEXT, "
			+ LOCATION + " VARCHAR(255), "
			+ START_TIME + " INTEGER, "
			+ END_TIME + " INTEGER "
			+ ");";
	
	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}
	
	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop the current table and replace it with a new one
		// All data will be lost using this method
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
		onCreate(db);
	}
}
