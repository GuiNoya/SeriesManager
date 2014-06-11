package com.seriesmanager.app.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.seriesmanager.app.Comm;
import com.seriesmanager.app.R;
import com.seriesmanager.app.entities.Show;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class SearchActivity extends ActionBarActivity {

    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        list = (ListView) findViewById(R.id.list_search);
        //list.setAdapter(new SearchAdapter(this, TestContent.SEARCH_LIST));
        list.setAdapter(new SearchAdapter(this, new ArrayList<Show>()));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public class SearchAdapter extends ArrayAdapter<Show> {
        private final Context context;
        private final List<Show> values;

        public SearchAdapter(Context context, List<Show> values) {
            super(context, R.layout.fragment_search_show_list, values);
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
            View rowView = inflater.inflate(R.layout.fragment_search_show_list, parent, false);
            final Show sh = values.get(position);
            ((TextView) rowView.findViewById(R.id.text_start_name)).setText(sh.getName());
            ((ImageView) rowView.findViewById(R.id.image_cover)).setImageResource(R.drawable.ic_launcher);
            ((TextView) rowView.findViewById(R.id.text_start_summary)).setText(sh.getSummary());
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Comm.mainContext, ShowActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            });

            return rowView;
        }
    }
}
