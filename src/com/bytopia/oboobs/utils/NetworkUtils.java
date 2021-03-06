package com.bytopia.oboobs.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.bytopia.oboobs.OboobsApp;
import com.bytopia.oboobs.model.Boobs;
import com.bytopia.oboobs.model.Order;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

public class NetworkUtils {

	private static String downloadUrl(String myurl) throws IOException {
		InputStream is = null;

		try {
			is = getInputStream(myurl);

			// Convert the InputStream into a string
			String contentAsString = readIt(is);
			return contentAsString;

			// Makes sure that the InputStream is closed after the app is
			// finished using it.
		} finally {
			if (is != null) {
				is.close();
			}
		}
	}

	private static InputStream getInputStream(String myurl)
			throws MalformedURLException, IOException, ProtocolException {
		InputStream is;
		URL url = new URL(myurl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setReadTimeout(10000 /* milliseconds */);
		conn.setConnectTimeout(15000 /* milliseconds */);
		conn.setRequestMethod("GET");
		conn.setDoInput(true);
		// Starts the query
		conn.connect();
		int response = conn.getResponseCode();
		Log.d("debug", "The response is: " + response);
		is = conn.getInputStream();
		return is;
	}

	private static String readIt(InputStream is) throws IOException {
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String temp;
		while ((temp = reader.readLine()) != null) {
			builder.append(temp);
		}
		return builder.toString();
	}

	public static List<Boobs> downloadBoobsList(int offset, int limit,
			Order order, boolean desc) throws IOException {
		String url = RequestBuilder.makeBoobs(offset, limit, order, desc);
		return downloadBoobsUrl(url);
	}

	public static List<Boobs> downloadNoiseList(int limit) throws IOException {
		String url = RequestBuilder.makeNoise(limit);
		return downloadBoobsUrl(url);
	}
	
	public static List<Boobs> downloadSearchModelResult(String searchText) throws IOException {
		String url = RequestBuilder.makeModelSearch(searchText);
		return downloadBoobsUrl(url);
	}
	
	public static List<Boobs> downloadSearchAuthorResult(String searchText) throws IOException {
		String url = RequestBuilder.makeAuthorSearch(searchText);
		return downloadBoobsUrl(url);
	}

	private static List<Boobs> downloadBoobsUrl(String url) throws IOException {
		String jsonResult = downloadUrl(url);
		Gson gson = new Gson();
		List<Boobs> boobsList = gson.fromJson(jsonResult,
				Utils.boobsCollectionType);

		return boobsList;
	}

	public static Bitmap downloadImage(String preview, OboobsApp app) {
		try {
			return BitmapFactory.decodeStream(getInputStream(preview));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (OutOfMemoryError e) {
			app.clearCache();
		}
		return null;
	}

}
