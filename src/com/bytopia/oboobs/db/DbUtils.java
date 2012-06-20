package com.bytopia.oboobs.db;

import static com.bytopia.oboobs.db.BoobsDbOpenHelper.AUTHOR;
import static com.bytopia.oboobs.db.BoobsDbOpenHelper.FAVORITES_TABLE_NAME;
import static com.bytopia.oboobs.db.BoobsDbOpenHelper.FILE_NAME;
import static com.bytopia.oboobs.db.BoobsDbOpenHelper.ID;
import static com.bytopia.oboobs.db.BoobsDbOpenHelper.MODEL;
import static com.bytopia.oboobs.db.BoobsDbOpenHelper.PREVIEW;
import static com.bytopia.oboobs.db.BoobsDbOpenHelper.RANK;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.bytopia.oboobs.model.Boobs;

public class DbUtils {
	public static BoobsDbOpenHelper helper;

	public static boolean addFavorite(Boobs boobs, String savedFileName) {

		SQLiteDatabase database = helper.getWritableDatabase();
		ContentValues values = createValues(boobs, savedFileName);

		long res = database.insertWithOnConflict(FAVORITES_TABLE_NAME, null,
				values, SQLiteDatabase.CONFLICT_REPLACE);

		database.close();

		return res != -1;
	}

	public static boolean removeFromFavorites(int id) {
		
		SQLiteDatabase database = helper.getWritableDatabase();
		int res = database.delete(FAVORITES_TABLE_NAME, ID+"=?", new String[]{""+id});
		return res!=0;
	}

	private static ContentValues createValues(Boobs boobs, String savedFileName) {
		ContentValues values = new ContentValues();

		values.put(ID, boobs.id);
		values.put(MODEL, boobs.model);
		values.put(AUTHOR, boobs.author);
		values.put(PREVIEW, boobs.preview);
		values.put(RANK, boobs.rank);
		values.put(FILE_NAME, savedFileName);

		return values;
	}

}
