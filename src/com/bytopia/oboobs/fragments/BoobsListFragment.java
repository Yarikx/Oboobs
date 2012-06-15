package com.bytopia.oboobs.fragments;

import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;

import com.actionbarsherlock.app.SherlockListFragment;
import com.bytopia.oboobs.adapters.BoobsListAdapter;
import com.bytopia.oboobs.model.Boobs;

public class BoobsListFragment extends SherlockListFragment {

	Activity activity;
	BoobsListAdapter adapter;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = activity;
	}

	public void fill(List<Boobs> boobs) {
		adapter = new BoobsListAdapter(activity, boobs);
		setListAdapter(adapter);
		setRetainInstance(true);
	}

	public void updateItem(Integer a, Bitmap b) {
		adapter.update();
		
	}

}
