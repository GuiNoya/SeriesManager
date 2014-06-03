package com.seriesmanager.app.parsers.TheTVDB;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Build;
import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Log;
import android.util.Xml;

import com.seriesmanager.app.Constants;
import com.seriesmanager.app.entities.Episode;
import com.seriesmanager.app.entities.Season;
import com.seriesmanager.app.entities.Show;
import com.seriesmanager.app.network.NetworkGet;

import org.xml.sax.ContentHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated
public class ShowParser extends AsyncTask<String, Integer, Show> {

    private Show show = new Show();
    private String entry;

    public ShowParser(String entry) {
        this.entry = entry;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            this.execute();
    }

    public ShowParser(int entry) {
        this.entry = Integer.toString(entry);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            this.execute();
    }

    @Override
    protected Show doInBackground(String... values) {
        String url = Constants.THETVDB_DOMAIN_API + Constants.THETVDB_API_KEY + "/series/" + entry + "/all/en.xml";
        try {
            ContentHandler handler = newHandler();
            try {
                Xml.parse(new NetworkGet(url).get(), Xml.Encoding.UTF_8, handler);
            } catch (Exception e) {
                Log.e("SeriesManager", "Exception getting XML data - parser", e);
            }
        } catch (Exception e) {
            Log.e("SeriesManager", "Exception getting XML data - handler", e);
        }
        return show;
    }

    private ContentHandler newHandler() {
        RootElement root = new RootElement("Data");
        Element series = root.getChild("Series");
        final Map<Integer, Season> seasons = new HashMap<Integer, Season>();
        show.setSeasons(seasons);
        series.getChild("id").setEndTextElementListener(
                new EndTextElementListener() {
                    public void end(String body) {
                        show.setId(Integer.parseInt(body));
                    }
                }
        );
        series.getChild("SeriesName").setEndTextElementListener(
                new EndTextElementListener() {
                    public void end(String body) {
                        show.setName(body);
                    }
                }
        );
        /*series.getChild("Status").setEndTextElementListener(
                new EndTextElementListener(){
                    public void end(String body) {
                        show.status = body;
                    }
                }
        );*/
        /*series.getChild("IMDB_ID").setEndTextElementListener(
                new EndTextElementListener(){
                    public void end(String body) {
                        show.imdb_id = body;
                    }
                }
        );*/
        series.getChild("Overview").setEndTextElementListener(
                new EndTextElementListener() {
                    public void end(String body) {
                        show.setSummary(body);
                    }
                }
        );
        series.getChild("FirstAired").setEndTextElementListener(
                new EndTextElementListener() {
                    public void end(String body) {
                        //show.setFirstAired(new Date(body));
                        show.setFirstAired(new Date());
                    }
                }
        );
        series.getChild("Network").setEndTextElementListener(
                new EndTextElementListener() {
                    public void end(String body) {
                        show.setNetwork(body);
                    }
                }
        );
        series.getChild("Genre").setEndTextElementListener(
                new EndTextElementListener() {
                    public void end(String body) {
                        String[] genres = body.split("|");
                        List<String> lGenres = new ArrayList<String>();
                        Log.w("Tamanho string separadas genero", Integer.toString(genres.length));
                        //lGenres.addAll(Arrays.asList(genres));
                        show.setGenres(Arrays.asList(genres));
                    }
                }
        );

        final ContentValues values = new ContentValues();
        final Element episode = root.getChild("Episode");
        episode.getChild("id").setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                values.put("id", Long.valueOf(body));
            }
        });
        episode.getChild("SeasonNumber").setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                values.put("season", Integer.valueOf(body));
            }
        });
        episode.getChild("EpisodeNumber").setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                values.put("episode", Integer.valueOf(body));
            }
        });
        episode.getChild("EpisodeName").setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                values.put("name", body);
            }
        });
        episode.getChild("Overview").setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                values.put("summary", body);
            }
        });
        episode.getChild("FirstAired").setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                values.put("date", body);
            }
        });
        episode.setEndElementListener(new EndElementListener() {
            @Override
            public void end() {
                Episode ep = new Episode();
                ep.setShow(show);
                ep.setId(values.getAsLong("id"));
                ep.setEpisodeNumber(values.getAsInteger("episode"));
                ep.setName(values.getAsString("name"));
                ep.setSummary(values.getAsString("summary"));
                try {
                    ep.setAirDate(new SimpleDateFormat("yyyy-mm-dd").parse(values.getAsString("date")));
                } catch (Exception e) {
                    ep.setAirDate(null);
                    e.printStackTrace();
                }
                ep.setAirDate(new Date());
                int seasonNumber = values.getAsInteger("season");
                if (!seasons.containsKey(seasonNumber)) {
                    Season se = new Season();
                    se.setSeasonNumber(seasonNumber);
                    se.setShow(show);
                    se.setEpisodes(new HashMap<Integer, Episode>());
                    seasons.put(seasonNumber, se);
                    se.getEpisodes().put(ep.getEpisodeNumber(), ep);
                    ep.setSeason(se);
                } else {
                    ep.setSeason(seasons.get(seasonNumber));
                    ep.getSeason().getEpisodes().put(ep.getEpisodeNumber(), ep);
                }
                values.clear();
            }
        });

        return root.getContentHandler();
    }
}
