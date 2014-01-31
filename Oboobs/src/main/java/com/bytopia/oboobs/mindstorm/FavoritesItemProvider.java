package com.bytopia.oboobs.mindstorm;

import com.bytopia.oboobs.db.DbUtils;
import com.bytopia.oboobs.model.Boobs;

import rx.Observable;
import rx.util.async.Async;

/**
 * Created by yarik on 1/31/14.
 */
public class FavoritesItemProvider implements ItemsProvider {

    private final boolean order;
    private String mediaUrl;

    public FavoritesItemProvider(boolean order, String mediaUrl) {
        this.order = order;
        this.mediaUrl = mediaUrl;
    }

    @Override
    public Observable<Boobs> boobs() {
        return Async.start(() -> DbUtils.getFavoriteBoobs(this.order))
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
