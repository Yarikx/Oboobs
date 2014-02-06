package com.bytopia.oboobs.mindstorm;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bytopia.oboobs.OboobsApp;
import com.bytopia.oboobs.R;
import com.bytopia.oboobs.model.Boobs;
import com.bytopia.oboobs.utils.Utils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.util.async.Async;
import rx.util.functions.Func0;

/**
 * Created by yarik on 2/5/14.
 */
public class BoobsPagesFragment extends Fragment implements ViewPager.OnPageChangeListener {

    private final ItemsProvider provider;
    private int current;
    private ArrayList<Boobs> boobsList;

    private ActionBar bar;

    @Inject
    protected Picasso picasso;

    public BoobsPagesFragment(ItemsProvider provider, int current) {
        this.provider = provider;
        this.current = current;
    }

    @Inject
    protected Utils utils;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        OboobsApp.instance.inject(this);

//        if(Build.VERSION.SDK_INT > 9){
//            NfcManager nfcManager = (NfcManager) OboobsApp.instance.getSystemService(Context.NFC_SERVICE);
//            NfcAdapter nfc = nfcManager.getDefaultAdapter();
//            if(nfc != null){
//                nfc.setNdefPushMessageCallback(new NfcAdapter.CreateNdefMessageCallback() {
//                    @Override
//                    public NdefMessage createNdefMessage(NfcEvent event) {
//                        BoobsFragment current = (BoobsFragment) adapter
//                                .instantiateItem(pager, pager.getCurrentItem());
//                        int id = current.getLastSetedBoobs().id;
//                        String url = RequestBuilder.boobsPart.contains("boob")?
//                                "http://oboobs.ru/b/":
//                                "http://obutts.ru/b/";
//
//
//                        NdefMessage msg = new NdefMessage(
//                                new NdefRecord[] {NdefRecord.createUri(url+id)});
//                        return msg;
//                    }
//                }, this);
//            }
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewPager viewPager = new ViewPager(getActivity());
        viewPager.setId(R.id.pager);

        boobsList = new ArrayList<>();
        PagerAdapter adapter = new FragmentStatePagerAdapter(getFragmentManager()) {

            @Override
            public Fragment getItem(int i) {
                return new BoobsImageFragment(boobsList.get(i).getFullImageUrl(provider.getMediaUrl()), picasso);
            }

            @Override
            public int getCount() {
                return boobsList.size();
            }
        };

        Observable<Boobs> boobies = provider.boobs().observeOn(AndroidSchedulers.mainThread());
        boobies.subscribe(boobs -> {
            boobsList.add(boobs);
            adapter.notifyDataSetChanged();
        });
        final int c = current;
        boobies.elementAt(c).subscribe(any -> {
            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem(c, false);
        });

        viewPager.setOnPageChangeListener(this);
        return viewPager;
    }

    private void setUpActionBar() {
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
    public void onPageSelected(int i) {
        Boobs boobs = boobsList.get(i);
        current = i;
        updateDetails(boobs);
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    public class BoobsImageFragment extends Fragment{
        private final String url;
        private Picasso picasso;

        private BoobsImageFragment(String url, Picasso picasso) {
            this.url = url;
            this.picasso = picasso;
            setRetainInstance(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            ImageView imageView = new ImageView(getActivity());
            picasso.with(getActivity()).load(
//                    url
                    "http://lorempixel.com/640/800/"
            )
                    .into(imageView);

            imageView.setOnClickListener(view -> {
                isFullScreen = !isFullScreen;
                updateFullscreen(isFullScreen);
            });
            return imageView;
        }
    }

    private void updateDetails(Boobs boobs) {
        boolean hasModelName = boobs.model != null && !boobs.model.trim().equals("");
        boolean hasAuthor = (boobs.author != null && !boobs.author.trim().equals(""));

        bar.setTitle(hasModelName ? boobs.model : null);
        bar.setSubtitle(hasAuthor ? boobs.author : null);

        isInFavorites = boobs.hasFavoritedFile(utils);

        getActivity().supportInvalidateOptionsMenu();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.boobs_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    boolean isInFavorites = false;
    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        MenuItem favorites = menu.findItem(R.id.favorite);

        if (isInFavorites) {
            favorites.setIcon(R.drawable.star_on);
            favorites.setTitle(R.string.remove_from_favorites);
        } else {
            favorites.setIcon(R.drawable.star_off);
            favorites.setTitle(R.string.add_to_favorites);
        }

        super.onPrepareOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.favorite){
            Boobs boobs = boobsList.get(current);
            Func0<Boolean> f;
            if(boobs.hasFavoritedFile(utils)){
                f = () -> utils.removeFavorite(boobs);
            }else{
                f = () -> {
                    try{
                        return utils.saveFavorite(boobs, picasso.load(boobs.getFullImageUrl(provider.getMediaUrl())).get());
                    }catch (IOException e){
                        throw new RuntimeException(e);
                    }
                };
            }
            Async.start(f)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ok -> {
                        if(boobsList.get(current) == boobs){
                            isInFavorites = boobs.hasFavoritedFile(utils);
                            ((ViewPager) getView().findViewById(R.id.pager)).getAdapter().notifyDataSetChanged();
                            getActivity().supportInvalidateOptionsMenu();
                        }
                    });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        bar = null;
    }


    boolean isFullScreen = false;
    @TargetApi(11)
    private void updateFullscreen(boolean isFullScreen) {
        if (isFullScreen) {
            getActivity().getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            if(bar != null) bar.hide();
            if (Build.VERSION.SDK_INT > 10) {
                getView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
            }
        } else {
            getActivity().getWindow().clearFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            if(bar != null) bar.show();
            if (Build.VERSION.SDK_INT > 10) {
                getView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        updateFullscreen(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        bar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        setUpActionBar();
        updateFullscreen(isFullScreen);
    }
}
