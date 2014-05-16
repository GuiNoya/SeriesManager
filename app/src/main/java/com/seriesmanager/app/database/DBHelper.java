package com.seriesmanager.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.seriesmanager.app.entities.Episode;
import com.seriesmanager.app.entities.Season;
import com.seriesmanager.app.entities.Show;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "database.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_SHOW = "shows";
    private static final String TABLE_SEASON = "seasons";
    private static final String TABLE_EPISODE = "episodes";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_OVERVIEW = "overview";
    private static final String COLUMN_NETWORK = "network";
    private static final String COLUMN_FAVORITE = "favorite";

    private static final String COLUMN_NUMBER = "number";
    private static final String COLUMN_ID_FOREIGN = "fid";

    private static final String COLUMN_WATCHED = "watched";


    public DBHelper(Context context, CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createShowTable = "CREATE TABLE " + TABLE_SHOW + "(" + COLUMN_ID + " INTEGER PRIMARY KEY, "
                + COLUMN_NAME + " TEXT NOT NULL, " + COLUMN_OVERVIEW + " TEXT, " + COLUMN_NETWORK + " TEXT, "
                + COLUMN_FAVORITE + " INTEGER" + ");";

        String createSeasonTable = "CREATE TABLE " + TABLE_SEASON + "(" + COLUMN_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NUMBER + " INTEGER NOT NULL, "
                + COLUMN_ID_FOREIGN + " INTEGER NOT NULL, FOREIGN KEY(" + COLUMN_ID_FOREIGN
                + ") REFERENCES " + TABLE_SHOW + "(" + COLUMN_ID + "));";

        String createEpisodeTable = "CREATE TABLE " + TABLE_EPISODE + "(" + COLUMN_ID + " INTEGER PRIMARY KEY, "
                + COLUMN_NAME + " TEXT NOT NULL, " + COLUMN_NUMBER + " INTEGER NOT NULL, " + COLUMN_OVERVIEW
                + " TEXT, " + COLUMN_WATCHED + " INTEGER, " + COLUMN_ID_FOREIGN
                + " INTEGER NOT NULL,  FOREIGN KEY(" + COLUMN_ID_FOREIGN + ") REFERENCES " + TABLE_SEASON
                + "(" + COLUMN_ID + "));";

        sqLiteDatabase.execSQL(createShowTable);
        sqLiteDatabase.execSQL(createSeasonTable);
        sqLiteDatabase.execSQL(createEpisodeTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        Log.w(DBHelper.class.getName(), "Upgrading database from version " + i + " to "
                + i2 + ", which will destroy all old data");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_EPISODE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_SEASON);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_SHOW);
        onCreate(sqLiteDatabase);
    }

    public void persistShow(Show show) {
        ContentValues values = new ContentValues();

        values.put(COLUMN_ID, show.getId());
        values.put(COLUMN_NAME, show.getName());
        values.put(COLUMN_OVERVIEW, show.getSummary());
        values.put(COLUMN_NETWORK, show.getNetwork());
        values.put(COLUMN_FAVORITE, show.isFavorite() ? 1 : 0);

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_SHOW, null, values);
        db.close();
    }

    public void persistSeason(Season season) {
        ContentValues values = new ContentValues();

        //values.put(COLUMN_ID, season.getId());
        values.put(COLUMN_NUMBER, season.getSeasonNumber());
        values.put(COLUMN_ID_FOREIGN, season.getShow().getId());

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_SEASON, null, values);
        db.close();

        db = getReadableDatabase();
        String query = "SELECT " + COLUMN_ID + " FROM " + TABLE_SEASON + " WHERE " + COLUMN_ID_FOREIGN + " = "
                + season.getShow().getId() + " AND " + COLUMN_NUMBER + " = " + season.getSeasonNumber();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        season.setId(cursor.getInt(0));
        db.close();
    }

    public void persistEpisode(Episode ep) {
        ContentValues values = new ContentValues();

        values.put(COLUMN_ID, ep.getId());
        values.put(COLUMN_ID_FOREIGN, ep.getSeason().getId());
        values.put(COLUMN_NUMBER, ep.getEpisodeNumber());
        values.put(COLUMN_NAME, ep.getName());
        values.put(COLUMN_OVERVIEW, ep.getSummary());
        values.put(COLUMN_WATCHED, ep.isWatched() ? 1 : 0);

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_EPISODE, null, values);
        db.close();
    }

    public void persistCompleteShow(Show show) {
        persistShow(show);
        for (Season season : show.getSeasons().values()) {
            persistSeason(season);
            for (Episode ep : season.getEpisodes().values()) {
                persistEpisode(ep);
            }
        }
    }

    public Show loadShowComplete(int tvdbId) {

        Show show = loadShow(tvdbId);
        loadShowExtraInfo(show);

        return show;
    }

    public Show loadShow(int tvdbId) {
        Show show = null;
        String query = "SELECT * FROM " + TABLE_SHOW + " WHERE " + COLUMN_ID + " = " + tvdbId;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            show = new Show();
            show.setId(cursor.getInt(0));
            show.setName(cursor.getString(1));
            show.setSummary(cursor.getString(2));
            show.setNetwork(cursor.getString(3));
            show.setFavorite(cursor.getInt(4) == 1);
            show.setFirstAired(new Date());
        }

        db.close();
        return show;
    }

    public Show loadShowExtraInfo(Show show) {
        if (show == null) {
            return null;
        }

        String query = "SELECT * FROM " + TABLE_SEASON + " WHERE " + COLUMN_ID_FOREIGN + " = " + show.getId()
                + " ORDER BY " + COLUMN_NUMBER;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Season season = new Season();
                season.setId(cursor.getInt(0));
                season.setSeasonNumber(cursor.getInt(1));
                season.setShow(show);
                show.getSeasons().put(season.getSeasonNumber(), season);

                query = "SELECT * FROM " + TABLE_EPISODE + " WHERE "
                        + COLUMN_ID_FOREIGN + " = " + season.getId();

                Cursor cursor2 = db.rawQuery(query, null);
                if (cursor2.moveToFirst()) {
                    while (!cursor2.isAfterLast()) {
                        Episode episode = new Episode();
                        episode.setId(cursor2.getInt(0));
                        episode.setName(cursor2.getString(1));
                        episode.setEpisodeNumber(cursor2.getInt(2));
                        episode.setSummary(cursor2.getString(3));
                        episode.setWatched(cursor2.getInt(4) == 1);
                        episode.setSeason(season);
                        episode.setShow(show);
                        season.getEpisodes().put(episode.getEpisodeNumber(), episode);
                        cursor2.moveToNext();
                    }
                }
                cursor.moveToNext();
            }
        }
        db.close();
        return show;
    }

    public List<Show> loadShowsAll() {
        List<Show> lista = new ArrayList<Show>();
        String query = "SELECT * FROM " + TABLE_SHOW + " ORDER BY " + COLUMN_FAVORITE + " DESC, "
                + COLUMN_NAME + " ASC;";

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Show show = loadShow(cursor.getInt(0));
                lista.add(show);
                cursor.moveToNext();
            }
        }

        db.close();
        return lista;
    }

    public void updateShow(Show show) {
        ContentValues values = new ContentValues();

        //values.put(COLUMN_ID, show.getId());
        values.put(COLUMN_NAME, show.getName());
        values.put(COLUMN_OVERVIEW, show.getSummary());
        values.put(COLUMN_NETWORK, show.getNetwork());
        values.put(COLUMN_FAVORITE, show.isFavorite() ? 1 : 0);

        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_SHOW, values, COLUMN_ID + " = " + show.getId(), null);
        db.close();
    }

    public void updateEpisode(Episode ep) {
        ContentValues values = new ContentValues();

        //values.put(COLUMN_ID, ep.getId());
        values.put(COLUMN_ID_FOREIGN, ep.getSeason().getId());
        values.put(COLUMN_NUMBER, ep.getEpisodeNumber());
        values.put(COLUMN_NAME, ep.getName());
        values.put(COLUMN_OVERVIEW, ep.getSummary());
        values.put(COLUMN_WATCHED, ep.isWatched() ? 1 : 0);

        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_EPISODE, values, COLUMN_ID + " = " + ep.getId(), null);
        db.close();
    }

}
