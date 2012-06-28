package com.bytopia.oboobs.fragments;

import java.io.Serializable;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.bytopia.oboobs.BuildConfig;
import com.bytopia.oboobs.providers.ImageProvider;
import com.bytopia.oboobs.utils.Tuple;

public class MainStateFragment extends Fragment {

	private static final String PROVIDER = "provider";
	private static final String PROVIDERS = "providers";
	private static final String DESK = "desk";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);

		try {
			desk = savedInstanceState.getBoolean(DESK);
			providers = (List<Tuple<Integer, ImageProvider>>) savedInstanceState
					.getSerializable(PROVIDERS);
			provider = (ImageProvider) savedInstanceState
					.getSerializable(PROVIDER);
			
			if(BuildConfig.DEBUG){
				Log.d("restore", "yeah, providers restored");
			}
		} catch (NullPointerException e) {
			
		}
	}

	public ImageProvider provider;
	public List<Tuple<Integer, ImageProvider>> providers;
	public boolean desk = true;

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(DESK, desk);
		outState.putSerializable(PROVIDERS, (Serializable) providers);
		outState.putSerializable(PROVIDER, provider);
	}
}
