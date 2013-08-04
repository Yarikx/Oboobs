package com.bytopia.oboobs.model;

import android.content.res.Resources;

import com.bytopia.oboobs.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yarik on 8/4/13.
 */
public class Repo {

    private final String keyName;
    private final String name;

    private final String apiUrl;
    private final String mediaUrl;

    public Repo(String keyName, String name, Resources res) {
        this.keyName = keyName;
        this.name = name;

        this.apiUrl = String.format(res.getString(R.string.api_url), keyName);
        this.mediaUrl = String.format(res.getString(R.string.media_url), keyName);
    }

    public String getKeyName() {
        return keyName;
    }

    public String getName() {
        return name;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    //TODO use now for showing in adapter, replace later to proper render
    @Override
    public String toString() {
        return this.name;
    }

    private static Repo createRepo(int keyId, int  nameId, Resources res){
        String mainPart = res.getString(keyId);
        String name = res.getString(nameId);
        return new Repo(mainPart, name, res);
    }

    public static List<Repo> getAllRepos(Resources res) {
        ArrayList<Repo> repos = new ArrayList<Repo>();
        repos.add(createRepo(R.string.oboobs_key_name, R.string.Boobs, res));
        repos.add(createRepo(R.string.obutts_key_name, R.string.Butts, res));
        return repos;
    }
}
