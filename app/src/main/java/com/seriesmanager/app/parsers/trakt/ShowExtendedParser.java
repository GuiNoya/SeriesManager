package com.seriesmanager.app.parsers.trakt;

import android.os.AsyncTask;
import android.os.Build;

import com.seriesmanager.app.Constants;
import com.seriesmanager.app.entities.Episode;
import com.seriesmanager.app.entities.Season;
import com.seriesmanager.app.entities.Show;
import com.seriesmanager.app.network.NetworkGet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowExtendedParser extends AsyncTask<String, Integer, Show> {

    private String entry;
    private Show show = new Show();

    public ShowExtendedParser(String entry) {
        this.entry = entry;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            this.execute();
    }

    public ShowExtendedParser(int entry) {
        this.entry = Integer.toString(entry);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            this.execute();
    }

    @Override
    protected Show doInBackground(String... strings) {
        try {
            String url = Constants.TRAKT_DOMAIN_API + "show/summary.json/" + Constants.TRAKT_API_KEY + "/" + entry + "/extended";
            InputStream is = new NetworkGet(url).get();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            JSONObject jsonObj = new JSONObject(reader.readLine());
            show.setId(jsonObj.getInt("tvdb_id"));
            show.setName(jsonObj.getString("title"));
            show.setSummary(jsonObj.getString("overview"));
            show.setYear(jsonObj.getInt("year"));
            show.setNetwork(jsonObj.getString("network"));
            show.setLastUpdated(new Date().getTime());
            show.setCountry(jsonObj.getString("country"));
            show.setRuntime(jsonObj.getInt("runtime"));
            show.setStatus(jsonObj.getString("status"));
            show.setAirDay(jsonObj.getString("air_day"));
            show.setAirTime(new SimpleDateFormat("h:mma").parse(jsonObj.getString("air_time")).getTime());
            String posterUrl = jsonObj.getString("poster");
            posterUrl = posterUrl.substring(0, posterUrl.length() - 4) + "-138" + posterUrl.substring(posterUrl.length() - 4);
            show.setCover(posterUrl);
            List<String> genres = new ArrayList<String>();
            show.setGenres(genres);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return show;
    }
}
