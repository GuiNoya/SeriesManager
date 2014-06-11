package com.seriesmanager.app;

import android.content.Context;

import com.seriesmanager.app.entities.ShowLite;

import java.util.List;

public class Comm {
    public static List<ShowLite> showsInstances;
    public static List<ShowLite> showsFiltered;
    public static Context mainContext = null;
    public static boolean updated = false;
}
