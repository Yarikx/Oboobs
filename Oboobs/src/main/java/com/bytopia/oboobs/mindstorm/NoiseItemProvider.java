package com.bytopia.oboobs.mindstorm;

import com.bytopia.oboobs.model.Boobs;
import com.bytopia.oboobs.model.Order;
import com.bytopia.oboobs.rest.ServerModule;
import com.bytopia.oboobs.utils.Utils;

import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by yarik on 1/31/14.
 */
public class NoiseItemProvider implements ItemsProvider{

    private ServerModule module;

    public NoiseItemProvider(ServerModule module) {
        this.module = module;
    }

    private PublishSubject<Void> nexts = PublishSubject.create();

    @Override
    public Observable<Boobs> boobs() {
        return nexts.scan(0, (state, any) -> state + Utils.getBoobsChunk())
                .flatMap(from -> module.noise(from));
    }

    @Override
    public void next() {
        nexts.onNext(null);
    }

    @Override
    public boolean hasSortOrder() {
        return true;
    }

    @Override
    public String getMediaUrl() {
        return module.getMediaUrl();
    }
}