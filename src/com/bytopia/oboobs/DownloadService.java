package com.bytopia.oboobs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;

import com.bytopia.oboobs.model.Boobs;
import com.bytopia.oboobs.utils.CacheHolder;
import com.bytopia.oboobs.utils.NetworkUtils;
import com.bytopia.oboobs.utils.Utils;

public class DownloadService extends IntentService {

	private static final String WIDTH = "width";
	private static final String HEIGTH = "heigth";
	private static final String THREAD_NAME = "BoobsDownloadThread";
	private static final String TYPE = "type";
	private static final String PREVIEW = "preview";
	private static final String BOOBS = "boobs";
	OboobsApp app;
	CacheHolder cacheHolder;
	Handler resultHandler;

	public DownloadService() {
		super(THREAD_NAME);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		app = (OboobsApp) getApplication();
		resultHandler = app.getResultHandler();
		cacheHolder = app.getCacheHolder();
	}

	//
	@Override
	protected void onHandleIntent(Intent intent) {

		Boobs boobs = (Boobs) intent.getSerializableExtra(BOOBS);

		int heigth = intent.getExtras().getInt(HEIGTH);
		int width = intent.getExtras().getInt(WIDTH);

		int type = intent.getExtras().getInt(TYPE);
		boolean preview = intent.getExtras().getBoolean(PREVIEW);

		Bitmap bitmap = getBitmap(boobs, heigth, width, preview);
		if (bitmap != null) {
			Message message = new Message();
			message.arg1 = boobs.id;
			message.obj = bitmap;
			message.arg2 = type;

			resultHandler.sendMessageAtFrontOfQueue(message);
		}

	}

	private Bitmap getBitmap(Boobs boobs, int heigth, int width, boolean preview) {
		String key = preview ? boobs.getPreviewUrl() : boobs.getFullImageUrl();
		Bitmap bitmap = cacheHolder.getBitmapFromMemCache(key);
		if (bitmap != null) {
			return bitmap;
		} else {
			if (boobs.hasFile && new File(boobs.filePath).exists()) {
				try {
					final BitmapFactory.Options options = new BitmapFactory.Options();
					options.inJustDecodeBounds = true;
					BitmapFactory.decodeStream(new FileInputStream(
							boobs.filePath), null, options);
						if (preview) {
							// Calculate inSampleSize
							options.inSampleSize = Utils.calculateInSampleSize(options, width,
									heigth);

							// Decode bitmap with inSampleSize set
							options.inJustDecodeBounds = false;
							Bitmap sampled =  BitmapFactory.decodeStream(new FileInputStream(boobs.filePath),
									new Rect(-1, -1, -1, -1), options);
							
							cacheHolder.addBitmapToMemoryCache(key, sampled);
							return sampled;
						} else {
							bitmap = BitmapFactory.decodeFile(boobs.filePath);
							cacheHolder.addBitmapToMemoryCache(key, bitmap);
						}
						return bitmap;
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
//				catch (OutOfMemoryError e){
//					try {
//						Debug.dumpHprofData("/sdcard/oomCrash.hprof");
//					} catch (IOException e1) {
//						e1.printStackTrace();
//					}
//					throw e;
//					
//				}
			}

			bitmap = cacheHolder.getBitmapFromDiskCache(key);
			if (bitmap != null) {
				return bitmap;
			} else {
				bitmap = NetworkUtils.downloadImage(key,app);
				if (bitmap != null) {
					cacheHolder.putImageToCache(key, bitmap, heigth, width);
					return bitmap;
				}
			}
		}
		return null;
	}

	public static void requestImage(Activity context, int senderType,
			Boobs item, boolean preview, int heigth, int width) {
		Intent intent = new Intent(context, DownloadService.class);

		intent.putExtra(BOOBS, item);
		intent.putExtra(HEIGTH, heigth);
		intent.putExtra(WIDTH, width);

		intent.putExtra(TYPE, senderType);

		intent.putExtra(PREVIEW, preview);

		context.startService(intent);

	}

}
