package com.seriesmanager.app.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import com.seriesmanager.app.R;
import com.seriesmanager.app.database.DBHelper;
import com.seriesmanager.app.ui.MainActivity;

import java.util.Date;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.putExtra("via_notification", true);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, intent.getIntExtra("id", 356), resultIntent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.icon_launcher)
                .setContentTitle(intent.getStringExtra("name"))
                .setContentText(intent.getStringExtra("season_episode"))
                .setAutoCancel(true);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(800);

        DBHelper dbHelper = new DBHelper(context, null);
        int id = intent.getIntExtra("id", 0);
        dbHelper.updateNextShowEpisode(id);
        Date date = dbHelper.getNextShowEpisodeDate(id);
        String seasonEpisode = dbHelper.getNextShowEpisodeSeasonEpisodeString(id);
        if (date != null && seasonEpisode != null)
            Notification.newNotification(context, id, intent.getStringExtra("name"), seasonEpisode, date);
    }
}
