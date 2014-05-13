package com.seriesmanager.app.entities;

import java.util.Date;

public class Episode {
    private long id;
    private Show show;
    private Season season;
    private int episodeNumber;
    private String name;
    private String summary;
    private Date airDate;
    private boolean watched = false;

    public Episode() {
    }

    public Episode(long id, Show show, Season season, int episodeNumber, String name) {
        this.id = id;
        this.show = show;
        this.season = season;
        this.episodeNumber = episodeNumber;
        this.name = name;
    }

    public Episode(long id, Show show, Season season, int episodeNumber, String name, boolean watched) {
        this.id = id;
        this.show = show;
        this.season = season;
        this.episodeNumber = episodeNumber;
        this.name = name;
        this.watched = watched;
    }

    public Episode(long id, Show show, Season season, int episodeNumber, String name, Date airDate, boolean watched) {
        this.id = id;
        this.show = show;
        this.season = season;
        this.episodeNumber = episodeNumber;
        this.name = name;
        this.airDate = airDate;
        this.watched = watched;
    }

    public Episode(long id, Show show, Season season, int episodeNumber, String name, Date airDate, boolean watched, String summary) {
        this.id = id;
        this.show = show;
        this.season = season;
        this.episodeNumber = episodeNumber;
        this.name = name;
        this.summary = summary;
        this.airDate = airDate;
        this.watched = watched;
    }

    public Episode(long id, Show show, Season season, int episodeNumber, String name, String summary, Date airDate, boolean watched) {
        this.id = id;
        this.show = show;
        this.season = season;
        this.episodeNumber = episodeNumber;
        this.name = name;
        this.summary = summary;
        this.airDate = airDate;
        this.watched = watched;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Show getShow() {
        return show;
    }

    public void setShow(Show show) {
        this.show = show;
    }

    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    public int getEpisodeNumber() {
        return episodeNumber;
    }

    public void setEpisodeNumber(int episodeNumber) {
        this.episodeNumber = episodeNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Date getAirDate() {
        return airDate;
    }

    public void setAirDate(Date airDate) {
        this.airDate = airDate;
    }

    public boolean isWatched() {
        return watched;
    }

    public void setWatched(boolean watched) {
        this.watched = watched;
    }
}
