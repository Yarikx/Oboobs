package com.bytopia.oboobs.providers;

import com.bytopia.oboobs.model.Boobs;

import java.util.List;

import rx.Observable;

public class ModelSearchProvider extends SearchProvider{
	
	private static final long serialVersionUID = -594001374906602689L;

	public ModelSearchProvider(String text) {
		super(text);
	}

    public Observable<List<Boobs>> getBoobs(int from) {
        return null;
    }

    @Override
    public Observable<List<Boobs>> getBoobs() {
        return null;
    }


//	@Override
//	public List<Boobs> getBoobs(int from) throws IOException {
//		return NetworkUtils.downloadSearchModelResult(searchText);
//	}

}
