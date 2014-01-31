package com.bytopia.oboobs.providers;

import com.bytopia.oboobs.db.DbUtils;
import com.bytopia.oboobs.model.Boobs;

import java.io.IOException;
import java.util.List;

import rx.Observable;

public class FavoritesProvider implements ImageProvider {
	
	private int order = DESK;

	public Observable<List<Boobs>> getBoobs(int from) throws IOException {
		if (from == 0) {
			List<Boobs> boobs = DbUtils.getFavoriteBoobs(this.order == DESK);
			return null; //Observable.from(boobs);
		} else {
			return null;
		}
	}

    @Override
    public Observable<List<Boobs>> getBoobs() {
        return null;
    }

    @Override
	public boolean hasOrder() {
		return true;
	}

	@Override
	public void setOrder(int order) {
		this.order = order;
	}

	@Override
	public boolean isInfinitive() {
		return false;
	}
}
