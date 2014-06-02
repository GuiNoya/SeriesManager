package com.seriesmanager.app.parsers.trakt;

import android.os.AsyncTask;
import android.os.Build;

import com.seriesmanager.app.Comm;
import com.seriesmanager.app.Constants;
import com.seriesmanager.app.database.DBHelper;
import com.seriesmanager.app.entities.Episode;
import com.seriesmanager.app.entities.Season;
import com.seriesmanager.app.entities.Show;
import com.seriesmanager.app.network.NetworkGet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowUpdateParser extends AsyncTask<Long, Integer, Show> {

    private String entry;
    private Show show = new Show();
    private long lastUpdated;

    public ShowUpdateParser(String entry, long lastUpdated) {
        this.entry = entry;
        this.lastUpdated = lastUpdated;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            this.execute();
    }

    public ShowUpdateParser(long entry, long lastUpdated) {
        this.entry = Long.toString(entry);
        this.lastUpdated = lastUpdated;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            this.execute();
    }

    @Override
    protected Show doInBackground(Long... longs) {
        try {
            String url = Constants.TRAKT_DOMAIN_API + "show/summary.json/" + Constants.TRAKT_API_KEY + "/" + entry + "/extended";
            InputStream is = new NetworkGet(url).get();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            JSONObject jsonObj = new JSONObject(reader.readLine());
            show.setLastUpdated(new Date().getTime());
            if (lastUpdated < show.getLastUpdated()) {
                show.setId(jsonObj.getInt("tvdb_id"));
                show.setName(jsonObj.getString("title"));
                show.setSummary(jsonObj.getString("overview"));
                show.setFirstAired(new Date(jsonObj.getLong("first_aired") * 1000));
                show.setNetwork(jsonObj.getString("network"));
                List<String> genres = new ArrayList<String>();
                JSONArray jsonArr = jsonObj.getJSONArray("genres");
                for (int i = 0; i < jsonArr.length(); i++) {
                    genres.add(jsonArr.getString(i));
                }
                Map<Integer, Season> seasons = new HashMap<Integer, Season>();
                show.setSeasons(seasons);
                jsonArr = jsonObj.getJSONArray("seasons");
                for (int i = 0; i < jsonArr.length(); i++) {
                    Season season = new Season();
                    JSONObject jsonSeason = jsonArr.getJSONObject(i);
                    season.setSeasonNumber(jsonSeason.getInt("season"));
                    season.setId(new DBHelper(Comm.mainContext, null).getSeasonId(show.getId(), season.getSeasonNumber()));
                    seasons.put(season.getSeasonNumber(), season);
                    season.setShow(show);
                    Map<Integer, Episode> episodes = new HashMap<Integer, Episode>();
                    season.setEpisodes(episodes);
                    JSONArray jsonEps = jsonSeason.getJSONArray("episodes");
                    for (int j = 0; j < jsonEps.length(); j++) {
                        Episode ep = new Episode();
                        JSONObject jsonEp = jsonEps.getJSONObject(j);
                        ep.setId(jsonEp.getInt("tvdb_id"));
                        ep.setShow(show);
                        ep.setSeason(season);
                        ep.setEpisodeNumber(jsonEp.getInt("number"));
                        ep.setName(jsonEp.getString("title"));
                        ep.setSummary(jsonEp.getString("overview"));
                        try {
                            ep.setAirDate(new Date(jsonEp.getLong("first_aired") * 1000));
                        } catch (Exception e) {
                            ep.setAirDate(null);
                            e.printStackTrace();
                        }
                        episodes.put(ep.getEpisodeNumber(), ep);
                    }
                }
                return show;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
