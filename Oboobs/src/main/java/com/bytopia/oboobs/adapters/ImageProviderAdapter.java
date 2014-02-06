package com.bytopia.oboobs.adapters;

import android.content.Context;
import android.util.Pair;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.bytopia.oboobs.model.Order;

import java.util.List;

public class ImageProviderAdapter extends ArrayAdapter<String>{
	
	SparseArray<String> names;
	List<Pair<Integer, Order>> providers;
	

	public ImageProviderAdapter(Context context, int textViewResourceId, List<Pair<Integer, Order>> providers) {
		super(context, textViewResourceId);
		this.providers = providers;
		names = new SparseArray<>();
		for(Pair<Integer, Order> p : providers){
			names.put(p.first, getContext().getString(p.first));
		}
	}
	
	@Override
	public String getItem(int position) {
		return names.get(providers.get(position).first);
	}
	
	@Override
	public long getItemId(int position) {
		return providers.get(position).first;
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
