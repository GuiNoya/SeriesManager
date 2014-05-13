package com.seriesmanager.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.seriesmanager.app.R;
import com.seriesmanager.app.entities.Show;

import java.util.List;

public class ShowListAdapter extends ArrayAdapter<Show> {
    private final Context context;
    private final List<Show> values;

    public ShowListAdapter(Context context, List<Show> values) {
        super(context, R.layout.fragment_show_list, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.fragment_show_list, parent, false);
        final Show sh = values.get(position);
        ((TextView) rowView.findViewById(R.id.text_name_show)).setText(sh.getName());
        ((ImageView) rowView.findViewById(R.id.image_cover)).setImageResource(R.drawable.ic_launcher);
        if (sh.isFavorite()) {
            ((ImageView) rowView.findViewById(R.id.image_favorite)).setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            ((ImageView) rowView.findViewById(R.id.image_favorite)).setImageResource(android.R.drawable.btn_star_big_off);
        }
        (rowView.findViewById(R.id.image_favorite)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sh.isFavorite() == true) {
                    sh.setFavorite(false);
                    ((ImageView) view).setImageResource(android.R.drawable.btn_star_big_off);
                } else {
                    sh.setFavorite(true);
                    ((ImageView) view).setImageResource(android.R.drawable.btn_star_big_on);
                }
            }
        });

        return rowView;
    }
}
