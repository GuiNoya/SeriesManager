package com.seriesmanager.app.entities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.seriesmanager.app.network.NetworkGet;

import java.io.ByteArrayOutputStream;

public class ShowSummary {
    int id;
    String name;
    String summary;
    String network;
    Bitmap cover;
    boolean added = false;

    public ShowSummary() {
    }

    public ShowSummary(int id, String name, String summary) {
        this.id = id;
        this.name = name;
        this.summary = summary;
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

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public Bitmap getCover() {
        return cover;
    }

    public void setCover(Bitmap cover) {
        this.cover = cover;
    }

    public void setCover(byte[] coverArray) {
        this.cover = BitmapFactory.decodeByteArray(coverArray, 0, coverArray.length);
    }

    public void setCover(String coverLink) {
        try {
            this.cover = BitmapFactory.decodeStream(new NetworkGet(coverLink).get());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] getCoverInByteArray() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        this.cover.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        return bos.toByteArray();
    }

    public boolean isAdded() {
        return added;
    }

    public void setAdded(boolean added) {
        this.added = added;
    }
}