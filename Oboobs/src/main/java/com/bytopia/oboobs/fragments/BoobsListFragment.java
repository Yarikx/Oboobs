package com.bytopia.oboobs.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.bytopia.oboobs.R;
import com.bytopia.oboobs.SearchResultActivity;
import com.bytopia.oboobs.adapters.BoobsListAdapter;
import com.bytopia.oboobs.adapters.ImageProviderAdapter;
import com.bytopia.oboobs.mindstorm.BoobsPagesFragment;
import com.bytopia.oboobs.mindstorm.ItemsProvider;
import com.bytopia.oboobs.mindstorm.ItemsProviderFactory;
import com.bytopia.oboobs.model.Boobs;
import com.bytopia.oboobs.model.Order;
import com.bytopia.oboobs.rest.ServerModule;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

public class BoobsListFragment extends ListFragment {

    private ServerModule serverModule;
    private List<Pair<Integer, Order>> ordersList;

    private ItemsProvider itemsProvider;
    CompositeSubscription currentProviderSubscription;

    private Contract contract;


    ////*************

    private final BehaviorSubject<Order> orders = BehaviorSubject.create(Order.ID);
    private final BehaviorSubject<Boolean> descOrder = BehaviorSubject.create(true);
    private Observable<Boolean> orderVisibility;
    private Observable<ItemsProvider> providers;
    private BoobsListAdapter adapter;

    {
        orders.subscribe(order -> currentOrder = order);
    }

    private Order currentOrder;

    public void setItemsProvider(ItemsProvider itemsProvider) {
        if (!itemsProvider.equals(this.itemsProvider)) {
            this.itemsProvider = itemsProvider;
            Log.e("events", "setting new list");
            if (isResumed())
                setListShown(false);
            if (currentProviderSubscription != null) currentProviderSubscription.unsubscribe();
            Observable<Boobs> boobsObs = itemsProvider.boobs()
                    .observeOn(AndroidSchedulers.mainThread());
            adapter.clear();

            Subscription s1 = boobsObs.first().subscribe(x -> setListShown(true));
            Subscription s2 = boobsObs.subscribe(boobs -> {
                adapter.add(boobs);
                adapter.notifyDataSetChanged();
            });
            currentProviderSubscription = new CompositeSubscription();
            currentProviderSubscription.add(s1);
            currentProviderSubscription.add(s2);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        serverModule = new ServerModule(ServerModule.ServerType.valueOf(getArguments().getString("type")));
//        ordersList = initProviders();
        adapter = new BoobsListAdapter(getActivity(), new ArrayList<>(), serverModule.getMediaUrl());
        setListAdapter(adapter);

        providers = Observable.combineLatest(orders.filter(order -> isResumed()).distinctUntilChanged(), descOrder.distinctUntilChanged(),
                (order, desc) -> {
                    Log.e("events", "changed " + order + " " + desc);
                    return ItemsProviderFactory.from(serverModule, order, desc);
                });

        orderVisibility = providers.map(x -> x.hasSortOrder());

        providers.subscribe(provider -> {
            this.setItemsProvider(provider);
            getActivity().supportInvalidateOptionsMenu();
        });

        ordersList = initProviders();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
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
    public void onResume() {
        super.onResume();
        setUpActionBar((ActionBarActivity) getActivity());
    }

    private void setUpActionBar(ActionBarActivity activity) {
        Log.e("events", "setting up actionbar");
        ActionBar bar = activity.getSupportActionBar();
        Context barContext = bar.getThemedContext();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        ArrayAdapter<String> spinnerAdapter = new ImageProviderAdapter(barContext,
                R.layout.support_simple_spinner_dropdown_item, ordersList);
        bar.setListNavigationCallbacks(spinnerAdapter, (int position, long l) -> {
            orders.onNext(ordersList.get(position).second);
            getActivity().supportInvalidateOptionsMenu();
            return true;
        });
        Order order = currentOrder;
        for (Pair<Integer, Order> orderPair : ordersList) {
            if (orderPair.second.equals(order)) {
                bar.setSelectedNavigationItem(ordersList.indexOf(orderPair));
                break;
            }
        }
        bar.setDisplayShowTitleEnabled(true);
    }


    private void search(String searchText) {
        Intent intent = new Intent(getActivity(), SearchResultActivity.class);
        intent.putExtra(SearchResultActivity.SEARCH, searchText);
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.main_items, menu);

        super.onCreateOptionsMenu(menu, menuInflater);
        EditText search = (EditText) MenuItemCompat.getActionView(menu.findItem(R.id.search));
        search.setOnEditorActionListener((TextView v, int actionId,
                                          KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                //TODO hide keyboard
//                hideKeyboard(v);
                search(v.getText().toString());
                return true;
            }
            return false;
        });

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem order = menu.findItem(R.id.order);
        order.setIcon(descOrder.toBlockingObservable().first() ? R.drawable.desc_dark
                : R.drawable.asc_dark);
        order.setVisible(orderVisibility.toBlockingObservable().first());
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.order:
                Log.e("events", "order change " + !descOrder.toBlockingObservable().first());
                descOrder.onNext(!descOrder.toBlockingObservable().first());
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static BoobsListFragment create(String type) {
        BoobsListFragment fragment = new BoobsListFragment();
        Bundle b = new Bundle();
        b.putString("type", type);
        fragment.setArguments(b);
        return fragment;
    }

    private static List<Pair<Integer, Order>> initProviders() {
        List<Pair<Integer, Order>> lProviders = new ArrayList<>();
        lProviders.add(Pair.create(R.string.by_date, Order.ID));
        lProviders.add(Pair.create(R.string.by_interest, Order.INTEREST));
        lProviders.add(Pair.create(R.string.by_rank, Order.RANK));
        lProviders.add(Pair.create(R.string.random, Order.NOISE));
        lProviders.add(Pair.create(R.string.favorites, Order.FAVORITES));
        return lProviders;
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Fragment f = new BoobsPagesFragment(itemsProvider, position);
        contract.changeContent(f);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        contract = (Contract) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        contract = null;
    }

    public static interface Contract {
        public void changeContent(Fragment fragment);
    }
}
