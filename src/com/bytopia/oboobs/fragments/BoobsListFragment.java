package com.bytopia.oboobs.fragments;

import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;

import com.actionbarsherlock.app.SherlockListFragment;
import com.bytopia.oboobs.ImageReceiver;
import com.bytopia.oboobs.OboobsApp;
import com.bytopia.oboobs.adapters.BoobsListAdapter;
import com.bytopia.oboobs.model.Boobs;

public class BoobsListFragment extends SherlockListFragment implements ImageReceiver{

	private static final int SENDER_TYPE = 42;
	Activity activity;
	OboobsApp app;
	BoobsListAdapter adapter;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = activity;
		app = (OboobsApp) activity.getApplication();
		app.setCurentReceiver(this);
	}

	public void fill(List<Boobs> boobs) {
		adapter = new BoobsListAdapter(activity, boobs, SENDER_TYPE);
		setListAdapter(adapter);
		setRetainInstance(true);
	}

	public void updateItem(Integer a, Bitmap b) {
		adapter.update();
		
	}

	@Override
	public void receiveImage(int imageId, Bitmap bitmap) {
		adapter.updateViews(imageId, bitmap, getListView());
	}

	@Override
	public int getSenderType() {
		return SENDER_TYPE;
	}

}
