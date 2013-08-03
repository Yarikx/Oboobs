package com.bytopia.oboobs.providers;

import com.bytopia.oboobs.model.Boobs;
import com.bytopia.oboobs.utils.NetworkUtils;

import java.io.IOException;
import java.util.List;

public class AuthorSearchProvider extends SearchProvider{

	private static final long serialVersionUID = -7917197510036159102L;

	public AuthorSearchProvider(String text) {
		super(text);
	}
	
	@Override
	public List<Boobs> getBoobs(int from) throws IOException {
		return NetworkUtils.downloadSearchAuthorResult(searchText);
	}

}
