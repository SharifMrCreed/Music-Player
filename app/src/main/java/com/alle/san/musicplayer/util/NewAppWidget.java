package com.alle.san.musicplayer.util;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.alle.san.musicplayer.PlaySongActivity;
import com.alle.san.musicplayer.R;
import com.alle.san.musicplayer.models.MusicFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static com.alle.san.musicplayer.util.Globals.ACTION_NEXT;
import static com.alle.san.musicplayer.util.Globals.ACTION_PAUSE;
import static com.alle.san.musicplayer.util.Globals.ACTION_PLAY;
import static com.alle.san.musicplayer.util.Globals.ACTION_PREVIOUS;
import static com.alle.san.musicplayer.util.Globals.ACTION_REPEAT;
import static com.alle.san.musicplayer.util.Globals.ACTION_SHUFFLE;
import static com.alle.san.musicplayer.util.Globals.AUDIO_PLAYER_STORAGE;
import static com.alle.san.musicplayer.util.Globals.WIDGET_ID;

/**
 * Implementation of App Widget functionality.
 */
public class NewAppWidget extends AppWidgetProvider{


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        MusicFile musicFile = StorageUtil.getCurrentSong(context);
        Intent intent = new Intent(context, PlaySongActivity.class);
        intent.putExtra(Globals.POSITION_KEY, StorageUtil.getPosition(context));
        intent.putExtra(Globals.SONGS_KEY, StorageUtil.getPlayingSongs(context));
        intent.putExtra(WIDGET_ID, WIDGET_ID);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        remoteViews.setOnClickPendingIntent(R.id.widget_parent, pendingIntent);
        if (musicFile != null) {
            Bitmap bitmap = Globals.albumBitmap(context, musicFile.getData());

            remoteViews.setImageViewBitmap(R.id.album_art, Globals.getResizedBitmap(bitmap, 500));
            remoteViews.setOnClickPendingIntent(R.id.previous_button, playbackAction(3, context));
            remoteViews.setOnClickPendingIntent(R.id.shuffle_button, playbackAction(4, context));
            remoteViews.setOnClickPendingIntent(R.id.repeat_button, playbackAction(5, context));
            if (StorageUtil.isShuffle(context)) remoteViews.setImageViewResource(R.id.shuffle_button, R.drawable.shuffle_icon_on);
            else  remoteViews.setImageViewResource(R.id.shuffle_button, R.drawable.shuffle_icon);
            if (StorageUtil.isRepeat(context)) remoteViews.setImageViewResource(R.id.repeat_button, R.drawable.repeat_icon_on);
            else remoteViews.setImageViewResource(R.id.repeat_button, R.drawable.repeat_icon);
            if (!StorageUtil.isPlaying(context)){
                remoteViews.setOnClickPendingIntent(R.id.widget_pause_button, playbackAction(0, context));
                remoteViews.setImageViewResource(R.id.widget_pause_button, R.drawable.play_icon);
            }else {
                remoteViews.setOnClickPendingIntent(R.id.widget_pause_button, playbackAction(1, context));
                remoteViews.setImageViewResource(R.id.widget_pause_button, R.drawable.pause_icon);
            }
            remoteViews.setOnClickPendingIntent(R.id.next_button, playbackAction(2, context));
            remoteViews.setTextViewText(R.id.song_name, musicFile.getTitle());
            remoteViews.setTextViewText(R.id.artist_name, musicFile.getArtist());
        }
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        int minWidth = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        int maxWidth = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
        int maxHeight = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);
        int minHeight = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        remoteViews.setViewVisibility(R.id.album_art, maxWidth > 300 ? View.VISIBLE : View.GONE);
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        StorageUtil.deleteAllWidgetIds(context);
        for (int appWidgetId : appWidgetIds) {
            StorageUtil.addWidgetId(appWidgetId, context);
            updateAppWidget(context, appWidgetManager, appWidgetId);

        }
    }


    private static PendingIntent playbackAction(int actionNumber, Context context) {
        Intent playbackAction = new Intent(context, MusicService.class);
        switch (actionNumber) {
            case 0:
                // Play
                playbackAction.setAction(ACTION_PLAY);
                return PendingIntent.getService(context, actionNumber, playbackAction, 0);
            case 1:
                // Pause
                playbackAction.setAction(ACTION_PAUSE);
                return PendingIntent.getService(context, actionNumber, playbackAction, 0);
            case 2:
                // Next track
                playbackAction.setAction(ACTION_NEXT);
                return PendingIntent.getService(context, actionNumber, playbackAction, 0);
            case 3:
                // Previous track
                playbackAction.setAction(ACTION_PREVIOUS);
                return PendingIntent.getService(context, actionNumber, playbackAction, 0);
            case 4:
                // Previous track
                playbackAction.setAction(ACTION_SHUFFLE);
                return PendingIntent.getService(context, actionNumber, playbackAction, 0);
            case 5:
                // Previous track
                playbackAction.setAction(ACTION_REPEAT);
                return PendingIntent.getService(context, actionNumber, playbackAction, 0);
            default:
                break;
        }
        return null;
    }

}