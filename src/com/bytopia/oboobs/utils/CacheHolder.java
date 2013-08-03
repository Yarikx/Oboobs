package com.bytopia.oboobs.utils;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.util.LruCache;

import com.bytopia.oboobs.OboobsApp;
import com.jakewharton.DiskLruCache;
import com.jakewharton.DiskLruCache.Editor;
import com.jakewharton.DiskLruCache.Snapshot;

import java.io.IOException;
import java.io.OutputStream;

public class CacheHolder {

	OboobsApp app;

	// Mem cache
	private LruCache<String, Bitmap> mMemoryCache;

	// Disk cache
	private static final int DEFAULT_DISK_CACHE_SIZE = 10000000;
	private static int maxDiskCacheSize = DEFAULT_DISK_CACHE_SIZE;

	private DiskLruCache diskCache;

	public CacheHolder(OboobsApp app) {
		this.app = app;

		initMemCache();

		initDiskCache();
	}

	private void initDiskCache() {

		maxDiskCacheSize = app.preferences.getInt(
				OboobsApp.MAX_DISK_CACHE_PREF, DEFAULT_DISK_CACHE_SIZE);

		if (Utils.externalStorageAvailable) {
			try {
				diskCache = DiskLruCache.open(Utils.cacheDir, 1, 1,
						maxDiskCacheSize);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void initMemCache() {
		// Get memory class of this device, exceeding this amount will throw an
		// OutOfMemory exception.
		final int memClass = ((ActivityManager) app
				.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();

		// Use 1/8th of the available memory for this memory cache.
		final int cacheSize = 1024 * 1024 * memClass / 6;

		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {

			@TargetApi(12)
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				// The cache size will be measured in bytes rather than number
				// of items.
				if (Build.VERSION.SDK_INT >= 11) {
					return bitmap.getByteCount();
				} else {
					return bitmap.getRowBytes() * bitmap.getHeight();
				}
			}
			
		};

	}

	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		mMemoryCache.put(key, bitmap);
	}

	public Bitmap getBitmapFromMemCache(String key) {
		return mMemoryCache.get(key);
	}

	public Bitmap getBitmapFromDiskCache(String key) {
		try {
			Snapshot snapshot = diskCache.get(key.toString());
			if (snapshot != null) {
				Bitmap bm = BitmapFactory.decodeStream(snapshot
						.getInputStream(0));
				addBitmapToMemoryCache(key, bm);
				return bm;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public void putImageToCache(String url, Bitmap bitmap, int previewHeigth,
			int previewWidth) {

		if (diskCache != null) {
			OutputStream os = null;
			try {
				Editor editor = diskCache.edit(url);
				if (editor != null) {
					os = editor.newOutputStream(0);
					bitmap.compress(CompressFormat.JPEG, 80, os);
					editor.commit();
					if (previewHeigth != 0 && previewWidth != 0) {
						Bitmap sampledBitmap = Utils
								.decodeSampledBitmapFromSnapshot(diskCache,
										url, previewWidth, previewHeigth);
						addBitmapToMemoryCache(url, sampledBitmap);
						return;
					} else {
						addBitmapToMemoryCache(url, bitmap);
						return;
					}
				}
			} catch (Exception e) {
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
		}
		addBitmapToMemoryCache(url, bitmap);
	}

	public boolean diskContain(Integer imageId) {
		try {
			return diskCache.get(imageId.toString()) != null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void clearCache() {
		mMemoryCache.evictAll();
	}

}
