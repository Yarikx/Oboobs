package com.bytopia.oboobs.fragments;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.bytopia.oboobs.providers.ImageProvider;
import com.bytopia.oboobs.utils.Tuple;

public class MainStateFragment extends Fragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}
	
	public ImageProvider provider;
	public List<Tuple<Integer,ImageProvider>> providers;
	public boolean desk = true;
}
