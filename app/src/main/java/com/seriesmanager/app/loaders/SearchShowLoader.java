package com.seriesmanager.app.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.seriesmanager.app.entities.ShowSummary;
import com.seriesmanager.app.parsers.trakt.ShowSearchParser;

import java.util.List;

public class SearchShowLoader extends AsyncTaskLoader<List<ShowSummary>> {

    String query;

    public SearchShowLoader(Context context, String query) {
        super(context);
        this.query = query;
        onStartLoading();
    }

    @Override
    public List<ShowSummary> loadInBackground() {
        List<ShowSummary> list;
        try {
            list = new ShowSearchParser(query).get();
        } catch (Exception e) {
            list = null;
        }
        return list;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }
}
