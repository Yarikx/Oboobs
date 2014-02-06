package com.bytopia.oboobs.mindstorm;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.bytopia.oboobs.adapters.BoobsListAdapter;
import com.bytopia.oboobs.rest.ServerModule;
import com.squareup.picasso.Picasso;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yarik on 2/5/14.
 */
@Module(
        injects = {
                BoobsListAdapter.class,
                BoobsPagesFragment.class,
                ServerModule.class,
                FavoritesItemProvider.class
        }
)
public class OboobsModule {
    private final Context context;

    public OboobsModule(Context context) {
        this.context = context;
    }

    @Provides
    public Context provideContext() {
        return context;
    }

    @Provides
    @Singleton
    public Picasso providePicasso() {
        return Picasso.with(context);
    }

    @Provides @Singleton
    public SharedPreferences provideSharedPreferences(){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

}
