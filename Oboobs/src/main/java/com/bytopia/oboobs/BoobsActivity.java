package com.bytopia.oboobs;

import android.graphics.Bitmap;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.NfcManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.BaseActivity;
import com.bytopia.oboobs.fragments.ActionBarHandler;
import com.bytopia.oboobs.fragments.BoobsFragment;
import com.bytopia.oboobs.model.Boobs;
import com.bytopia.oboobs.providers.ImageProvider;
import com.bytopia.oboobs.utils.RequestBuilder;
import com.bytopia.oboobs.utils.Utils;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class BoobsActivity extends BaseActivity implements BoobsFragmentHolder, ActionBarHandler {

	public static final String BOOBS = "boobs";
	public static final String BOOBS_LIST = "boobs_list";
	public static final String BOOBS_PROVIDER = "boobs_provider";
	public static final String ITEM = "item";
	public static final String OFFSET = "offset";
	private static final int LOAD_BOOBS_NUMBER = 3;
	private static final String STATE_TAG = "state_tag";

	boolean hasModelName = false;
	boolean hasAuthor = false;

	AtomicBoolean isFavoriteBusy = new AtomicBoolean(false);
	AtomicBoolean isDownloadBusy = new AtomicBoolean(false);

	boolean isInFavorites = false;
	boolean hasImage = false;

	private ViewPager pager;

	MySwypeAdapter adapter;

	LocalStateFragment sf;
    private NfcAdapter nfc;

    @SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle arg0) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(arg0);

		setContentView(R.layout.boobs_activity_layout);

		pager = (ViewPager) findViewById(R.id.pager);

		setProgressBarIndeterminateVisibility(false);

		sf = (LocalStateFragment) fragmentManager.findFragmentByTag(STATE_TAG);
		if (sf == null) {
			sf = new LocalStateFragment();
			fragmentManager.beginTransaction().add(sf, STATE_TAG).commit();
			Bundle extra = getIntent().getExtras();
			sf.boobsList = (List<Boobs>) extra.getSerializable(BOOBS_LIST);
			sf.position = extra.getInt(ITEM);
			sf.provider = (ImageProvider) extra.get(BOOBS_PROVIDER);
			sf.offset = extra.getInt(OFFSET);
		}

		adapter = new MySwypeAdapter(fragmentManager);
		pager.setAdapter(adapter);
		pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				super.onPageSelected(position);
				pageChanged(position);
			}
		});
		pager.setCurrentItem(sf.position, false);

		updateDetails(sf.boobsList.get(sf.position));

		bar.setDisplayHomeAsUpEnabled(true);

        if(Build.VERSION.SDK_INT > 9){
            NfcManager nfcManager = (NfcManager) this.getSystemService(NFC_SERVICE);
            nfc = nfcManager.getDefaultAdapter();
            if(nfc != null){
                nfc.setNdefPushMessageCallback(new NfcAdapter.CreateNdefMessageCallback() {
                    @Override
                    public NdefMessage createNdefMessage(NfcEvent event) {
                        BoobsFragment current = (BoobsFragment) adapter
                                .instantiateItem(pager, pager.getCurrentItem());
                        int id = current.getLastSetedBoobs().id;
                        String url = RequestBuilder.boobsPart.contains("boob")?
                                "http://oboobs.ru/b/":
                                "http://obutts.ru/b/";


                        NdefMessage msg = new NdefMessage(
                                new NdefRecord[] {NdefRecord.createUri(url+id)});
                        return msg;
                    }
                }, this);
            }
        }

	}

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    protected void pageChanged(int localPosition) {

		sf.position = localPosition;

		Boobs currentBoob = sf.boobsList.get(sf.position);
		updateDetails(currentBoob);

		if (sf.provider.isInfinitive()
				&& sf.boobsList.size() - 1 - sf.position < LOAD_BOOBS_NUMBER
				&& !isDownloadBusy.get()) {
			loadMoreBoobs();
		}
	}

	private void loadMoreBoobs() {

		isDownloadBusy.set(true);

//		Log.d("load", "start loading boobs");
//		new AsyncTask<Void, Void, List<Boobs>>() {
//
//			@Override
//			protected List<Boobs> doInBackground(Void... params) {
//				sf.offset += Utils.getBoobsChunk();
//				try {
//					return sf.provider.getBoobs(sf.offset);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				return null;
//			}
//
//			protected void onPostExecute(List<Boobs> result) {
//				Log.d("load", "boobs loaded");
//				if (result != null) {
//					sf.boobsList.addAll(result);
//					adapter.update();
//					isDownloadBusy.set(false);
//				}
//			}
//
//		}.execute();
	}

	private void updateDetails(Boobs boobs) {
		hasModelName = boobs.model != null && !boobs.model.trim().equals("");
		hasAuthor = (boobs.author != null && !boobs.author.trim().equals(""));

		bar.setTitle(hasModelName ? boobs.model : null);
		bar.setSubtitle(hasAuthor ? boobs.author : null);

		isInFavorites = boobs.hasFavoritedFile();

		supportInvalidateOptionsMenu();
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
					addToFavorites(sf.boobsList.get(sf.position));
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

			Bitmap cBitmap;

			protected void onPreExecute() {
				BoobsFragment current = (BoobsFragment) adapter
						.instantiateItem(pager, pager.getCurrentItem());
				cBitmap = current.getCurrentBitmap();
			};

			@Override
			protected Boolean doInBackground(Void... params) {
				isFavoriteBusy.set(true);
				if (cBitmap != null) {
					return Utils.saveFavorite(boobs, cBitmap);
				}
				return null;
			}

			protected void onPostExecute(Boolean result) {
				if (result != null && result) {
					isInFavorites = true;
					supportInvalidateOptionsMenu();
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
				return Utils.removeFavorite(sf.boobsList.get(sf.position));
			}

			protected void onPostExecute(Boolean result) {
				if (result != null && result) {
					isInFavorites = false;
					supportInvalidateOptionsMenu();
				}
				isFavoriteBusy.set(false);
			};

		}.execute();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.boobs_menu, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		MenuItem favorites = menu.findItem(R.id.favorite);

		if (isInFavorites) {
			favorites.setIcon(R.drawable.star_on);
			favorites.setTitle(R.string.remove_from_favorites);
		} else {
			favorites.setIcon(R.drawable.star_off);
			favorites.setTitle(R.string.add_to_favorites);
		}

		favorites.setVisible(hasImage);

		return true;
	}

	@Override
	public void imageReceived(int position, Bitmap bitmap) {
		hasImage = true;
		supportInvalidateOptionsMenu();
	}

    @Override
    public void hide() {
        bar.hide();
    }

    @Override
    public void show() {
        bar.show();
    }

    class MySwypeAdapter extends FragmentStatePagerAdapter {

		public MySwypeAdapter(FragmentManager fm) {
			super(fm);
		}

		public void update() {
			notifyDataSetChanged();
		}

		@Override
		public Fragment getItem(int pos) {
			BoobsFragment boobsFragment = new BoobsFragment();
            boobsFragment.setActionBarHandler(BoobsActivity.this);
			boobsFragment.SENDER_TYPE = pos;
			Bundle args = new Bundle();
			args.putSerializable(BoobsFragment.INIT_BOOBS,
					sf.boobsList.get(pos));
			boobsFragment.setArguments(args);

			return boobsFragment;
		}

		@Override
		public int getCount() {
			return sf.boobsList.size();
		}

	}

	@Override
	public boolean isFullScreen() {
		return sf.fs;
	}

	@Override
	public void setFullScreen(boolean fs) {
		sf.fs = fs;
	}

	public static class LocalStateFragment extends Fragment {
		public List<Boobs> boobsList;
		public ImageProvider provider;
		public int position;
		public int offset;

		public boolean fs = false;

		@SuppressWarnings("unchecked")
		@Override
		public void onCreate(Bundle b) {
			super.onCreate(b);
			setRetainInstance(true);
			try {
				boobsList = (List<Boobs>) b.get(BOOBS_LIST);
				provider = (ImageProvider) b.get(BOOBS_PROVIDER);
				position = b.getInt(ITEM);
				offset = b.getInt(OFFSET);
			} catch (NullPointerException e) {
			}
		}

		@Override
		public void onSaveInstanceState(Bundle outState) {
			super.onSaveInstanceState(outState);
			outState.putSerializable(BOOBS_LIST, (Serializable) boobsList);
			outState.putSerializable(BOOBS_PROVIDER, (Serializable) provider);
			outState.putInt(ITEM, position);
			outState.putInt(OFFSET, offset);
		}

	}



}
