package com.bytopia.oboobs;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.bytopia.oboobs.fragments.BoobsListFragment;
import com.bytopia.oboobs.model.Boobs;
import com.bytopia.oboobs.model.Order;
import com.bytopia.oboobs.utils.CacheHolder;
import com.bytopia.oboobs.utils.NetworkUtils;
import com.bytopia.oboobs.utils.Utils;

public class OboobsMaintActivity extends SherlockFragmentActivity implements
		ActionBar.OnNavigationListener {

	private ActionBar bar;
	private OboobsApp app;
	private FragmentManager fragmentManager;

	private BoobsListFragment boobsListFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		app = (OboobsApp) getApplication();

		setTheme(R.style.Theme_Sherlock); // Used for theme switching in samples
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		Context context = getSupportActionBar().getThemedContext();
		ArrayAdapter<CharSequence> list =
		// new ArrayAdapter<CharSequence>(context,
		// R.layout.sherlock_spinner_item, tabs) ;
		ArrayAdapter.createFromResource(context, R.array.by,
				R.layout.sherlock_spinner_item);
		list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

		bar = getSupportActionBar();

		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		bar.setListNavigationCallbacks(list, this);

		fragmentManager = getSupportFragmentManager();

		boobsListFragment = (BoobsListFragment) fragmentManager
				.findFragmentByTag("BoobsList");
		new AsyncTask<Void, Void, List<Boobs>>() {

			@Override
			protected List<Boobs> doInBackground(Void... params) {
				try {
					List<Boobs> boobs = NetworkUtils.downloadBoobsList(0, 20,
							Order.RANK,true);

					return boobs;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}

			protected void onPostExecute(final java.util.List<Boobs> result) {
				if (result != null) {
					boobsListFragment.fill(result);

				}
			};
		}.execute();

	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Used to put dark icons on light action bar
		boolean isDark = app.isDark;

		// menu.add("Save")
		// .setIcon(isLight ? R.drawable.ic_compose_inverse :
		// R.drawable.ic_compose)
		// .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		// menu.add("Search")
		// .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM |
		// MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		//
		// menu.add("Refresh")
		// .setIcon(isLight ? R.drawable.ic_refresh_inverse :
		// R.drawable.ic_refresh)
		// .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM |
		// MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		return true;
	}
}