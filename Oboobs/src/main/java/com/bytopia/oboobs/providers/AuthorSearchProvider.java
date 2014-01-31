package com.bytopia.oboobs.providers;

import com.bytopia.oboobs.model.Boobs;
import com.bytopia.oboobs.utils.NetworkUtils;

import java.io.IOException;
import java.util.List;

import rx.Observable;

public class AuthorSearchProvider extends SearchProvider{

	private static final long serialVersionUID = -7917197510036159102L;

	public AuthorSearchProvider(String text) {
		super(text);
	}
	
	public List<Boobs> getBoobs(int from) throws IOException {
		return NetworkUtils.downloadSearchAuthorResult(searchText);
	}

    @Override
    public Observable<List<Boobs>> getBoobs() {
        return null;
    }
}
