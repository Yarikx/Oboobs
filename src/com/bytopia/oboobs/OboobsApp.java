package com.bytopia.oboobs;

import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.preference.PreferenceManager;

import com.bytopia.oboobs.utils.CacheHolder;
import com.bytopia.oboobs.utils.Utils;

public class OboobsApp extends Application {

	public boolean isDark = true;

	public static String MAX_DISK_CACHE_PREF;
	
	public static String PACKAGE_NAME;

	public SharedPreferences preferences;

	private CacheHolder cacheHolder;

	private Handler resultHandler;

	private ImageReceiver curentReceiver;

	@Override
	public void onCreate() {
		super.onCreate();
		Utils.initApp(this);

		preferences = PreferenceManager.getDefaultSharedPreferences(this);

		MAX_DISK_CACHE_PREF = getString(R.string.max_disk_cache_pref);
		PACKAGE_NAME = getPackageName();

		cacheHolder = new CacheHolder(this);

		resultHandler = new Handler(new Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				int senderType = msg.arg2;
				if (curentReceiver != null && curentReceiver.getSenderType() == senderType) {
					Bitmap bm = (Bitmap) msg.obj;
					int id = msg.arg1;
					
					curentReceiver.receiveImage(id, bm);
				}

				return true;
			}
		});
		
	}

	public CacheHolder getCacheHolder() {
		return cacheHolder;
	}

	public Handler getResultHandler() {
		return resultHandler;
	}

	public ImageReceiver getCurentReceiver() {
		return curentReceiver;
	}

	public void setCurentReceiver(ImageReceiver curentReceiver) {
		this.curentReceiver = curentReceiver;
	}

}
