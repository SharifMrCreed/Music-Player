package com.alle.san.musicplayer;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import static com.alle.san.musicplayer.util.Globals.MUSIC_CHANNEL;
import static com.alle.san.musicplayer.util.Globals.MUSIC_PLAYER_NOTIFICATION_CONTROL;
import static com.alle.san.musicplayer.util.Globals.NOTIFICATION_CHANNEL;
import static com.alle.san.musicplayer.util.Globals.PLAYER_NOTIFICATION;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    MUSIC_CHANNEL, MUSIC_PLAYER_NOTIFICATION_CONTROL, NotificationManager.IMPORTANCE_HIGH
            );
            NotificationChannel channel2 = new NotificationChannel(
                    NOTIFICATION_CHANNEL, PLAYER_NOTIFICATION, NotificationManager.IMPORTANCE_HIGH
            );
            channel1.enableVibration(false);
            channel1.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            channel1.enableLights(false);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel1);
            notificationManager.createNotificationChannel(channel2);

        }

    }
}
