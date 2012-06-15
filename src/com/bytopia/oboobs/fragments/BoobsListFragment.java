package com.bytopia.oboobs.fragments;

import java.util.List;

import android.app.Activity;

import com.actionbarsherlock.app.SherlockListFragment;
import com.bytopia.oboobs.adapters.BoobsListAdapter;
import com.bytopia.oboobs.model.Boobs;

public class BoobsListFragment extends SherlockListFragment {

	Activity activity;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = activity;
	}

	public void fill(List<Boobs> boobs) {
		setListAdapter(new BoobsListAdapter(activity, boobs));
		setRetainInstance(true);
	}

}
