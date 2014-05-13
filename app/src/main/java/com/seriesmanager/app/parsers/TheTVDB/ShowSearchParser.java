package com.seriesmanager.app.parsers.TheTVDB;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Log;
import android.util.Xml;

import com.seriesmanager.app.Constants;
import com.seriesmanager.app.entities.ShowSummary;
import com.seriesmanager.app.network.NetworkGet;

import org.xml.sax.ContentHandler;

import java.util.ArrayList;
import java.util.List;

public class ShowSearchParser extends AsyncTask<String, Integer, List<ShowSummary>> {

    List<ShowSummary> shows = new ArrayList<ShowSummary>();
    private String query;

    public ShowSearchParser(String entry) {
        this.query = entry.trim().replace(" ", "+");
        this.execute();
    }

    @Override
    protected List<ShowSummary> doInBackground(String... strings) {
        String url = Constants.THETVDB_DOMAIN_API + "GetSeries.php?seriesname=" + query;
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
        return shows;
    }

    private ContentHandler newHandler() {
        RootElement root = new RootElement("Data");
        Element series = root.getChild("Series");
        final ContentValues values = new ContentValues();

        series.getChild("id").setEndTextElementListener(
                new EndTextElementListener() {
                    public void end(String body) {
                        values.put("id", Integer.valueOf(body));
                    }
                }
        );
        series.getChild("SeriesName").setEndTextElementListener(
                new EndTextElementListener() {
                    public void end(String body) {
                        values.put("name", body);
                    }
                }
        );
        series.getChild("Overview").setEndTextElementListener(
                new EndTextElementListener() {
                    public void end(String body) {
                        values.put("summary", body);
                    }
                }
        );

        series.setEndElementListener(new EndElementListener() {
            @Override
            public void end() {
                ShowSummary show = new ShowSummary();
                show.setId(values.getAsInteger("id"));
                show.setSummary(values.getAsString("summary"));
                show.setName(values.getAsString("name"));
                shows.add(show);
                values.clear();
            }
        });

        return root.getContentHandler();
    }
}
