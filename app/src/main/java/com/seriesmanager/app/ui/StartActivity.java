package com.seriesmanager.app.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.seriesmanager.app.Comm;
import com.seriesmanager.app.R;
import com.seriesmanager.app.database.DBHelper;
import com.seriesmanager.app.entities.Show;
import com.seriesmanager.app.entities.ShowSummary;
import com.seriesmanager.app.interfaces.OnShowListInteractionListener;
import com.seriesmanager.app.loaders.TrendingsLoader;
import com.seriesmanager.app.notifications.Notification;
import com.seriesmanager.app.parsers.trakt.ShowExtendedParser;
import com.seriesmanager.app.ui.dialogs.ShowSummaryDialog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class StartActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<List<ShowSummary>> {

    private final int LOADER_ID = 0;

    private ListView list;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        list = (ListView) findViewById(R.id.list_start);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_start);
        progressBar.setVisibility(View.VISIBLE);

        list.setAdapter(new StartAdapter(this, new ArrayList<ShowSummary>()));
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);

    }

    @Override
    public Loader<List<ShowSummary>> onCreateLoader(int id, Bundle args) {
        return new TrendingsLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<ShowSummary>> loader, List<ShowSummary> data) {
        if (data != null) {
            list.setAdapter(new StartAdapter(this, data));
            ((StartAdapter) list.getAdapter()).addAll(data);
            progressBar.setVisibility(View.GONE);
            ((StartAdapter) list.getAdapter()).notifyDataSetChanged();
            if (data.size() == 0) {
                TextView t = (TextView) findViewById(android.R.id.empty);
                t.setText("No trending shows\nThat's really weird.");
                t.setVisibility(View.VISIBLE);
            }
        } else {
            TextView t = (TextView) findViewById(android.R.id.empty);
            t.setText("Problem loading data!\nVerify your network.");
            t.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        list.setAdapter(null);
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
            ((ImageView) rowView.findViewById(R.id.image_cover)).setImageBitmap(sh.getCover());
            ((ImageView) rowView.findViewById(R.id.image_cover)).setImageResource(R.drawable.ic_launcher);
            ((TextView) rowView.findViewById(R.id.text_start_summary)).setText(sh.getSummary());
            final ImageView img = ((ImageView) rowView.findViewById(R.id.image_add));
            img.setImageResource(R.drawable.ic_plus);
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new ShowAdder().execute(sh.getId());
                    img.setImageResource(R.drawable.ic_correct);
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

        private class ShowAdder extends AsyncTask<Integer, Void, Void> {

            @Override
            protected Void doInBackground(Integer... integers) {
                final Show show;
                try {
                    show = new ShowExtendedParser(integers[0]).get();
                    new DBHelper(context, null).persistCompleteShow(show);
                    //TODO: get real time of the episode and the seasonEpisode string
                    Date date = new Date(new Date().getTime() + 10000);
                    String seasonEpisode = new DBHelper(context, null).getNextShowEpisode(show.getId()).toString();
                    Notification.newNotification(context, show.getId(), show.getName(), seasonEpisode, date);
                    ((OnShowListInteractionListener) Comm.mainContext).onShowListInteraction();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), show.getName() + " adicionada", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Couldn't add the show", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                return null;
            }
        }
    }
}
