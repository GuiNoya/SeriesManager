package com.seriesmanager.app.entities;

public class ShowSummary {
    int id;
    String name;
    String summary;
    String network;

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
}