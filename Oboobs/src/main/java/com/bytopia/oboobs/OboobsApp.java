package com.bytopia.oboobs;

import android.app.Application;

import com.bytopia.oboobs.mindstorm.OboobsModule;

import dagger.ObjectGraph;

public class OboobsApp extends Application {

	public boolean isDark = true;

	public static String MAX_DISK_CACHE_PREF;
	
	public static String PACKAGE_NAME;

    public static OboobsApp instance;

    private ObjectGraph objectGraph;

	@Override
	public void onCreate() {
        instance = this;
        objectGraph = ObjectGraph.create(new OboobsModule(this));
		super.onCreate();

		MAX_DISK_CACHE_PREF = getString(R.string.max_disk_cache_pref);
		PACKAGE_NAME = getPackageName();
	}

	public void inject(Object o){
        objectGraph.inject(o);
    }

}
