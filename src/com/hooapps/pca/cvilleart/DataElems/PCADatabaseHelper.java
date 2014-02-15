package com.hooapps.pca.cvilleart.DataElems;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PCADatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "pca.db";
	private static final int DATABASE_VERSION = 1;

	public PCADatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		VenueTable.onCreate(db);
		//EventTable.onCreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		VenueTable.onUpgrade(db, oldVersion, newVersion);
		//EventTable.onUpgrade(db, oldVersion, newVersion);
	}
}