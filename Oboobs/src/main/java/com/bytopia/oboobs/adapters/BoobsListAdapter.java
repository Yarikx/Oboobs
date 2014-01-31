package com.bytopia.oboobs.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bytopia.oboobs.OboobsApp;
import com.bytopia.oboobs.R;
import com.bytopia.oboobs.model.Boobs;
import com.bytopia.oboobs.utils.CacheHolder;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BoobsListAdapter extends ArrayAdapter<Boobs> {

    LayoutInflater inflater;
    CacheHolder cacheHolder;
    OboobsApp app;
    String mediaUrl;

    public BoobsListAdapter(Context context, List<Boobs> objects,
                            String mediaUrl) {
        super(context, android.R.layout.simple_list_item_1, objects);
        this.mediaUrl = mediaUrl;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        app = OboobsApp.instance;
        cacheHolder = app.getCacheHolder();
        setNotifyOnChange(true);
    }

    static class BoobsViewHolder {
        ImageView imageView;
        ProgressBar bar;
        TextView modelName;
        TextView rank;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        BoobsViewHolder holder;
        View view = convertView;
        if (convertView != null) {
            holder = (BoobsViewHolder) convertView.getTag();
        } else {
            view = inflater.inflate(R.layout.boobs_item, parent, false);
            holder = new BoobsViewHolder();
            holder.imageView = (ImageView) view.findViewById(R.id.image);
            holder.bar = (ProgressBar) view.findViewById(R.id.bar);
            holder.modelName = (TextView) view.findViewById(R.id.modelName);
            holder.rank = (TextView) view.findViewById(R.id.rank);
            view.setTag(holder);
        }

        Boobs item = getItem(position);

        holder.modelName.setText(item.model);
        holder.rank.setText("" + item.rank);

        Picasso.with(app).load(item.getPreviewUrl(mediaUrl)).into(holder.imageView);
        holder.imageView.setVisibility(View.VISIBLE);
        holder.modelName.setVisibility(View.VISIBLE);
        holder.rank.setVisibility(View.VISIBLE);
        holder.bar.setVisibility(View.GONE);

        return view;
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).id;
    }

    public void update() {
        notifyDataSetChanged();
    }

    public void updateViews(int imageId, Bitmap bitmap, AbsListView list) {

        int first = list.getFirstVisiblePosition();
        int count = list.getChildCount();
        for (int i = 0; i < count; i++) {
            View v = list.getChildAt(i);
            BoobsViewHolder holder = (BoobsViewHolder) v.getTag();
            if (holder != null && getItem(first + i).id == imageId) {
                holder.imageView.setVisibility(View.VISIBLE);
                holder.modelName.setVisibility(View.VISIBLE);
                holder.rank.setVisibility(View.VISIBLE);

                holder.imageView.setImageBitmap(bitmap);
                holder.bar.setVisibility(View.GONE);
            }
        }

    }

}
