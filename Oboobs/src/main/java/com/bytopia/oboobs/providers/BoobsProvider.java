package com.bytopia.oboobs.providers;

import com.bytopia.oboobs.model.Boobs;
import com.bytopia.oboobs.model.Order;
import com.bytopia.oboobs.rest.ServerModule;

import java.util.List;

import rx.Observable;

public abstract class BoobsProvider implements ImageProvider{
	
	private int order = DESK;

    ServerModule module;

    protected BoobsProvider(ServerModule module) {
        this.module = module;
    }

    int from = 0;

    @Override
	public Observable<List<Boobs>> getBoobs() {
        int old = from;
        from +=20;
//		return module.getBoobs(old, getBoobsOrder(),order == DESK);
        return null;
	}
	
	protected abstract Order getBoobsOrder();

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
		return true;
	}

}
