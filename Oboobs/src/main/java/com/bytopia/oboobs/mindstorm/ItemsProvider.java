package com.bytopia.oboobs.mindstorm;

import com.bytopia.oboobs.model.Boobs;

import rx.Observable;

/**
 * Created by yarik on 1/31/14.
 */
public interface ItemsProvider {

    public Observable<Boobs> boobs();

    public void next();

    public boolean hasSortOrder();

    public String getMediaUrl();
}
