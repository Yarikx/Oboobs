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

        Context barContext = bar.getThemedContext();

        ArrayAdapter<String> list = new ImageProviderAdapter(barContext,
                R.layout.support_simple_spinner_dropdown_item, null);

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
    public boolean onNavigationItemSelected(int i, long l) {
        return false;
    }
}