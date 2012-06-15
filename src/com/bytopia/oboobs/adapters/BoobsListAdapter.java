package com.bytopia.oboobs.adapters;

import java.util.List;

import com.bytopia.oboobs.model.Boobs;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class BoobsListAdapter extends ArrayAdapter<Boobs> {

	public BoobsListAdapter(Context context, List<Boobs> objects) {
		super(context, android.R.layout.simple_list_item_1, objects);
		// TODO Auto-generated constructor stub
	}

}
