package com.seriesmanager.app.parsers.trakt;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import com.seriesmanager.app.Constants;
import com.seriesmanager.app.database.DBHelper;
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
    Context context;

    public TrendingShowsParser(Context context) {
        this.context = context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
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
            DBHelper dbHelper = new DBHelper(context, null);
            List<Integer> showsInDB = dbHelper.getShowsIds();
            JSONArray jsonArray = new JSONArray(json.toString());
            int i = 0;
            int j = 0;
            while (!jsonArray.isNull(i) && j < 20) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                int id = jsonObj.getInt("tvdb_id");
                if (showsInDB.contains(id)) {
                    i++;
                    continue;
                }
                ShowSummary show = new ShowSummary();
                show.setId(id);
                show.setName(jsonObj.getString("title"));
                show.setSummary(jsonObj.getString("overview"));
                show.setNetwork(jsonObj.getString("network"));
                String posterUrl = jsonObj.getString("poster");
                posterUrl = posterUrl.substring(0, posterUrl.length() - 4) + "-138" + posterUrl.substring(posterUrl.length() - 4);
                show.setCover(posterUrl);
                shows.add(show);
                j++;
                i++;
            }
            return shows;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
