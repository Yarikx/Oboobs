package com.bytopia.oboobs;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
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
import com.bytopia.oboobs.fragments.MainStateFragment;
import com.bytopia.oboobs.fragments.dialogs.NetworkErrorDialog;
import com.bytopia.oboobs.model.Boobs;
import com.bytopia.oboobs.model.Repo;
import com.bytopia.oboobs.providers.FavoritesProvider;
import com.bytopia.oboobs.providers.IdBoobsProvider;
import com.bytopia.oboobs.providers.ImageProvider;
import com.bytopia.oboobs.providers.InterestBoobsProvider;
import com.bytopia.oboobs.providers.NoiseBoobsProvider;
import com.bytopia.oboobs.providers.RankBoobsProvider;
import com.bytopia.oboobs.utils.RequestBuilder;
import com.bytopia.oboobs.utils.Tuple;

import java.util.ArrayList;
import java.util.List;

public class OboobsMaintActivity extends BaseActivity implements
		ActionBar.OnNavigationListener, BoobsListFragmentHolder {

	private static final String STATE_TAG = "state";

	private List<Tuple<Integer, ImageProvider>> providers;

	private BoobsListFragment boobsListFragment;

	private MainStateFragment stateFragment;
    
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView leftDrawer;

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
        leftDrawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Repo repo = (Repo) adapterView.getItemAtPosition(i);
                Boobs.mediaUrl = repo.getMediaUrl();
                RequestBuilder.apiUrl = repo.getApiUrl();
                RequestBuilder.boobsPart = repo.getKeyName();

                boobsListFragment.getBoobsFrom(stateFragment.provider);
                supportInvalidateOptionsMenu();
                mDrawerLayout.closeDrawers();
            }
        });
        
        //other

		boobsListFragment = (BoobsListFragment) fragmentManager
				.findFragmentByTag("BoobsList");

		stateFragment = (MainStateFragment) fragmentManager
				.findFragmentByTag(STATE_TAG);
		if (stateFragment == null) {
			stateFragment = new MainStateFragment();
			fragmentManager.beginTransaction().add(stateFragment, STATE_TAG)
					.commit();
			stateFragment.providers = initProviders();
		}
		providers = stateFragment.providers;

		Context barContext = bar.getThemedContext();

		ArrayAdapter<String> list = new ImageProviderAdapter(barContext,
				R.layout.support_simple_spinner_dropdown_item, providers);

		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		bar.setListNavigationCallbacks(list, this);
		list.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

		if (stateFragment.provider != null) {

			for (int i = 0; i < providers.size(); i++) {
				if (stateFragment.provider == providers.get(i).b) {
					bar.setSelectedNavigationItem(i);
					break;
				}
			}
		}

		bar.setSelectedNavigationItem(stateFragment.selectedProviderPosition);

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

    private List<Tuple<Integer, ImageProvider>> initProviders() {
		List<Tuple<Integer, ImageProvider>> lProviders = new ArrayList<Tuple<Integer, ImageProvider>>();
		lProviders.add(new Tuple<Integer, ImageProvider>(R.string.by_date,
				new IdBoobsProvider()));
		lProviders.add(new Tuple<Integer, ImageProvider>(R.string.by_rank,
				new RankBoobsProvider()));
		lProviders.add(new Tuple<Integer, ImageProvider>(R.string.by_interest,
				new InterestBoobsProvider()));
		lProviders.add(new Tuple<Integer, ImageProvider>(R.string.random,
				new NoiseBoobsProvider()));
		lProviders.add(new Tuple<Integer, ImageProvider>(R.string.favorites,
				new FavoritesProvider()));
		return lProviders;
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {

		stateFragment.selectedProviderPosition = itemPosition;
		final ImageProvider provider = providers.get(itemPosition).b;

		if (stateFragment.provider != provider) {
			stateFragment.provider = provider;
			provider.setOrder(stateFragment.desk ? ImageProvider.DESK
					: ImageProvider.ASC);
			boobsListFragment.getBoobsFrom(provider);

            supportInvalidateOptionsMenu();
		}

		return true;
	}

	EditText search;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Used to put dark icons on light action bar

		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.main_items, menu);

		search = (EditText) MenuItemCompat.getActionView(menu.findItem(R.id.search));
		search.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					hideKeyboard(v);
					search(v.getText().toString());
					return true;
				}
				return false;
			}

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

		boolean isDark = app.isDark;

		if (stateFragment != null && stateFragment.provider != null) {

			if (isDark) {
				order.setIcon(stateFragment.desk ? R.drawable.desc_dark
						: R.drawable.asc_dark);
			} else {
				order.setIcon(stateFragment.desk ? R.drawable.desc_light
						: R.drawable.ask_light);
			}

			if (stateFragment.provider.hasOrder()) {
				order.setVisible(true);
			} else {
				order.setVisible(false);
			}

		}

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
			stateFragment.desk = !stateFragment.desk;

			if (stateFragment != null && stateFragment.provider != null) {
				stateFragment.provider
						.setOrder(stateFragment.desk ? ImageProvider.DESK
								: ImageProvider.ASC);
				boobsListFragment.getBoobsFrom(stateFragment.provider);
			}

			supportInvalidateOptionsMenu();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void handleErrorWhileLoadingProvider() {
		createSwitchToFavoritesDialog();

	}

	private void createSwitchToFavoritesDialog() {
		DialogFragment newFragment = NetworkErrorDialog.newInstance(providers
				.size() - 1);
		newFragment.show(getSupportFragmentManager(), "dialog");
	}
}