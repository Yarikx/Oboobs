package com;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.bytopia.oboobs.OboobsApp;
import com.bytopia.oboobs.R;

public class BaseActivity extends SherlockFragmentActivity{
	
	protected ActionBar bar;
	protected OboobsApp app;
	protected FragmentManager fragmentManager;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		
		app = (OboobsApp) getApplication();
		
		setTheme(R.style.Theme_Sherlock); // Used for theme switching in samples
		
		fragmentManager = getSupportFragmentManager();
		
		bar = getSupportActionBar();
	}

}
