package com.bytopia.oboobs.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class BoobsDbOpenHelper extends SQLiteOpenHelper{
	
	private static final int version = 1;
	private static final String DB_NAME = "boobs_db";
	private static final String FAVORITES_TABLE_NAME = "favorites";

	public BoobsDbOpenHelper(Context context) {
		super(context, DB_NAME, null, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
//		db.execSQL("CREATE TABLE")
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
