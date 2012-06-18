package com.bytopia.oboobs.adapters;

import java.util.List;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.bytopia.oboobs.providers.ImageProvider;
import com.bytopia.oboobs.utils.Tuple;

public class ImageProviderAdapter extends ArrayAdapter<String>{
	
	SparseArray<String> names;
	List<Tuple<Integer, ImageProvider>> providers;
	

	public ImageProviderAdapter(Context context, int textViewResourceId, List<Tuple<Integer, ImageProvider>> providers) {
		super(context, textViewResourceId);
		this.providers = providers;
		names = new SparseArray<String>();
		for(Tuple<Integer, ImageProvider> p : providers){
			names.put(p.a, getContext().getString(p.a));
		}
	}
	
	@Override
	public String getItem(int position) {
		return names.get(providers.get(position).a);
	}
	
	@Override
	public long getItemId(int position) {
		return providers.get(position).a;
	}
	
	@Override
	public int getCount() {
		return providers.size();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return super.getView(position, convertView, parent);
	}
	

}
