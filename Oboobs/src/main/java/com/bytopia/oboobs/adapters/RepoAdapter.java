package com.bytopia.oboobs.adapters;

import android.R;
import android.content.Context;
import android.widget.ArrayAdapter;

import com.bytopia.oboobs.model.Repo;

import java.util.List;

/**
 * Created by yarik on 8/4/13.
 */
public class RepoAdapter extends ArrayAdapter<Repo> {
    public static RepoAdapter getDefaultRepoAdapter(Context context){
        List<Repo> repos = Repo.getAllRepos(context.getResources());
        return new RepoAdapter(context, repos);
    }

    public RepoAdapter(Context context, List<Repo> repos){
        super(context, R.layout.simple_list_item_1, repos);
    }
}
