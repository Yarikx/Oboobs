package com.bytopia.oboobs.adapters;

import java.util.HashMap;
import java.util.Map;

import com.bytopia.oboobs.providers.ImageProvider;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class ImageProviderAdapter extends ArrayAdapter<String>{
	
	Integer[] items;
	Map<Integer, String> names;

	public ImageProviderAdapter(Context context, int textViewResourceId, Map<Integer, ImageProvider> map) {
		super(context, textViewResourceId);
		items = new Integer[1];
		items = map.keySet().toArray(items);
		names = new HashMap<Integer, String>();
		for(Integer id : items){
			names.put(id, getContext().getString(id));
		}
	}
	
	@Override
	public String getItem(int position) {
		return names.get(items[position]);
	}
	
	@Override
	public long getItemId(int position) {
		return items[position];
	}
	
	@Override
	public int getCount() {
		return items.length;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return super.getView(position, convertView, parent);
	}
	

}
