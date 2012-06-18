package com.bytopia.oboobs;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.bytopia.oboobs.fragments.BoobsFragment;
import com.bytopia.oboobs.model.Boobs;

public class BoobsActivity extends SherlockFragmentActivity {

	private static final String BOOBS_FRAGMENT_TAG = "BoobsFragment";
	public static final String BOOBS = "boobs";
	BoobsFragment boobsFragment;
	FragmentManager fragmentManager;
	ActionBar actionBar;

	Boobs boobs;

	boolean hasModelName = false;
	boolean hasAuthor = false;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		setTheme(R.style.Theme_Sherlock);

		setContentView(R.layout.boobs_activity_layout);
		
		actionBar = getSupportActionBar();
		fragmentManager = getSupportFragmentManager();
		
		boobsFragment = (BoobsFragment) fragmentManager
				.findFragmentByTag(BOOBS_FRAGMENT_TAG);

		boobs = (Boobs) getIntent().getExtras().getSerializable(BOOBS);
		boobsFragment.setBoobs(boobs);

		hasModelName = boobs.model != null && !boobs.model.equals("");
		hasAuthor = boobs.author != null && !boobs.author.equals("");

		actionBar.setTitle(boobs.model);
		actionBar.setSubtitle(boobs.author);
		
		actionBar.setDisplayHomeAsUpEnabled(true);

	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;

		default:
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}

}
