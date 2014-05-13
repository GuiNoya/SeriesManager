package com.seriesmanager.app.parsers.trakt;

import android.os.AsyncTask;

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
        this.execute();
    }

    public ShowExtendedParser(int entry) {
        this.entry = Integer.toString(entry);
        this.execute();
    }

    @Override
    protected Show doInBackground(String... strings) {
        try {
            String url = Constants.TRAKT_DOMAIN_API + "show/summary.json/" + Constants.TRAKT_API_KEY + "/" + entry + "/extended";
            InputStream is = new NetworkGet(url).get();
            //StringBuilder json = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            /*String line = reader.readLine();
            while (line != null){
                json.append(line);
                line = reader.readLine();
            }*/
            JSONObject jsonObj = new JSONObject(reader.readLine());
            show.setId(jsonObj.getInt("tvdb_id"));
            show.setName(jsonObj.getString("title"));
            show.setSummary(jsonObj.getString("overview"));
            show.setFirstAired(new Date(jsonObj.getLong("first_aired")));
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
                    ep.setAirDate(new Date(jsonEp.getLong("first_aired")));
                    episodes.put(ep.getEpisodeNumber(), ep);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return show;
    }
}
