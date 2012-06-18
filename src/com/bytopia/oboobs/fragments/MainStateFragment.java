package com.bytopia.oboobs.fragments;

import java.util.Map;

import com.bytopia.oboobs.providers.ImageProvider;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class MainStateFragment extends Fragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}
	
	public ImageProvider provider;
	public Map<Integer, ImageProvider> providers;
	public boolean desk = true;
}
