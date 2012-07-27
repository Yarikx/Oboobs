package com;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.bytopia.oboobs.OboobsApp;
import com.bytopia.oboobs.R;

public class BaseActivity extends SherlockFragmentActivity {

	protected ActionBar bar;
	protected OboobsApp app;
	protected FragmentManager fragmentManager;

	private InputMethodManager imm;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		app = (OboobsApp) getApplication();

		setTheme(R.style.Theme_Sherlock); // Used for theme switching in samples

		fragmentManager = getSupportFragmentManager();

		bar = getSupportActionBar();

		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	}

	protected void hideKeyboard(TextView textView) {
		imm.hideSoftInputFromWindow(textView.getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

}
