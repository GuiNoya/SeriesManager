package com.seriesmanager.app;

import android.content.Context;
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
import android.widget.Toast;

import com.seriesmanager.app.entities.ShowSummary;
import com.seriesmanager.app.interfaces.OnShowListInteractionListener;
import com.seriesmanager.app.parsers.trakt.ShowExtendedParser;
import com.seriesmanager.app.parsers.trakt.TrendingShowsParser;
import com.seriesmanager.app.ui.dialogs.ShowSummaryDialog;
import com.seriesmanager.app.ui.dialogs.WarningDialog;

import java.util.ArrayList;
import java.util.List;


public class StartActivity extends ActionBarActivity {

    private ListView list;
    private List<ShowSummary> listToAdd = new ArrayList<ShowSummary>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        list = (ListView) findViewById(R.id.list_start);
        List<ShowSummary> shows = null;
        try {
            shows = new TrendingShowsParser().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        WarningDialog wd = new WarningDialog("Please Wait", "Getting data");
        wd.show(getSupportFragmentManager(), "");
        try {
            list.setAdapter(new StartAdapter(this, shows));
            wd.dismiss();
        } catch (Exception e) {
            shows = new ArrayList<ShowSummary>();
            list.setAdapter(new StartAdapter(this, shows));
            wd.dismiss();
            new ShowSummaryDialog(new ShowSummary(0, "Network Problem", "Please enable the network."))
                    .show(getSupportFragmentManager(), "");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start, menu);
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

    public void onFinishClick(View vi) {
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public class StartAdapter extends ArrayAdapter<ShowSummary> {
        private final Context context;
        private final List<ShowSummary> values;

        public StartAdapter(Context context, List<ShowSummary> values) {
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
            View rowView = inflater.inflate(R.layout.fragment_start_show_list, parent, false);
            final ShowSummary sh = values.get(position);
            ((TextView) rowView.findViewById(R.id.text_start_name)).setText(sh.getName());
            ((ImageView) rowView.findViewById(R.id.image_cover)).setImageResource(R.drawable.ic_launcher);
            ((TextView) rowView.findViewById(R.id.text_start_summary)).setText(sh.getSummary());
            final ImageView img = ((ImageView) rowView.findViewById(R.id.image_add));
            img.setImageResource(R.drawable.ic_plus);
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*listToAdd.add(sh);
                    Show s = new Show();
                    s.setId(sh.getId());
                    s.setName(sh.getName());
                    s.setFirstAired(new Date());
                    s.setSummary(sh.getSummary());
                    s.setNetwork(sh.getNetwork());
                    TestContent.SHOWS.add(s);*/
                    try {
                        //TestContent.SHOWS.add(new ShowExtendedParser(sh.getId()).get());
                        //TestContent.SHOWS.add(new ShowParser(sh.getId()).get());
                        Comm.showsList.add(new ShowExtendedParser(sh.getId()).get());
                        ((OnShowListInteractionListener) Comm.mainContext).onShowListInteraction();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    img.setImageResource(R.drawable.ic_correct);
                    Toast.makeText(context, sh.getName() + " adicionada", Toast.LENGTH_SHORT).show();
                }
            });
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new ShowSummaryDialog(sh).show(getSupportFragmentManager(), "");
                }
            });

            return rowView;
        }
    }
}
