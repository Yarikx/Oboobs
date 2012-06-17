package com.bytopia.oboobs.providers;

import com.bytopia.oboobs.model.Order;

public class IdBoobsProvider extends BoobsProvider{

	@Override
	protected Order getBoobsOrder() {
		return Order.ID;
	}

}
