package com.bytopia.oboobs.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.bytopia.oboobs.BoobsActivity;
import com.bytopia.oboobs.adapters.BoobsListAdapter;
import com.bytopia.oboobs.mindstorm.ItemsProvider;
import com.bytopia.oboobs.model.Boobs;

import java.util.ArrayList;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class BoobsListFragment extends ListFragment {

    private ItemsProvider itemsProvider;
    CompositeSubscription s;

    public void setItemsProvider(ItemsProvider itemsProvider) {
        if (!itemsProvider.equals(this.itemsProvider)) {
            this.itemsProvider = itemsProvider;
            setListAdapter(null);
            setListShown(false);
            if(s!= null) s.unsubscribe();
            Observable<Boobs> boobsObs = itemsProvider.boobs()
                    .cache()
                    .observeOn(AndroidSchedulers.mainThread());
            BoobsListAdapter adapter = new BoobsListAdapter(getActivity(), new ArrayList<>(), itemsProvider.getMediaUrl());
            Subscription s1 = boobsObs.first().subscribe(any -> setListAdapter(adapter));
            Subscription s2 = boobsObs.subscribe(boobs -> adapter.add(boobs));
            s = new CompositeSubscription();
            s.add(s1);
            s.add(s2);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setDividerHeight(0);

        getListView().setOnScrollListener(new AbsListView.OnScrollListener() {

            private int visibleThreshold = 5;
            private int previousTotal = 0;
            private boolean loading = true;

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (loading) {
                    if (totalItemCount > previousTotal && totalItemCount > 0) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                    // I load the next page of gigs using a background task,
                    // but you can call any function here.
                    itemsProvider.next();
                    loading = true;
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
        });
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        Intent intent = new Intent(getActivity(), BoobsActivity.class);
//		intent.putExtra(BoobsActivity.BOOBS_LIST, ((Serializable)boobs));
//		intent.putExtra(BoobsActivity.BOOBS_PROVIDER, ((Serializable)currentProvider));
        intent.putExtra(BoobsActivity.ITEM, position);
//        intent.putExtra(BoobsActivity.OFFSET, currentOffset);

        getActivity().startActivity(intent);

    }


}
