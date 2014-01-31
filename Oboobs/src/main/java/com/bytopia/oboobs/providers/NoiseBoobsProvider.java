package com.bytopia.oboobs.providers;

import com.bytopia.oboobs.model.Boobs;
import com.bytopia.oboobs.utils.NetworkUtils;
import com.bytopia.oboobs.utils.Utils;

import java.io.IOException;
import java.util.List;

import rx.Observable;

public class NoiseBoobsProvider implements ImageProvider{

//	@Override
//	public List<Boobs> getBoobs(int from) throws IOException {
//		return NetworkUtils.downloadNoiseList(Utils.getBoobsChunk());
//	}

    public Observable<List<Boobs>> getBoobs(int from) {
        return null;
    }

    @Override
    public Observable<List<Boobs>> getBoobs() {
        return null;
    }

    @Override
	public boolean hasOrder() {
		return false;
	}

	@Override
	public void setOrder(int order) {}
	
	@Override
	public boolean isInfinitive() {
		return true;
	}

}
