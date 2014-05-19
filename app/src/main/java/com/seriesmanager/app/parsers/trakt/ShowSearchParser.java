package com.seriesmanager.app.parsers.trakt;

import android.os.AsyncTask;
import android.os.Build;

import com.seriesmanager.app.Constants;
import com.seriesmanager.app.entities.ShowSummary;
import com.seriesmanager.app.network.NetworkGet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ShowSearchParser extends AsyncTask<String, Integer, List<ShowSummary>> {

    List<ShowSummary> shows = new ArrayList<ShowSummary>();
    private String query;

    public ShowSearchParser(String entry) {
        this.query = entry.trim().replace(" ", "+");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            this.execute();
    }

    @Override
    protected List<ShowSummary> doInBackground(String... strings) {
        try {
            String url = Constants.TRAKT_DOMAIN_API + "search/shows.json/" + Constants.TRAKT_API_KEY + "?query=" + query + "&limit=15";
            InputStream is = (new NetworkGet(url)).get();
            StringBuilder json = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = reader.readLine();
            while (line != null) {
                json.append(line);
                line = reader.readLine();
            }
            JSONArray jsonArray = new JSONArray(json.toString());
            int i = 0;
            while (!jsonArray.isNull(i)) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                ShowSummary show = new ShowSummary();
                show.setId(jsonObj.getInt("tvdb_id"));
                show.setName(jsonObj.getString("title"));
                show.setSummary(jsonObj.getString("overview"));
                show.setNetwork(jsonObj.getString("network"));
                shows.add(show);
                i++;
            }
            return shows;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
