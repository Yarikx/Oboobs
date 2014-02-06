package com.bytopia.oboobs.mindstorm;

import com.bytopia.oboobs.model.Boobs;
import com.bytopia.oboobs.model.Order;
import com.bytopia.oboobs.rest.ServerModule;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;

/**
 * Created by yarik on 1/31/14.
 */
public class SimpleItemProvider implements ItemsProvider{

    private ServerModule module;
    private Order category;
    private boolean descSortOrder;

    public SimpleItemProvider(ServerModule module, Order category, boolean descSortOrder) {
        this.module = module;
        this.category = category;
        this.descSortOrder = descSortOrder;
        this.boobsObservable = nexts.scan(0, (state, any) -> state + module.utils.getBoobsChunk())
                .flatMap(from -> module.getBoobs(from, category, descSortOrder))
                .cache()
                .observeOn(AndroidSchedulers.mainThread());
    }

    private final PublishSubject<Void> nexts = PublishSubject.create();
    private final Observable<Boobs> boobsObservable;

    @Override
    public Observable<Boobs> boobs() {
        return boobsObservable;
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

    @Override
    public boolean equals(Object o) {
        if(this == o)
            return  true;
        else if(o instanceof SimpleItemProvider){
            SimpleItemProvider that = (SimpleItemProvider) o;
            return this.category == that.category
                    && this.descSortOrder == that.descSortOrder
                    && this.module.equals(that.module);
        }else return false;
    }
}
