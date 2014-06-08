package com.seriesmanager.app.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Date;

public class Notification {

    public static void newNotification(Context context, int showId, String showName, String seasonEpisode, Date dateTime) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, NotificationReceiver.class);
        alarmIntent.putExtra("id", showId);
        alarmIntent.putExtra("name", showName);
        alarmIntent.putExtra("season_episode", seasonEpisode);
        PendingIntent alarmPI = PendingIntent.getBroadcast(context, showId, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC, dateTime.getTime(), alarmPI);
    }

}
