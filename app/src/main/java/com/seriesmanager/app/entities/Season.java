package com.seriesmanager.app.entities;

import android.media.Image;

import java.util.HashMap;
import java.util.Map;

public class Season {
    private Show show;
    private int seasonNumber;
    private Image cover;
    private Map<Integer, Episode> episodes = new HashMap<Integer, Episode>();

    public Season() {
        this.episodes = new HashMap<Integer, Episode>();
    }

    public Season(Show show, int seasonNumber) {
        this.show = show;
        this.seasonNumber = seasonNumber;
        this.episodes = new HashMap<Integer, Episode>();
    }

    public Season(Show show, int seasonNumber, Map<Integer, Episode> episodes) {
        this.show = show;
        this.seasonNumber = seasonNumber;
        this.episodes = episodes;
    }

    public Show getShow() {
        return show;
    }

    public void setShow(Show show) {
        this.show = show;
    }

    public int getSeasonNumber() {
        return seasonNumber;
    }

    public void setSeasonNumber(int seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    public Map<Integer, Episode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(Map<Integer, Episode> episodes) {
        this.episodes = episodes;
    }

    public Image getCover() {
        return cover;
    }

    public void setCover(Image cover) {
        this.cover = cover;
    }

}
