package com.seriesmanager.app.entities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.seriesmanager.app.network.NetworkGet;

import java.io.ByteArrayOutputStream;

public class ShowLite {
    private int id;
    private String name;
    private Bitmap cover;
    private boolean favorite;
    private int numberOverdue;
    private long lastUpdated;

    public ShowLite() {
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

    public void setCover(byte[] coverArray) {
        cover = BitmapFactory.decodeByteArray(coverArray, 0, coverArray.length);
    }

    public void setCover(String coverLink) {
        try {
            cover = BitmapFactory.decodeStream(new NetworkGet(coverLink).get());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCover(Bitmap cover) {
        this.cover = cover;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public int getNumberOverdue() {
        return numberOverdue;
    }

    public void setNumberOverdue(int numberOverdue) {
        this.numberOverdue = numberOverdue;
    }

    public byte[] getCoverInByteArray() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        this.cover.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        return bos.toByteArray();
    }
}
