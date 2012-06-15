package com.bytopia.oboobs;

import java.io.File;
import java.io.IOException;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.bytopia.oboobs.utils.Utils;
import com.jakewharton.DiskLruCache;

public class OboobsApp extends Application {

	public boolean isDark = true;

	public static String MAX_DISK_CACHE_PREF;

	SharedPreferences preferences;

	private File cacheDir;
	boolean externalStorageAvailable = false;
	private static final int DEFAULT_CACHE_SIZE = 10000000;
	private static int maxCacheSize = DEFAULT_CACHE_SIZE;

	private DiskLruCache diskCache;

	@Override
	public void onCreate() {
		super.onCreate();
		Utils.initApp(this);
		
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		
		MAX_DISK_CACHE_PREF = getString(R.string.max_disk_cache_pref);
		maxCacheSize = preferences.getInt(MAX_DISK_CACHE_PREF, DEFAULT_CACHE_SIZE);
		
		
		
		
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
		    // We can read and write the media
		    externalStorageAvailable = true;
		    cacheDir = getExternalCacheDir();
		    
		    try {
				diskCache = DiskLruCache.open(cacheDir, 1, 1, maxCacheSize);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		
	}
}
