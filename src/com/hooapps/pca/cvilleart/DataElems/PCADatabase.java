package com.hooapps.pca.cvilleart.DataElems;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database to store the information pulled from the PCA website.
 * 
 * @author Spencer Gennari
 *
 */

public class PCADatabase extends SQLiteOpenHelper {
	
	// Database information
	private static final int DB_VERSION = 1;
	private static final String DB_NAME = "pca_website_data";
	
	// Venue Table Information and Columns
	public static final String TABLE_VENUES = "venues";
	public static final String VENUE_ID = "_id";
	public static final String VENUE_NAME = "name";
	public static final String VENUE_DESCRIPTION = "description";
	public static final String VENUE_TYPE = "type";
	public static final String VENUE_IMG_URL = "image_url";
	public static final String VENUE_ADDRESS = "address";
	
	// Create statement for the Venue Table
	private static final String CREATE_TABLE_VENUES = "CREATE TABLE "
			+ TABLE_VENUES + " (" + VENUE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ VENUE_NAME + " TEXT NOT NULL, "
			+ VENUE_DESCRIPTION + " TEXT NOT NULL, "
			+ VENUE_TYPE + " TEXT NOT NULL, "
			+ VENUE_IMG_URL + " TEXT NOT NULL, "
			+ VENUE_ADDRESS + " TEXT NOT NULL);";
	
	public PCADatabase(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}
	
	/**
	 * Creates the database by executing the various create table statements.
	 * 
	 * @param db The database that executes the create statements
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_VENUES);
		seedData(db);
	}
	
	/**
	 * Upgrades the database when a difference in versions is detected.
	 * 
	 * @param db The database that executes the upgrade statements
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_VENUES);
		onCreate(db);
	}
	
	/**
	 * Seeds sample data into the database for testing purposes.
	 * 
	 * @param db The database into which the values are inserted
	 */
	private void seedData(SQLiteDatabase db) {
		db.execSQL("INSERT INTO "+TABLE_VENUES+" ("+VENUE_NAME+", "+VENUE_DESCRIPTION+", "+VENUE_TYPE+", "+VENUE_IMG_URL+", "+VENUE_ADDRESS+") values ("
				+"'PVCC Fine Arts Department'"+", "
				+"'The purpose of the Humanities, Fine Arts & Social Sciences Division is to offer world-class coursework applicable to the needs of the students whether their goal is transfer to a four-year college, moving into a college curriculum, improving job skills or fostering artistic abilities for personal development.'"+", "
				+"'Dance'"+", "
				+"'http://directories.charlottesvillearts.org/container.php?file=title.jpg&lay=PHP%20-%20Primary%20Image&recid=4872&field=Directory_Primary_Image'"+", "
				+"'V. Earl Dickinson Building, 501 College Drive, Charlottesville, VA 22902'"+");");
		
		db.execSQL("INSERT INTO "+TABLE_VENUES+" ("+VENUE_NAME+", "+VENUE_DESCRIPTION+", "+VENUE_TYPE+", "+VENUE_IMG_URL+", "+VENUE_ADDRESS+") values ("
				+"'The Paramount Theater'"+", "
				+"'For most of the middle of the 20th century, Charlottesville’s Paramount Theater had taken a leading role in the community. Created by Chicago architectural firm Rapp & Rapp, architects of The Paramount-Publix chain and its flagship theater in New York City’s Times Square, Charlottesville’s Paramount was part of the golden age of cinema.'"+", "
				+"'Film'"+", "
				+"'http://directories.charlottesvillearts.org/container.php?file=streetView.jpg&lay=PHP%20-%20Primary%20Image&recid=4907&field=Directory_Primary_Image'"+", "
				+"'215 East Main St., Charlottesville, VA 22902'"+");");
		
		db.execSQL("INSERT INTO "+TABLE_VENUES+" ("+VENUE_NAME+", "+VENUE_DESCRIPTION+", "+VENUE_TYPE+", "+VENUE_IMG_URL+", "+VENUE_ADDRESS+") values ("
				+"'FIREFISH Gallery'"+", "
				+"'FIREFISH Gallery, located on the Downtown Mall in Charlottesville, hosts a variety of classes for adults, teens, and children. Mosaic lessons teach participants how to create decorative objects of art using rich colors, reflective surfaces and timeless permanence.'"+", "
				+"'Galleries'"+", "
				+"'http://directories.charlottesvillearts.org/container.php?file=sign.jpg&lay=PHP%20-%20Primary%20Image&recid=6926&field=Directory_Primary_Image'"+", "
				+"'108 2nd St NW, Charlottesville, VA 22902'"+");");
		
		db.execSQL("INSERT INTO "+TABLE_VENUES+" ("+VENUE_NAME+", "+VENUE_DESCRIPTION+", "+VENUE_TYPE+", "+VENUE_IMG_URL+", "+VENUE_ADDRESS+") values ("
				+"'Para Coffee'"+", "
				+"'Para Cofee is located on the Corner near the University of Virginia. It is a coffee shop that hosts occasional concerts and art shows.'"+", "
				+"'Music'"+", "
				+"'http://directories.charlottesvillearts.org/container.php?file=filename-351-507749145306.jpg&lay=PHP%20-%20Primary%20Image&recid=7384&field=Directory_Primary_Image'"+", "
				+"'19 Elliewood Avenue, Charlottesville, VA 22903'"+");");
		/*
		db.execSQL("INSERT INTO "+TABLE_VENUES+" ("+VENUE_NAME+", "+VENUE_DESCRIPTION+", "+VENUE_TYPE+", "+VENUE_IMG_URL+", "+VENUE_ADDRESS+") values ("
				+"'Four County Players'"+", "
				+"'Founded in 1973, Four County Players is Central Virginia's longest continuously operating community theater. For more than 35 years, the players have delighted audiences with a full range of theater experiences.'"+", "
				+"'Theatre'"+", "
				+"'http://directories.charlottesvillearts.org/container.php?file=Screen+shot+2013-06-24+at+9.48.02+AM.png&lay=PHP%20-%20Primary%20Image&recid=4819&field=Directory_Primary_Image'"+", "
				+"'5256 Governor Barbour St., Barboursville, VA 22923'"+");");
		*/
	}
}
