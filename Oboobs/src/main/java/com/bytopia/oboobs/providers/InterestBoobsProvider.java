package com.bytopia.oboobs.providers;

import com.bytopia.oboobs.model.Order;
import com.bytopia.oboobs.rest.ServerModule;

public class InterestBoobsProvider extends BoobsProvider{

    protected InterestBoobsProvider(ServerModule module) {
        super(module);
    }

    @Override
	protected Order getBoobsOrder() {
		return Order.INTEREST;
	}

}
