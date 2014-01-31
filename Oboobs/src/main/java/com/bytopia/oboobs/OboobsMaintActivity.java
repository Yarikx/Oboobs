package com.bytopia.oboobs;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.BaseActivity;
import com.bytopia.oboobs.adapters.ImageProviderAdapter;
import com.bytopia.oboobs.adapters.RepoAdapter;
import com.bytopia.oboobs.fragments.BoobsListFragment;
import com.bytopia.oboobs.mindstorm.ItemsProvider;
import com.bytopia.oboobs.mindstorm.ItemsProviderFactory;
import com.bytopia.oboobs.mindstorm.SimpleItemProvider;
import com.bytopia.oboobs.model.Order;
import com.bytopia.oboobs.model.Repo;
import com.bytopia.oboobs.rest.ServerModule;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.subjects.BehaviorSubject;

public class OboobsMaintActivity extends BaseActivity implements
        ActionBar.OnNavigationListener {

    private List<Pair<Integer, Order>> ordersList;

    private BoobsListFragment boobsListFragment;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView leftDrawer;

    ////*************

    private BehaviorSubject<ServerModule> serverModules = BehaviorSubject.create(new ServerModule(ServerModule.ServerType.boobs));
    private BehaviorSubject<Order> orders = BehaviorSubject.create(Order.ID);
    private BehaviorSubject<Boolean> descOrder = BehaviorSubject.create(true);

    Observable<Boolean> orderVisibility;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        //init side bar
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setHomeButtonEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        );

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        bar.setDisplayHomeAsUpEnabled(true);
        bar.setHomeButtonEnabled(true);

        leftDrawer = (ListView) findViewById(R.id.left_drawer);
        leftDrawer.setAdapter(RepoAdapter.getDefaultRepoAdapter(this));
        leftDrawer.setOnItemClickListener((AdapterView<?> adapterView, View view, int i, long l) -> {
            Repo repo = (Repo) adapterView.getItemAtPosition(i);
            serverModules.onNext(new ServerModule(ServerModule.ServerType.valueOf(repo.getKeyName())));
            mDrawerLayout.closeDrawers();
        });

        //state
        if(savedInstanceState != null){
            //TODO handle orientation change
        }


        //other
        boobsListFragment = (BoobsListFragment) fragmentManager
                .findFragmentByTag("BoobsList");

        ordersList = initProviders();
        Context barContext = bar.getThemedContext();

        ArrayAdapter<String> list = new ImageProviderAdapter(barContext,
                R.layout.support_simple_spinner_dropdown_item, ordersList);

        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        bar.setListNavigationCallbacks(list, this);
        list.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        Observable<ItemsProvider> providers = Observable.combineLatest(serverModules, orders, descOrder, (module, order, desc) ->
                ItemsProviderFactory.from(module, order, desc));

        orderVisibility = providers.map(x -> x.hasSortOrder());

        providers.subscribe(provider -> {
            boobsListFragment.setItemsProvider(provider);
            supportInvalidateOptionsMenu();
        });

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        final Order order = ordersList.get(itemPosition).second;
        orders.onNext(order);
        return true;
    }

    EditText search;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Used to put dark icons on light action bar

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_items, menu);

        search = (EditText) MenuItemCompat.getActionView(menu.findItem(R.id.search));
        search.setOnEditorActionListener((TextView v, int actionId,
                                          KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard(v);
                search(v.getText().toString());
                return true;
            }
            return false;
        });

        return true;
    }

    private void search(String searchText) {
        Intent intent = new Intent(this, SearchResultActivity.class);
        intent.putExtra(SearchResultActivity.SEARCH, searchText);
        startActivity(intent);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem order = menu.findItem(R.id.order);
        order.setIcon(descOrder.toBlockingObservable().first() ? R.drawable.desc_dark
                : R.drawable.asc_dark);
        order.setVisible(orderVisibility.toBlockingObservable().first());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        int id = item.getItemId();

        switch (id) {
            case R.id.order:
                descOrder.onNext(!descOrder.toBlockingObservable().first());
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}