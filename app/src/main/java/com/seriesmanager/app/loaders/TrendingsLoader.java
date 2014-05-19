package com.seriesmanager.app.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.seriesmanager.app.entities.ShowSummary;
import com.seriesmanager.app.parsers.trakt.TrendingShowsParser;

import java.util.List;

public class TrendingsLoader extends AsyncTaskLoader<List<ShowSummary>> {

    public TrendingsLoader(Context context) {
        super(context);
    }

    @Override
    public List<ShowSummary> loadInBackground() {
        List<ShowSummary> list;
        try {
            list = new TrendingShowsParser().get();
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
