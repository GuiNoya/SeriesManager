package com.seriesmanager.app;

import android.content.Context;

import com.seriesmanager.app.entities.Episode;
import com.seriesmanager.app.entities.Season;
import com.seriesmanager.app.entities.Show;

import java.util.List;

public class Comm {
    public static Show actualShow = null;
    public static Season actualSeason = null;
    public static Episode actualEpisode = null;
    public static Context mainContext = null;
    public static List<Show> showsList = null;
    public static boolean updated = false;
}
