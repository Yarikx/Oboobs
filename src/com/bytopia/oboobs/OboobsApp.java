package com.bytopia.oboobs;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.bytopia.oboobs.utils.CacheHolder;
import com.bytopia.oboobs.utils.Utils;

public class OboobsApp extends Application {

	public boolean isDark = true;

	public static String MAX_DISK_CACHE_PREF;

	public SharedPreferences preferences;

	private CacheHolder cacheHolder;

	@Override
	public void onCreate() {
		super.onCreate();
		Utils.initApp(this);

		preferences = PreferenceManager.getDefaultSharedPreferences(this);

		MAX_DISK_CACHE_PREF = getString(R.string.max_disk_cache_pref);

		cacheHolder = new CacheHolder(this);

	}

	public CacheHolder getCacheHolder() {
		return cacheHolder;
	}

}
