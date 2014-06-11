package com.seriesmanager.app.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.seriesmanager.app.Comm;
import com.seriesmanager.app.database.DBHelper;
import com.seriesmanager.app.entities.Show;
import com.seriesmanager.app.notifications.Notification;
import com.seriesmanager.app.parsers.trakt.ShowUpdateParser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class UpdateShowsLoader extends AsyncTaskLoader<List<Show>> {

    Map<Long, Long> map;

    public UpdateShowsLoader(Context context, Map<Long, Long> map) {
        super(context);
        this.map = map;
        onStartLoading();
    }

    @Override
    public List<Show> loadInBackground() {
        List<Show> list = new ArrayList<Show>();
        if (!Comm.updated) {
            Comm.updated = true;
            for (Long showId : map.keySet()) {
                try {
                    Show s = new ShowUpdateParser(showId, map.get(showId)).get();
                    if (s != null) {
                        list.add(s);
                        DBHelper dbHelper = new DBHelper(getContext(), null);
                        Integer i = dbHelper.getNextShowEpisode(s.getId());
                        if (i != null) {
                            dbHelper.updateCompleteShow(s);
                            Date date = dbHelper.getNextShowEpisodeDate(s.getId());
                            String seasonEpisode = dbHelper.getNextShowEpisodeSeasonEpisodeString(s.getId());
                            if (date != null && seasonEpisode != null)
                                Notification.newNotification(getContext(), s.getId(), s.getName(), seasonEpisode, date);
                        } else {
                            dbHelper.updateCompleteShow(s);
                            Date date = dbHelper.getNextShowEpisodeDate(s.getId());
                            String seasonEpisode = dbHelper.getNextShowEpisodeSeasonEpisodeString(s.getId());
                            if (date != null && seasonEpisode != null)
                                Notification.newNotification(getContext(), s.getId(), s.getName(), seasonEpisode, date);
                        }
                    }
                } catch (Exception e) {
                }
                map.remove(showId);
            }
        }
        return list;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }
}
