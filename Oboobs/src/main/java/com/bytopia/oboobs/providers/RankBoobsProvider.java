package com.bytopia.oboobs.providers;

import com.bytopia.oboobs.model.Order;
import com.bytopia.oboobs.rest.ServerModule;

public class RankBoobsProvider extends BoobsProvider{

    protected RankBoobsProvider(ServerModule module) {
        super(module);
    }

    @Override
	protected Order getBoobsOrder() {
		return Order.RANK;
	}

}
