package com.seriesmanager.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.seriesmanager.app.adapters.CalendarAdapter;
import com.seriesmanager.app.entities.Episode;
import com.seriesmanager.app.entities.Season;
import com.seriesmanager.app.entities.Show;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "database.db";
    private static final int DATABASE_VERSION = 4;

    private static final String TABLE_SHOW = "shows";
    private static final String TABLE_SEASON = "seasons";
    private static final String TABLE_EPISODE = "episodes";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_OVERVIEW = "overview";
    private static final String COLUMN_NETWORK = "network";
    private static final String COLUMN_FAVORITE = "favorite";
    private static final String COLUMN_YEAR = "year";
    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_COUNTRY = "country";
    private static final String COLUMN_RUNTIME = "runtime";
    private static final String COLUMN_AIRDAY = "airday";
    private static final String COLUMN_AIRTIME = "airtime";
    private static final String COLUMN_COVER = "cover";
    private static final String COLUMN_GENRES = "genres";
    private static final String COLUMN_LAST_UPDATED = "last_updated";
    private static final String COLUMN_NEXT_EPISODE = "next_ep";

    private static final String COLUMN_NUMBER = "number";
    private static final String COLUMN_ID_FOREIGN = "fid";

    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_WATCHED = "watched";
    private static final String COLUMN_RATING = "rating";


    public DBHelper(Context context, CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase sqLiteDatabase) {
        super.onOpen(sqLiteDatabase);
        if (!sqLiteDatabase.isReadOnly()) {
            sqLiteDatabase.execSQL("PRAGMA foreign_keys = ON;");
        }
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createShowTable = "CREATE TABLE " + TABLE_SHOW + "(" + COLUMN_ID + " INTEGER PRIMARY KEY, "
                + COLUMN_NAME + " TEXT NOT NULL, " + COLUMN_OVERVIEW + " TEXT, " + COLUMN_NETWORK + " TEXT, "
                + COLUMN_FAVORITE + " INTEGER, " + COLUMN_YEAR + " INTEGER," + COLUMN_STATUS + " TEXT,"
                + COLUMN_COUNTRY + " TEXT," + COLUMN_RUNTIME + " INTEGER," + COLUMN_AIRDAY + " TEXT,"
                + COLUMN_AIRTIME + " TIME," + COLUMN_COVER + " BLOB," + COLUMN_GENRES + " TEXT,"
                + COLUMN_LAST_UPDATED + " INTEGER, " + COLUMN_NEXT_EPISODE + " INTEGER, FOREIGN KEY("
                + COLUMN_NEXT_EPISODE + ") REFERENCES " + TABLE_EPISODE + "(" + COLUMN_ID + "));";

        String createSeasonTable = "CREATE TABLE " + TABLE_SEASON + "(" + COLUMN_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NUMBER + " INTEGER NOT NULL, "
                + COLUMN_ID_FOREIGN + " INTEGER NOT NULL, FOREIGN KEY(" + COLUMN_ID_FOREIGN
                + ") REFERENCES " + TABLE_SHOW + "(" + COLUMN_ID + ") ON DELETE CASCADE);";

        String createEpisodeTable = "CREATE TABLE " + TABLE_EPISODE + "(" + COLUMN_ID + " INTEGER PRIMARY KEY, "
                + COLUMN_NAME + " TEXT NOT NULL, " + COLUMN_NUMBER + " INTEGER NOT NULL, " + COLUMN_OVERVIEW
                + " TEXT, " + COLUMN_WATCHED + " INTEGER, " + COLUMN_RATING + " INTEGER, " + COLUMN_DATE
                + " TEXT, " + COLUMN_ID_FOREIGN + " INTEGER NOT NULL,  FOREIGN KEY(" + COLUMN_ID_FOREIGN
                + ") REFERENCES " + TABLE_SEASON + "(" + COLUMN_ID + ") ON DELETE CASCADE);";

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
        values.put(COLUMN_YEAR, show.getYear());
        values.put(COLUMN_STATUS, show.getStatus());
        values.put(COLUMN_COUNTRY, show.getCountry());
        values.put(COLUMN_RUNTIME, show.getRuntime());
        values.put(COLUMN_AIRDAY, show.getAirDay());
        values.put(COLUMN_AIRTIME, new SimpleDateFormat("h:mm a").format(show.getAirTime()));
        values.put(COLUMN_COVER, show.getCoverInByteArray());
        values.put(COLUMN_GENRES, show.getGenresPlainText());
        values.put(COLUMN_LAST_UPDATED, show.getLastUpdated());

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_SHOW, null, values);
        db.close();
    }

    public void persistSeason(Season season) {
        ContentValues values = new ContentValues();

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
        values.put(COLUMN_RATING, (int) ep.getRating());
        values.put(COLUMN_DATE, new SimpleDateFormat("yyyy-MM-dd").format(ep.getAirDate()));

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
        updateNextShowEpisode(show.getId());
    }

    public Show loadCompleteShow(int tvdbId) {

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
            show.setYear(cursor.getInt(5));
            show.setStatus(cursor.getString(6));
            show.setCountry(cursor.getString(7));
            show.setRuntime(cursor.getInt(8));
            show.setAirDay(cursor.getString(9));
            try {
                show.setAirTime(new SimpleDateFormat("h:mm a").parse(cursor.getString(10)).getTime());
            } catch (Exception e) {
                e.printStackTrace();
            }
            show.setCover(cursor.getBlob(11));
            show.setGenres(cursor.getString(12));
            show.setLastUpdated(cursor.getLong(13));
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
                        episode.setRating((short) cursor2.getInt(5));
                        try {
                            episode.setAirDate(new SimpleDateFormat("yyyy-MM-dd").parse(cursor2.getString(6)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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

    public void updateCompleteShow(Show show) {

        updateShow(show);
        for (Season s : show.getSeasons().values()) {
            updateOrPersistSeason(s);
            for (Episode ep : s.getEpisodes().values()) {
                updateOrPersistEpisode(ep);
            }
        }
        updateNextShowEpisode(show.getId());
    }

    public void updateShow(Show show) {
        ContentValues values = new ContentValues();

        //values.put(COLUMN_ID, show.getId());
        values.put(COLUMN_NAME, show.getName());
        values.put(COLUMN_OVERVIEW, show.getSummary());
        values.put(COLUMN_NETWORK, show.getNetwork());
        values.put(COLUMN_FAVORITE, show.isFavorite() ? 1 : 0);
        values.put(COLUMN_YEAR, show.getYear());
        values.put(COLUMN_STATUS, show.getStatus());
        values.put(COLUMN_COUNTRY, show.getCountry());
        values.put(COLUMN_RUNTIME, show.getRuntime());
        values.put(COLUMN_AIRDAY, show.getAirDay());
        values.put(COLUMN_AIRTIME, new SimpleDateFormat("h:mm a").format(show.getAirTime()));
        values.put(COLUMN_COVER, show.getCoverInByteArray());
        values.put(COLUMN_GENRES, show.getGenresPlainText());
        values.put(COLUMN_LAST_UPDATED, show.getLastUpdated());

        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_SHOW, values, COLUMN_ID + " = " + show.getId(), null);
        db.close();
    }

    public void updateOrPersistSeason(Season season) {
        ContentValues values = new ContentValues();

        values.put(COLUMN_NUMBER, season.getSeasonNumber());
        values.put(COLUMN_ID_FOREIGN, season.getShow().getId());

        SQLiteDatabase db = getWritableDatabase();
        if (season.getId() < 0) {
            db.insert(TABLE_SEASON, null, values);
            String query = "SELECT " + COLUMN_ID + " FROM " + TABLE_SEASON + " WHERE " + COLUMN_ID_FOREIGN + " = "
                    + season.getShow().getId() + " AND " + COLUMN_NUMBER + " = " + season.getSeasonNumber();
            Cursor cursor = db.rawQuery(query, null);
            cursor.moveToFirst();
            season.setId(cursor.getInt(0));
        } else {
            db.update(TABLE_SEASON, values, COLUMN_ID + " = " + season.getId(), null);
        }
        db.close();
    }

    public void updateOrPersistEpisode(Episode ep) {
        ContentValues values = new ContentValues();

        values.put(COLUMN_ID_FOREIGN, ep.getSeason().getId());
        values.put(COLUMN_NUMBER, ep.getEpisodeNumber());
        values.put(COLUMN_NAME, ep.getName());
        values.put(COLUMN_OVERVIEW, ep.getSummary());
        values.put(COLUMN_WATCHED, ep.isWatched() ? 1 : 0);
        values.put(COLUMN_RATING, (int) ep.getRating());

        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT " + COLUMN_ID + " FROM " + TABLE_EPISODE + " WHERE " + COLUMN_ID + " = "
                + ep.getId();
        Cursor cursor = db.rawQuery(query, null);
        db = getWritableDatabase();
        if (cursor.moveToFirst()) {
            db.close();
            db = getWritableDatabase();
            db.update(TABLE_EPISODE, values, COLUMN_ID + " = " + ep.getId(), null);
        } else {
            db.close();
            db = getWritableDatabase();
            values.put(COLUMN_ID, ep.getId());
            db.insert(TABLE_EPISODE, null, values);
        }
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
        values.put(COLUMN_RATING, (int) ep.getRating());

        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_EPISODE, values, COLUMN_ID + " = " + ep.getId(), null);
        db.close();
    }

    public void updateEpisodeWatched(long episodeId, boolean watched) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_WATCHED, watched ? 1 : 0);

        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_EPISODE, values, COLUMN_ID + " = " + episodeId, null);
        db.close();
    }

    public List<Show> loadOverdueShows() {
        List<Show> lista = new ArrayList<Show>();
        String query = "SELECT s." + COLUMN_ID_FOREIGN + ", COUNT(s."
                + COLUMN_ID_FOREIGN + ") FROM " + TABLE_EPISODE + " e, " + TABLE_SEASON + " s "
                + "WHERE e." + COLUMN_WATCHED + "=0 and e." + COLUMN_DATE + "<date('now') and e."
                + COLUMN_ID_FOREIGN + "=s." + COLUMN_ID + " GROUP BY s." + COLUMN_ID_FOREIGN;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            if (cursor.isLast()) {
                if (cursor.getInt(0) == 0 && cursor.getInt(1) == 0)
                    cursor.moveToNext();
            }
            while (!cursor.isAfterLast()) {
                Show show = loadShow(cursor.getInt(0));
                show.setNumberOverdue(cursor.getInt(1));
                lista.add(show);
                cursor.moveToNext();
            }
        }

        db.close();
        return lista;
    }

    public List<CalendarAdapter.ParentGroup> loadCalendarShows() {
        List<CalendarAdapter.ParentGroup> lists = new ArrayList<CalendarAdapter.ParentGroup>();
        lists.add(new CalendarAdapter.ParentGroup("Previous"));
        lists.add(new CalendarAdapter.ParentGroup("Today"));
        lists.add(new CalendarAdapter.ParentGroup("Soon"));

        String query = "SELECT sh." + COLUMN_ID + ", sh." + COLUMN_NAME + ", s." + COLUMN_NUMBER
                + ", e." + COLUMN_NUMBER + ", e." + COLUMN_ID + ", e." + COLUMN_NAME + ", e."
                + COLUMN_DATE + ", e." + COLUMN_WATCHED + " from " + TABLE_EPISODE + " e, " + TABLE_SEASON
                + " s, " + TABLE_SHOW + " sh where e." + COLUMN_DATE + "<date('now') and e." + COLUMN_DATE
                + ">date('now', '-7 days') and e." + COLUMN_ID_FOREIGN + "=s." + COLUMN_ID + " and s."
                + COLUMN_ID_FOREIGN + "=sh." + COLUMN_ID + " ORDER BY e." + COLUMN_DATE;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        List<CalendarAdapter.CalendarEpisode> list = loadCalendarEpisode(cursor);
        lists.get(0).setArrayChildren(list);

        query = "SELECT sh." + COLUMN_ID + ", sh." + COLUMN_NAME + ", s." + COLUMN_NUMBER
                + ", e." + COLUMN_NUMBER + ", e." + COLUMN_ID + ", e." + COLUMN_NAME + ", e."
                + COLUMN_DATE + ", e." + COLUMN_WATCHED + " from " + TABLE_EPISODE + " e, " + TABLE_SEASON
                + " s, " + TABLE_SHOW + " sh where e." + COLUMN_DATE + "=date('now') and e."
                + COLUMN_ID_FOREIGN + "=s." + COLUMN_ID + " and s." + COLUMN_ID_FOREIGN + "=sh."
                + COLUMN_ID + " ORDER BY e." + COLUMN_DATE;

        cursor = db.rawQuery(query, null);

        list = loadCalendarEpisode(cursor);
        lists.get(1).setArrayChildren(list);

        query = "SELECT sh." + COLUMN_ID + ", sh." + COLUMN_NAME + ", s." + COLUMN_NUMBER
                + ", e." + COLUMN_NUMBER + ", e." + COLUMN_ID + ", e." + COLUMN_NAME + ", e."
                + COLUMN_DATE + ", e." + COLUMN_WATCHED + " from " + TABLE_EPISODE + " e, " + TABLE_SEASON
                + " s, " + TABLE_SHOW + " sh where e." + COLUMN_DATE + ">date('now') and e." + COLUMN_DATE
                + "<date('now', '+7 days') and e." + COLUMN_ID_FOREIGN + "=s." + COLUMN_ID + " and s."
                + COLUMN_ID_FOREIGN + "=sh." + COLUMN_ID + " ORDER BY e." + COLUMN_DATE;

        cursor = db.rawQuery(query, null);

        list = loadCalendarEpisode(cursor);
        lists.get(2).setArrayChildren(list);

        db.close();
        return lists;
    }

    private List<CalendarAdapter.CalendarEpisode> loadCalendarEpisode(Cursor cursor) {
        List<CalendarAdapter.CalendarEpisode> list = new ArrayList<CalendarAdapter.CalendarEpisode>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                CalendarAdapter.CalendarEpisode ep = new CalendarAdapter.CalendarEpisode();
                ep.setShowId(cursor.getInt(0));
                ep.setShowName(cursor.getString(1));
                ep.setSeasonNumber(cursor.getInt(2));
                ep.setEpisodeNumber(cursor.getInt(3));
                ep.setId(cursor.getLong(4));
                ep.setName(cursor.getString(5));
                try {
                    ep.setAirDate(format.parse(cursor.getString(6)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ep.setWatched(cursor.getInt(7) == 1);
                list.add(ep);
                cursor.moveToNext();
            }
        }
        return list;
    }

    public int getSeasonId(long showId, int number) {
        String query = "SELECT " + COLUMN_ID + " FROM " + TABLE_SEASON + " WHERE " + COLUMN_ID_FOREIGN
                + " = " + showId + " AND " + COLUMN_NUMBER + " = " + number;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            db.close();
            return id;
        }

        db.close();
        return -1;
    }

    public Integer getNextShowEpisode(int showId) {
        String query = "SELECT " + COLUMN_NEXT_EPISODE + " FROM shows WHERE id = " + showId;
        Integer id = null;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            id = cursor.getInt(0);
        }
        db.close();

        return id;
    }

    public Integer updateNextShowEpisode(int showId) {
        String query = "SELECT e." + COLUMN_ID + " from " + TABLE_EPISODE + " e, " + TABLE_SEASON
                + " s, " + TABLE_SHOW + " sh where e." + COLUMN_DATE + ">date('now') and "
                + "e." + COLUMN_ID_FOREIGN + "=s." + COLUMN_ID + " and s."
                + COLUMN_ID_FOREIGN + "=" + showId + " ORDER BY e." + COLUMN_DATE + " ASC LIMIT 1";
        Integer id = null;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            id = cursor.getInt(0);
        }
        db.close();

        db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NEXT_EPISODE, id);
        db.update(TABLE_SHOW, values, "id = " + showId, null);
        db.close();

        return id;
    }

    public List<Show> getFavoriteShows() {
        List<Show> lista = new ArrayList<Show>();
        String query = "SELECT " + COLUMN_ID + " FROM " + TABLE_SHOW + " WHERE " + COLUMN_FAVORITE + " = 1";

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

    public int getNumTotalShows() {
        String query = "SELECT count(*) FROM " + TABLE_SHOW;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        int value = 0;
        if (cursor.moveToFirst())
            value = cursor.getInt(0);

        db.close();

        return value;
    }

    public List<Show> getEndedShows() {
        List<Show> lista = new ArrayList<Show>();
        String query = "SELECT " + COLUMN_ID + " FROM " + TABLE_SHOW + " WHERE " + COLUMN_STATUS + " = 'Ended'";

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

    public int getNumEndedShows() {
        String query = "SELECT count(*) FROM " + TABLE_SHOW + " WHERE " + COLUMN_STATUS + " = 'Ended'";

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        int value = 0;
        if (cursor.moveToFirst())
            value = cursor.getInt(0);

        db.close();

        return value;
    }

    public List<Show> getOnAirShows() {
        List<Show> lista = new ArrayList<Show>();
        String query = "SELECT " + COLUMN_ID + " FROM " + TABLE_SHOW + " WHERE " + COLUMN_STATUS + " = 'Continuing'";

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

    public int getNumOnAirShows() {
        String query = "SELECT count(*) FROM " + TABLE_SHOW + " WHERE " + COLUMN_STATUS + " = 'Continuing'";

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        int value = 0;
        if (cursor.moveToFirst())
            value = cursor.getInt(0);

        db.close();

        return value;
    }

    public int getNumOverdueShows() {
        String query = "SELECT COUNT(DISTINCT sh." + COLUMN_ID + ") FROM " + TABLE_SHOW + " sh, "
                + TABLE_EPISODE + " e, " + TABLE_SEASON + " se" + " WHERE e." + COLUMN_WATCHED
                + "=0 and e." + COLUMN_DATE + "<date('now') and e." + COLUMN_ID_FOREIGN + "=se."
                + COLUMN_ID + " and se." + COLUMN_ID_FOREIGN + "=sh." + COLUMN_ID + " GROUP BY sh."
                + COLUMN_ID;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        int value = 0;
        if (cursor.moveToFirst())
            value = cursor.getInt(0);
        db.close();

        return value;
    }

    public int getNumWatchedEpisodes() {
        String query = "SELECT count(*) FROM " + TABLE_EPISODE + " WHERE " + COLUMN_WATCHED + " = 1";

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        int value = 0;
        if (cursor.moveToFirst())
            value = cursor.getInt(0);

        db.close();

        return value;
    }

    public int getNumUnwatchedEpisodes() {
        String query = "SELECT count(*) FROM " + TABLE_EPISODE + " WHERE " + COLUMN_WATCHED + " = 0";

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        int value = 0;
        if (cursor.moveToFirst())
            value = cursor.getInt(0);

        db.close();

        return value;
    }

    public void deleteShow(Show show) {
        SQLiteDatabase db = getReadableDatabase();
        db.delete(TABLE_SHOW, COLUMN_ID + " = " + Integer.toString(show.getId()), null);
        db.close();
    }
}
