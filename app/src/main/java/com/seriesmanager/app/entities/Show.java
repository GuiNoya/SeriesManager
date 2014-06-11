package com.seriesmanager.app.entities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.seriesmanager.app.network.NetworkGet;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class Show {
    private int id;
    private String name;
    private Bitmap cover;
    private String summary;
    private Map<Integer, Season> seasons;
    private int year;
    private String country;
    private int runtime;
    private String status;
    private String airDay;
    private Date airTime;
    private String network;
    private boolean favorite = false;
    private List<String> genres;
    private int numberOverdue;
    private long lastUpdated;

    public Show() {
        this.seasons = new HashMap<Integer, Season>();
        this.genres = new ArrayList<String>(5);
    }

    /*public Show(int id, String name, String summary, Date firstAired, String network) {
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
    }*/

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
            this.cover = BitmapFactory.decodeStream(new NetworkGet(coverLink).get());
            /*URL url = new URL(coverLink);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();*/
            //this.cover = BitmapFactory.decodeStream(connection.getInputStream());
        /*} catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();*/
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public void setGenres(String genres) {
        StringTokenizer token = new StringTokenizer(genres, "|");
        while (token.hasMoreTokens()) {
            this.genres.add(token.nextToken());
        }
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public int getNumberOverdue() {
        return numberOverdue;
    }

    public void setNumberOverdue(int numberOverdue) {
        this.numberOverdue = numberOverdue;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAirDay() {
        return airDay;
    }

    public void setAirDay(String airDay) {
        this.airDay = airDay;
    }

    public Date getAirTime() {
        return airTime;
    }

    public void setAirTime(Date airTime) {
        this.airTime = airTime;
    }

    //Getters and Setters that need some kind of conversion between types
    public void setAirTime(long airTime) {
        Date date = new Date(airTime);
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
        try {
            this.airTime = sdf.parse(sdf.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getGenresPlainText() {
        String genre = "";
        for (String g : genres) {
            genre += g + "|";
        }
        return genre;
    }

    public byte[] getCoverInByteArray() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        this.cover.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        return bos.toByteArray();
    }
}
