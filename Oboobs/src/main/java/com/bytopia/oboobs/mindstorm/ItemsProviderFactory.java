package com.bytopia.oboobs.mindstorm;

import com.bytopia.oboobs.model.Order;
import com.bytopia.oboobs.rest.ServerModule;

/**
 * Created by yarik on 1/31/14.
 */
public class ItemsProviderFactory {

    public static ItemsProvider from(ServerModule module, Order order, boolean desc){
        if(order == Order.NOISE){
            return new NoiseItemProvider(module);
        }else if(order == Order.FAVORITES){
            return new FavoritesItemProvider(desc, module.getMediaUrl());
        }else{
            return new SimpleItemProvider(module, order, desc);
        }
    }
}
