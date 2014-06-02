package com.seriesmanager.app.entities;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Show {
    private int id;
    private String name;
    private Bitmap cover;
    private String summary;
    private Map<Integer, Season> seasons;
    private Date firstAired;
    private String network;
    private boolean favorite = false;
    private List<String> genres;
    private boolean added = true;
    private int numberOverdue;
    private boolean loaded = false;
    private long lastUpdated;

    public Show() {
        this.seasons = new HashMap<Integer, Season>();
        this.genres = new ArrayList<String>(5);
    }

    public Show(int id, String name, String summary, Date firstAired, String network) {
        this.id = id;
        this.name = name;
        this.summary = summary;
        this.firstAired = firstAired;
        this.network = network;
        this.seasons = new HashMap<Integer, Season>();
        this.genres = new ArrayList<String>(5);
    }

    public Show(int id, String name, String summary, Date firstAired, String network, boolean added) {
        this.id = id;
        this.name = name;
        this.summary = summary;
        this.firstAired = firstAired;
        this.network = network;
        this.added = added;
        this.seasons = new HashMap<Integer, Season>();
        this.genres = new ArrayList<String>(5);
    }

    public Show(int id, String name, String summary, boolean favorite, Date firstAired, String network) {
        this.id = id;
        this.name = name;
        this.summary = summary;
        this.favorite = favorite;
        this.firstAired = firstAired;
        this.network = network;
        this.seasons = new HashMap<Integer, Season>();
        this.genres = new ArrayList<String>(5);
    }

    public Show(int id, String name, String summary, Date firstAired, String network, Bitmap cover) {
        this.id = id;
        this.name = name;
        this.cover = cover;
        this.summary = summary;
        this.firstAired = firstAired;
        this.network = network;
        this.seasons = new HashMap<Integer, Season>();
        this.genres = new ArrayList<String>(5);
    }

    public Show(int id, String name, String summary, boolean favorite, Date firstAired, String network, Bitmap cover) {
        this.id = id;
        this.name = name;
        this.cover = cover;
        this.summary = summary;
        this.favorite = favorite;
        this.firstAired = firstAired;
        this.network = network;
        this.seasons = new HashMap<Integer, Season>();
        this.genres = new ArrayList<String>(5);
    }

    public Show(int id, String name, Bitmap cover, String summary, HashMap<Integer, Season> seasons, Date firstAired, String network, List<String> genres) {
        this.id = id;
        this.name = name;
        this.cover = cover;
        this.summary = summary;
        this.seasons = seasons;
        this.firstAired = firstAired;
        this.network = network;
        this.genres = genres;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getCover() {
        return cover;
    }

    public void setCover(Bitmap cover) {
        this.cover = cover;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Map<Integer, Season> getSeasons() {
        return seasons;
    }

    public void setSeasons(Map<Integer, Season> seasons) {
        this.seasons = seasons;
    }

    public Date getFirstAired() {
        return firstAired;
    }

    public void setFirstAired(Date firstAired) {
        this.firstAired = firstAired;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public boolean isAdded() {
        return added;
    }

    public void setAdded(boolean added) {
        this.added = added;
    }

    public int getNumberOverdue() {
        return numberOverdue;
    }

    public void setNumberOverdue(int numberOverdue) {
        this.numberOverdue = numberOverdue;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
