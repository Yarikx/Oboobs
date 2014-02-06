package com.bytopia.oboobs.rest;

import android.util.Log;

import com.bytopia.oboobs.OboobsApp;
import com.bytopia.oboobs.R;
import com.bytopia.oboobs.model.Boobs;
import com.bytopia.oboobs.model.Order;
import com.bytopia.oboobs.utils.Utils;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by yarik on 1/31/14.
 */
public class ServerModule {
    public enum ServerType {
        boobs,
        butts
    }

    @Inject
    public Utils utils;

    private final String server;
    private final String type;

    public String getMediaUrl() {
        return mediaUrl;
    }

    private final String mediaUrl;
    private final ApiClient client;

    public ServerModule(ServerType serverType) {
        OboobsApp app = OboobsApp.instance;
        app.inject(this);
        type = serverType.toString();
        server = String.format(app.getResources().getString(R.string.api_url), type);
        mediaUrl = String.format(app.getResources().getString(R.string.media_url), type);
        client = OboobsClient.getClient(server);
    }

    public Observable<Boobs> getBoobs(int from, Order order, boolean desc) {
        Log.d("oboobs", String.format("bobos request to %d, %s, %b",from, order, desc));
        return client.items(type, from, utils.getBoobsChunk(), createOrder(order, desc)).flatMap(list -> Observable.from(list));
    }

    public Observable<Boobs> noise() {
        return client.noise(utils.getBoobsChunk()).flatMap(list -> Observable.from(list));
    }

    private static String createOrder(Order order, boolean desc) {
        return (desc ? "-" : "") + order;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o)
            return true;
        else return o instanceof ServerModule && ((ServerModule) o).type.equals(this.type);
    }
}
