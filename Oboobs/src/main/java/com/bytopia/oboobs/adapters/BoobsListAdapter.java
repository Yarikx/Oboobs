package com.bytopia.oboobs.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bytopia.oboobs.OboobsApp;
import com.bytopia.oboobs.R;
import com.bytopia.oboobs.model.Boobs;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

public class BoobsListAdapter extends ArrayAdapter<Boobs> {

    @Inject
    Picasso picasso;

    LayoutInflater inflater;
    String mediaUrl;

    public BoobsListAdapter(Context context, List<Boobs> objects,
                            String mediaUrl) {
        super(context, android.R.layout.simple_list_item_1, objects);
        OboobsApp.instance.inject(this);
        this.mediaUrl = mediaUrl;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

        picasso.load(
//                item.getPreviewUrl(mediaUrl)
                "http://lorempixel.com/640/800/"
        ).into(holder.imageView);
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

}
