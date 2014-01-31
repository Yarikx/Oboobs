package com.bytopia.oboobs.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BoobsDbOpenHelper extends SQLiteOpenHelper{
	
	private static final int version = 1;
	private static final String DB_NAME = "boobs_db";
	public static final String FAVORITES_TABLE_NAME = "favorites";
	public static final String ID = "_id";
	public static final String MODEL = "model";
	public static final String AUTHOR = "author";
	public static final String FILE_NAME = "fileName";
	public static final String RANK = "rank";
	public static final String PREVIEW = "preview";
	
	
	public BoobsDbOpenHelper(Context context) {
		super(context, DB_NAME, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE "
				+FAVORITES_TABLE_NAME+" ("+
				ID+" INTEGER, "+
				MODEL+" TEXT, "+
				AUTHOR+" TEXT, "+
				RANK+" INTEGER, "+
				PREVIEW+" TEXT, "+
				FILE_NAME+" TEXT); "
				);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
