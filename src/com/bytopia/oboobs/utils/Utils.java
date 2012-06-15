package com.bytopia.oboobs.utils;

import static com.bytopia.oboobs.utils.RequestBuilder.apiUrl;
import static com.bytopia.oboobs.utils.RequestBuilder.authorPart;
import static com.bytopia.oboobs.utils.RequestBuilder.boobsPart;
import static com.bytopia.oboobs.utils.RequestBuilder.modelPart;
import static com.bytopia.oboobs.utils.RequestBuilder.noisePart;

import java.lang.reflect.Type;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;

import com.bytopia.oboobs.ImageReceiver;
import com.bytopia.oboobs.OboobsApp;
import com.bytopia.oboobs.R;
import com.bytopia.oboobs.model.Boobs;
import com.google.gson.reflect.TypeToken;

public class Utils {

	static OboobsApp app;
	
	static CacheHolder cacheHolder;

	static Type boobsCollectionType = new TypeToken<List<Boobs>>(){}.getType();

	public static void initApp(OboobsApp oboobsApp) {
		app = oboobsApp;
		cacheHolder = app.getCacheHolder();
		disableConnectionReuseIfNecessary();
		spreadStaticValues();
	}

	private static void spreadStaticValues() {
		apiUrl = app.getString(R.string.api_url);
		boobsPart = app.getString(R.string.boobs_part);
		noisePart = app.getString(R.string.noise_part);
		modelPart = app.getString(R.string.model_search_part);
		authorPart = app.getString(R.string.author_search_part);
	}

	private static void disableConnectionReuseIfNecessary() {
		// HTTP connection reuse which was buggy pre-froyo
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
			System.setProperty("http.keepAlive", "false");
		}
	}
	
//	public void requestImage(int imageId, String url, Context context, ImageReceiver imageReceiver){
//		CacheHolder cacheHolder = app.getCacheHolder();
//		
//		Bitmap bitmap = cacheHolder.getBitmapFromMemCache(imageId);
//		if(bitmap != null){
//			return imageReceiver.receiveImage(imageId, bitmap);
//		}
//	}
//	
//	public Bitmap getImageFromMemCache(int imageId){
//		return cacheHolder.getBitmapFromMemCache(imageId);
//	}
//	
//	public Bitmap getImageFromDiskCache(int imageId){
//		cacheHolder.
//		
//	}
}
