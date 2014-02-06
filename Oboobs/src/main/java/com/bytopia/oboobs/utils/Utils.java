package com.bytopia.oboobs.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.content.ContextCompat;

import com.BaseActivity;
import com.bytopia.oboobs.R;
import com.bytopia.oboobs.db.DbUtils;
import com.bytopia.oboobs.model.Boobs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.inject.Inject;

public class Utils {

    private final Context context;

    @Inject DbUtils dbUtils;

	public File baseDir;
	public File cacheDir;
	public File filesDir;
	public File favoritesDir;
	
	public boolean externalStorageAvailable = false;

	private static final String FAVORITES = "favorites";
    @Inject
    protected SharedPreferences preferences;

//	static Type boobsCollectionType = new TypeToken<List<Boobs>>() {
//	}.getType();

	public static class Constants {
		public static final int DEFAULT_CHUNK = 20;

		private static final String PREF_CHUNK_KEY = "chunk_number";
	}

    @Inject
	public Utils(Context context) {
        this.context = context;
		BaseActivity.flurryKey = context.getResources().getString(R.string.flurry_key);
		setDirs();
	}

	private void setDirs() {
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			externalStorageAvailable = true;
			// We can read and write the media
				cacheDir = ContextCompat.getExternalCacheDirs(context)[0];
				filesDir = ContextCompat.getExternalFilesDirs(context, null)[0];
				baseDir = cacheDir.getParentFile();
		}else{
			cacheDir = context.getCacheDir();
			filesDir = context.getFilesDir();
			baseDir = cacheDir.getParentFile();
		}
		favoritesDir = new File(filesDir, FAVORITES);
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		float ratio = (float) width / (float) height;
		if ((float) reqWidth / (float) reqHeight > ratio) {
			reqWidth = (int) (ratio * reqHeight);
		} else {
			reqHeight = (int) (reqWidth / ratio);
		}

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}
		return inSampleSize;
	}

	public int getBoobsChunk() {
		return preferences.getInt(Constants.PREF_CHUNK_KEY,
				Constants.DEFAULT_CHUNK);
	}

	public File getFileInFavorites(String fileName) {
		return new File(favoritesDir,fileName);
	}

	public boolean hasFileInFavorite(String fileName) {
		return getFileInFavorites(fileName).exists();
	}

	public boolean saveFavorite(Boobs boobs, Bitmap imageBitmap) {
		OutputStream os = null;
		try {
			File f = boobs.getSavedFile(this);
			if (!f.getParentFile().exists()) {
				f.getParentFile().mkdirs();
			}
			os = new FileOutputStream(boobs.getSavedFile(this));
			imageBitmap.compress(CompressFormat.JPEG, 80, os);

			boolean ok = dbUtils.addFavorite(boobs, f.getPath());

			return ok;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	public boolean removeFavorite(Boobs boobs) {
		File f = boobs.getSavedFile(this);
		if(f.exists()){
			f.delete();
		}

		boolean ok = dbUtils.removeFromFavorites(boobs.id);

		return ok;
	}

}
