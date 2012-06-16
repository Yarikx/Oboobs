package com.bytopia.oboobs;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

import com.bytopia.oboobs.model.Boobs;
import com.bytopia.oboobs.utils.CacheHolder;
import com.bytopia.oboobs.utils.NetworkUtils;

public class DownloadService extends IntentService {

	private static final String WIDTH = "width";
	private static final String HEIGTH = "heigth";
	private static final String ID = "id";
	private static final String URL = "url";
	private static final String THREAD_NAME = "BoobsDownloadThread";
	private static final String TYPE = "type";
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
		String url = intent.getExtras().getString(URL);
		Integer id = intent.getExtras().getInt(ID);

		int heigth = intent.getExtras().getInt(HEIGTH);
		int width = intent.getExtras().getInt(WIDTH);

		int type = intent.getExtras().getInt(TYPE);

		Bitmap bitmap = getBitmap(id, url, heigth, width);
		if (bitmap != null) {
			Message message = new Message();
			message.arg1 = id;
			message.obj = bitmap;
			message.arg2 = type;

			resultHandler.sendMessage(message);
		}

	}

	private Bitmap getBitmap(Integer id, String url, int heigth, int width) {
		Bitmap bitmap = cacheHolder.getBitmapFromMemCache(id);
		if (bitmap != null) {
			return bitmap;
		} else {
			bitmap = cacheHolder.getBitmapFromDiskCache(id);
			if (bitmap != null) {
				return bitmap;
			} else {
				bitmap = NetworkUtils.downloadImage("http://media.oboobs.ru/"
						+ url);
				if (bitmap != null) {
					cacheHolder.putImageToCache(id, bitmap, heigth, width);
					return bitmap;
				}
			}
		}
		return null;
	}

	public static void requestImage(Activity context, int senderType,
			Boobs item, boolean preview, int heigth, int width) {
		Intent intent = new Intent(context, DownloadService.class);

		intent.putExtra(ID, item.id);
		// TODO ger real image too
		intent.putExtra(URL, item.preview);
		intent.putExtra(HEIGTH, heigth);
		intent.putExtra(WIDTH, width);

		intent.putExtra(TYPE, senderType);

		context.startService(intent);

	}

}
