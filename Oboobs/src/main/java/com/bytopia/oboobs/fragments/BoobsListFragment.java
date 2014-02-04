package com.bytopia.oboobs.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
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

import com.bytopia.oboobs.BoobsActivity;
import com.bytopia.oboobs.R;
import com.bytopia.oboobs.SearchResultActivity;
import com.bytopia.oboobs.adapters.BoobsListAdapter;
import com.bytopia.oboobs.adapters.ImageProviderAdapter;
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
import rx.subscriptions.CompositeSubscription;

public class BoobsListFragment extends ListFragment implements ActionBar.OnNavigationListener {

    private ServerModule serverModule;
    private List<Pair<Integer, Order>> ordersList;

    private ItemsProvider itemsProvider;
    CompositeSubscription currentProviderSubscription;


    ////*************

    private BehaviorSubject<Order> orders = BehaviorSubject.create(Order.ID);
    private BehaviorSubject<Boolean> descOrder = BehaviorSubject.create(true);
    private Observable<Boolean> orderVisibility;

    public void setItemsProvider(ItemsProvider itemsProvider) {
        if (!itemsProvider.equals(this.itemsProvider)) {
            this.itemsProvider = itemsProvider;
            setListAdapter(null);
            setListShown(false);
            if(currentProviderSubscription != null) currentProviderSubscription.unsubscribe();
            Observable<Boobs> boobsObs = itemsProvider.boobs()
                    .cache()
                    .observeOn(AndroidSchedulers.mainThread());
            BoobsListAdapter adapter = new BoobsListAdapter(getActivity(), new ArrayList<>(), itemsProvider.getMediaUrl());
            Subscription s1 = boobsObs.first().subscribe(any -> setListAdapter(adapter));
            Subscription s2 = boobsObs.subscribe(boobs -> adapter.add(boobs));
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


        Observable<ItemsProvider> providers = Observable.combineLatest(orders, descOrder, (order, desc) ->
                ItemsProviderFactory.from(serverModule, order, desc));

        orderVisibility = providers.map(x -> x.hasSortOrder());

        providers.subscribe(provider -> {
            this.setItemsProvider(provider);
            getActivity().supportInvalidateOptionsMenu();
        });

        setUpActionBar();
    }

    private void setUpActionBar() {
        ActionBar bar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        Context barContext = bar.getThemedContext();
        ArrayAdapter<String> list = new ImageProviderAdapter(barContext,
                R.layout.support_simple_spinner_dropdown_item, ordersList);
//        bar.setDisplayShowTitleEnabled(false);
        bar.setListNavigationCallbacks(list, this);
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

    private void search(String searchText) {
        Intent intent = new Intent(getActivity(), SearchResultActivity.class);
        intent.putExtra(SearchResultActivity.SEARCH, searchText);
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        // Used to put dark icons on light action bar

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
                descOrder.onNext(!descOrder.toBlockingObservable().first());
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public static BoobsListFragment create(String type){
        BoobsListFragment fragment = new BoobsListFragment();
        Bundle b = new Bundle();
        b.putString("type", type);
        fragment.setArguments(b);
        return  fragment;
    }


    @Override
    public boolean onNavigationItemSelected(int i, long l) {
        orders.onNext(ordersList.get(i).second);
        return true;
    }


    private static List<Pair<Integer, Order>> initProviders() {
        List<Pair<Integer, Order>> lProviders = new ArrayList<>();
        lProviders.add(Pair.create(R.string.by_date, Order.ID));
        lProviders.add(Pair.create(R.string.by_interest, Order.INTEREST));
        lProviders.add(Pair.create(R.string.by_rank, Order.RANK));
        lProviders.add(Pair.create(R.string.noise_part, Order.NOISE));
        lProviders.add(Pair.create(R.string.favorites, Order.FAVORITES));
        return lProviders;
    }
}
