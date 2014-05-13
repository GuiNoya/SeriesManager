package com.seriesmanager.app.parsers.trakt;

import android.os.AsyncTask;

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

public class TrendingShowsParser extends AsyncTask<String, Integer, List<ShowSummary>> {

    List<ShowSummary> shows = new ArrayList<ShowSummary>();

    public TrendingShowsParser() {
        this.execute();
    }

    @Override
    protected List<ShowSummary> doInBackground(String... strings) {
        try {
            String url = Constants.TRAKT_DOMAIN_API + "shows/trending.json/" + Constants.TRAKT_API_KEY;
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
            while (!jsonArray.isNull(i) && i < 20) {
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
            return null;
        }
    }
}
