package com.bytopia.oboobs.providers;

import com.bytopia.oboobs.model.Boobs;
import com.bytopia.oboobs.utils.NetworkUtils;

import java.io.IOException;
import java.util.List;

public class ModelSearchProvider extends SearchProvider{
	
	private static final long serialVersionUID = -594001374906602689L;

	public ModelSearchProvider(String text) {
		super(text);
	}
	

	@Override
	public List<Boobs> getBoobs(int from) throws IOException {
		return NetworkUtils.downloadSearchModelResult(searchText);
	}

}
