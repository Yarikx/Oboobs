package com.bytopia.oboobs.rest;

import com.bytopia.oboobs.model.Boobs;

import java.util.List;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

/**
 * Created by yarik on 1/30/14.
 */
public class OboobsClient {

    public static ApiClient getClient(String server){
        RestAdapter adapter = new RestAdapter.Builder()
                .setServer(server)
                .setClient(new OkClient())
                .build();

        return adapter.create(ApiClient.class);
    }

}
