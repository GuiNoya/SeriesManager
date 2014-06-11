package com.seriesmanager.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.seriesmanager.app.R;
import com.seriesmanager.app.entities.ShowLite;

import java.util.List;

public class OverdueAdapter extends ArrayAdapter<ShowLite> {
    private final Context context;
    private final List<ShowLite> values;

    public OverdueAdapter(Context context, List<ShowLite> values) {
        super(context, R.layout.fragment_show_overdue_list, values);
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
        final ShowLite sh = values.get(position);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.fragment_show_overdue_list, parent, false);
        ((TextView) rowView.findViewById(R.id.text_name_show)).setText(sh.getName());
        ((ImageView) rowView.findViewById(R.id.image_cover)).setImageResource(R.drawable.ic_launcher);
        ((TextView) rowView.findViewById(R.id.text_number_overdue)).setText(sh.getNumberOverdue() + " episodes to watch");
        ((ImageView) rowView.findViewById(R.id.image_cover)).setImageBitmap(sh.getCover());

        return rowView;
    }
}
