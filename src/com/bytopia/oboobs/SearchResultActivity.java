package com.bytopia.oboobs;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.BaseActivity;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.bytopia.oboobs.fragments.BoobsListFragment;
import com.bytopia.oboobs.model.Boobs;
import com.bytopia.oboobs.providers.AuthorSearchProvider;
import com.bytopia.oboobs.providers.ModelSearchProvider;
import com.bytopia.oboobs.providers.SearchProvider;
import com.bytopia.oboobs.utils.NetworkUtils;

public class SearchResultActivity extends BaseActivity implements
		ActionBar.TabListener, BoobsListFragmentHolder {

	public static final String SEARCH = "search";
	public static final String SEARCH_TYPE = "search_type";
	public static final int SEARCH_BOTH = 0;
	public static final int SEARCH_MODEL = 1;
	public static final int SEARCH_AUTHOR = 2;

	private String searchText;
	private boolean searchModel = true;
	private boolean searchAuthor = true;

	private int searchType = SEARCH_BOTH;

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	private EditText searchView;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		searchText = getIntent().getExtras().getString(SEARCH);
		Integer t = getIntent().getExtras().getInt(SEARCH_TYPE);
		if (t != null) {
			searchType = t;
		} else {
			searchType = SEARCH_BOTH;
		}

		switch (searchType) {
		case SEARCH_BOTH:
			searchModel = true;
			searchAuthor = true;
			break;
		case SEARCH_MODEL:
			searchModel = true;
			searchAuthor = false;
			break;
		case SEARCH_AUTHOR:
			searchModel = false;
			searchAuthor = true;
			break;
		}

		setContentView(R.layout.search_layout);
		{
			View view = getLayoutInflater().inflate(
					R.layout.non_collapsible_search, null);
			searchView = (EditText) view.findViewById(R.id.search_field);
			bar.setCustomView(view);
			bar.setDisplayShowCustomEnabled(true);
		}
		
		searchView.setText(searchText);
		searchView.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_SEARCH){
					searchText = v.getText().toString();
					hideKeyboard(v);
					updateSearch();
				}
				return true;
			}
		});

		// ------------------
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the action bar.
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab.
		// We can also use ActionBar.Tab#select() to do this if we have a
		// reference to the
		// Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						bar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		if (searchModel && searchAuthor) {
			for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
				bar.addTab(bar.newTab()
						.setText(mSectionsPagerAdapter.getPageTitle(i))
						.setTabListener(this));
			}
		}

		// ------------------

		// LoaderManager loaderManager = getSupportLoaderManager();

	}

	protected void updateSearch() {
		if (searchModel) {
			SearchProvider provider = new ModelSearchProvider(searchText);
			mSectionsPagerAdapter.setModelBoobs(provider);
		}

		if (searchAuthor) {
			SearchProvider provider = new AuthorSearchProvider(searchText);
			mSectionsPagerAdapter.setAuthorBoobs(provider);
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		updateSearch();
	}

	class BoobsSearchLoader extends AsyncTaskLoader<List<Boobs>> {

		public BoobsSearchLoader(Context context) {
			super(context);
		}

		@Override
		public List<Boobs> loadInBackground() {
			// TODO Auto-generated method stub
			try {
				return NetworkUtils.downloadSearchModelResult(searchText);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}

	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the primary sections of the app.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
			if (searchModel) {
				modelBoobs = new BoobsListFragment();
				modelBoobs.SENDER_TYPE = 43;
			}

			if (searchAuthor) {
				authorBoobs = new BoobsListFragment();
			}
		}

		public void setAuthorBoobs(SearchProvider provider) {
			authorBoobs.setInitBoobsProvider(provider);
		}

		public void setModelBoobs(SearchProvider provider) {
			modelBoobs.setInitBoobsProvider(provider);
		}

		BoobsListFragment modelBoobs;
		BoobsListFragment authorBoobs;

		@Override
		public Fragment getItem(int i) {
			return (searchModel && (i == 0)) ? modelBoobs : authorBoobs;
		}

		@Override
		public int getCount() {
			return searchModel && searchAuthor ? 2 : 1;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return getString(R.string.model).toUpperCase();
			case 1:
				return getString(R.string.author).toUpperCase();
			}
			return null;
		}
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleErrorWhileLoadingProvider() {
		// TODO Auto-generated method stub

	}

}
