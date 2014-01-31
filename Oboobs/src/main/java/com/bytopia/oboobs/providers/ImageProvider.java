package com.bytopia.oboobs.providers;

import com.bytopia.oboobs.model.Boobs;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import rx.Observable;

public interface ImageProvider extends Serializable {

    public static final int ASC = 0, DESK = 1;

    public Observable<List<Boobs>> getBoobs();

    public boolean hasOrder();

    public void setOrder(int order);

    public boolean isInfinitive();


}
