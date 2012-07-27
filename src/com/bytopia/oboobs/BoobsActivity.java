package com.bytopia.oboobs;

import java.util.concurrent.atomic.AtomicBoolean;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.Window;

import com.BaseActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.bytopia.oboobs.fragments.BoobsFragment;
import com.bytopia.oboobs.model.Boobs;
import com.bytopia.oboobs.utils.Utils;

public class BoobsActivity extends BaseActivity implements
		BoobsFragmentHolder {

	private static final String BOOBS_FRAGMENT_TAG = "BoobsFragment";
	public static final String BOOBS = "boobs";
	BoobsFragment boobsFragment;
	FragmentManager fragmentManager;

	Bitmap imageBitmap;

	Boobs boobs;

	boolean hasModelName = false;
	boolean hasAuthor = false;

	AtomicBoolean isFavoriteBusy = new AtomicBoolean(false);

	boolean isInFavorites = false;
	boolean hasImage = false;

	@Override
	protected void onCreate(Bundle arg0) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(arg0);

		setContentView(R.layout.boobs_activity_layout);

		setProgressBarIndeterminateVisibility(false);

		fragmentManager = getSupportFragmentManager();

		boobsFragment = (BoobsFragment) fragmentManager
				.findFragmentByTag(BOOBS_FRAGMENT_TAG);

		boobs = (Boobs) getIntent().getExtras().getSerializable(BOOBS);

		isInFavorites = boobs.hasFavoritedFile();

		boobsFragment.setBoobs(boobs);

		hasModelName = boobs.model != null && !boobs.model.equals("");
		hasAuthor = boobs.author != null && !boobs.author.equals("");

		bar.setTitle(boobs.model);
		bar.setSubtitle(boobs.author);

		bar.setDisplayHomeAsUpEnabled(true);

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
					addToFavorites();
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

	private void addToFavorites() {

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
				return Utils.removeFavorite(boobs);
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

}
