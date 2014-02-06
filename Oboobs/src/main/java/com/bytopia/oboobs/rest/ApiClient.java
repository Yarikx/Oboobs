package com.bytopia.oboobs.rest;

import com.bytopia.oboobs.model.Boobs;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

/**
 * Created by yarik on 1/30/14.
 */
public interface ApiClient {

    @GET("/{type}/{start}/{count}/{order}/")
    Observable<List<Boobs>> items(
            @Path("type") String type,
            @Path("start") int start,
            @Path("count") int count,
            @Path("order")String order);

    @GET("/noise/{count}/")
    Observable<List<Boobs>> noise(
            @Path("count") int count);

}
