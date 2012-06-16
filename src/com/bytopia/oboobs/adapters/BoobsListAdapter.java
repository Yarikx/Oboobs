package com.bytopia.oboobs.adapters;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bytopia.oboobs.DownloadService;
import com.bytopia.oboobs.ImageReceiver;
import com.bytopia.oboobs.OboobsApp;
import com.bytopia.oboobs.R;
import com.bytopia.oboobs.model.Boobs;
import com.bytopia.oboobs.utils.CacheHolder;

public class BoobsListAdapter extends ArrayAdapter<Boobs> implements ImageReceiver{
	
	Activity context;
	LayoutInflater inflater;
	CacheHolder cacheHolder;
	OboobsApp app;

	int h = 0, w = 0;

	public BoobsListAdapter(Activity context, List<Boobs> objects) {
		super(context, android.R.layout.simple_list_item_1, objects);
		this.context = context;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		cacheHolder = ((OboobsApp) context.getApplication()).getCacheHolder();
		app = (OboobsApp) context.getApplication();
		app.setCurentReceiver(this);
		
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
			h = holder.imageView.getHeight();
			w = holder.imageView.getWidth();
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
			DownloadService.requestImage(context, this, getItem(position), true, h, w);
		}

		return view;
	}

	public void update() {
		notifyDataSetChanged();

	}

	@Override
	public void receiveImage(int imageId, Bitmap bitmap) {
		update();
	}

	@Override
	public int getSenderType() {
		return 42;
	}

}
