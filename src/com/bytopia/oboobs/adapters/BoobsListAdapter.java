package com.bytopia.oboobs.adapters;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bytopia.oboobs.OboobsApp;
import com.bytopia.oboobs.R;
import com.bytopia.oboobs.model.Boobs;
import com.bytopia.oboobs.utils.CacheHolder;
import com.bytopia.oboobs.utils.NetworkUtils;

public class BoobsListAdapter extends ArrayAdapter<Boobs> {

	Activity context;
	LayoutInflater inflater;
	CacheHolder cacheHolder;

	public BoobsListAdapter(Activity context, List<Boobs> objects) {
		super(context, android.R.layout.simple_list_item_1, objects);
		this.context = context;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		cacheHolder = ((OboobsApp) context.getApplication()).getCacheHolder();
	}

	static class BoobsViewHolder {
		ImageView imageView;
	}

	class Tuple<A, B> {
		A a;
		B b;

		public Tuple(A a, B b) {
			this.a = a;
			this.b = b;
		}

	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		BoobsViewHolder holder;
		View view = convertView;
		if (convertView != null) {
			holder = (BoobsViewHolder) convertView.getTag();
		} else {
			view = inflater.inflate(R.layout.boobs_item, parent, false);
			holder = new BoobsViewHolder();
			holder.imageView = (ImageView) view.findViewById(R.id.image);
			view.setTag(holder);
		}

		Bitmap bitmap = cacheHolder.getBitmapFromMemCache(getItem(position).id);
		if (bitmap != null) {
			holder.imageView.setImageBitmap(bitmap);
		} else {
			holder.imageView.setImageResource(R.drawable.ic_launcher);
			new AsyncTask<Void, Void, Bitmap>() {

				@Override
				protected Bitmap doInBackground(Void... params) {

					Integer id = getItem(position).id;
					Bitmap bitmap = cacheHolder.getBitmapFromMemCache(id);
					if (bitmap != null) {
						return bitmap;
					} else {
						bitmap = cacheHolder.getBitmapFromDiskCache(id);
						if (bitmap != null) {
							return bitmap;
						} else {
							bitmap = NetworkUtils
									.downloadImage("http://media.oboobs.ru/"
											+ getItem(position).preview);
							if (bitmap != null) {
								cacheHolder.putImageToCache(id, bitmap);
								return bitmap;
							}
						}
					}

					return null;
				}

				protected void onPostExecute(Bitmap result) {
					update();
				};
			}.execute();
		}

		return view;
	}

	public void update() {
		notifyDataSetChanged();

	}

}
