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
public class SimpleItemProvider implements ItemsProvider{

    private ServerModule module;
    private Order category;
    private boolean descSortOrder;

    public SimpleItemProvider(ServerModule module, Order category, boolean descSortOrder) {
        this.module = module;
        this.category = category;
        this.descSortOrder = descSortOrder;
    }

    private PublishSubject<Void> nexts = PublishSubject.create();

    @Override
    public Observable<Boobs> boobs() {
        return nexts.scan(0, (state, any) -> state + Utils.getBoobsChunk())
                .flatMap(from -> module.getBoobs(from, category, descSortOrder));
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
