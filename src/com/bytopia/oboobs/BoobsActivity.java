package com.bytopia.oboobs;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Window;

import com.BaseActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.bytopia.oboobs.fragments.BoobsFragment;
import com.bytopia.oboobs.model.Boobs;
import com.bytopia.oboobs.utils.Utils;

public class BoobsActivity extends BaseActivity implements BoobsFragmentHolder {

	public static final String BOOBS = "boobs";
	public static final String BOOBS_LIST = "boobs_list";
	public static final String ITEM = "item";
	BoobsFragment boobsFragment;

	Bitmap imageBitmap;

	boolean hasModelName = false;
	boolean hasAuthor = false;

	AtomicBoolean isFavoriteBusy = new AtomicBoolean(false);

	boolean isInFavorites = false;
	boolean hasImage = false;

	private ViewPager pager;
	private List<Boobs> boobsList;
	private int position;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle arg0) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(arg0);

		setContentView(R.layout.boobs_activity_layout);

		pager = (ViewPager) findViewById(R.id.pager);

		setProgressBarIndeterminateVisibility(false);

		// boobsFragment = (BoobsFragment) fragmentManager
		// .findFragmentByTag(BOOBS_FRAGMENT_TAG);

		{
			Bundle extra = getIntent().getExtras();
			boobsList = (List<Boobs>) extra.getSerializable(BOOBS_LIST);
			position = extra.getInt(ITEM);
		}

		pager.setAdapter(new MySwypeAdapter(fragmentManager));
		pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				super.onPageSelected(position);
				pageChanged(position);
			}
		});
		pager.setCurrentItem(position, false);

		updateDetails(boobsList.get(position));

		bar.setDisplayHomeAsUpEnabled(true);

	}

	protected void pageChanged(int localPosition) {
		position = localPosition;

		Boobs currentBoob = boobsList.get(position);
		updateDetails(currentBoob);

	}

	private void updateDetails(Boobs boobs) {
		hasModelName = boobs.model != null && !boobs.model.trim().equals("");
		hasAuthor = (boobs.author != null && !boobs.author.trim().equals(""));

		bar.setTitle(hasModelName ? boobs.model : null);
		bar.setSubtitle(hasAuthor ? boobs.author : null);

		isInFavorites = boobs.hasFavoritedFile();

		invalidateOptionsMenu();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;

		case R.id.favorite:

			if (!isFavoriteBusy.get()) {
				if (!isInFavorites) {
					addToFavorites(boobsList.get(position));
				} else {
					removeFromFavorites();
				}
			}

			return true;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	private void addToFavorites(final Boobs boobs) {

		new AsyncTask<Void, String, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				isFavoriteBusy.set(true);
				if (imageBitmap != null) {
					return Utils.saveFavorite(boobs, imageBitmap);
				}
				return null;
			}

			protected void onPostExecute(Boolean result) {
				if (result != null && result) {
					isInFavorites = true;
					invalidateOptionsMenu();
				}
				isFavoriteBusy.set(false);
			};

		}.execute();

	}

	private void removeFromFavorites() {

		new AsyncTask<Void, String, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				isFavoriteBusy.set(true);
				return Utils.removeFavorite(boobsList.get(position));
			}

			protected void onPostExecute(Boolean result) {
				if (result != null && result) {
					isInFavorites = false;
					invalidateOptionsMenu();
				}
				isFavoriteBusy.set(false);
			};

		}.execute();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.boobs_menu, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		MenuItem favorites = menu.findItem(R.id.favorite);

		if (isInFavorites) {
			favorites.setIcon(R.drawable.star_on);
			favorites.setTitle(R.string.add_to_favorites);
		} else {
			favorites.setIcon(R.drawable.star_off);
			favorites.setTitle(R.string.remove_from_favorites);
		}

		favorites.setVisible(hasImage);

		return true;
	}

	@Override
	public void imageReceived(Bitmap bitmap) {
		hasImage = true;
		imageBitmap = bitmap;
		invalidateOptionsMenu();
	}

	class MySwypeAdapter extends FragmentStatePagerAdapter {

		public MySwypeAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int pos) {
			BoobsFragment boobsFragment = new BoobsFragment();
			boobsFragment.SENDER_TYPE = pos;
			Bundle args = new Bundle();
			args.putSerializable(BoobsFragment.INIT_BOOBS, boobsList.get(pos));
			boobsFragment.setArguments(args);

			return boobsFragment;
		}

		@Override
		public int getCount() {
			return boobsList.size();
		}

	}

}
