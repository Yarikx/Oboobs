package com.bytopia.oboobs.mindstorm;

import com.bytopia.oboobs.OboobsApp;
import com.bytopia.oboobs.db.DbUtils;
import com.bytopia.oboobs.model.Boobs;

import javax.inject.Inject;

import rx.Observable;
import rx.util.async.Async;

/**
 * Created by yarik on 1/31/14.
 */
public class FavoritesItemProvider implements ItemsProvider {

    private final boolean order;
    private String mediaUrl;

    @Inject
    DbUtils dbUtils;

    public FavoritesItemProvider(boolean order, String mediaUrl) {
        this.order = order;
        this.mediaUrl = mediaUrl;
        OboobsApp.instance.inject(this);
    }

    @Override
    public Observable<Boobs> boobs() {
        return Async.start(() -> dbUtils.getFavoriteBoobs(this.order))
                .flatMap(list -> Observable.from(list));
    }

    @Override
    public void next() {}

    @Override
    public boolean hasSortOrder() {
        return true;
    }

    @Override
    public String getMediaUrl() {
        return mediaUrl;
    }
}
