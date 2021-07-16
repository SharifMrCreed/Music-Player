package com.alle.san.musicplayer.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.alle.san.musicplayer.R;

import java.util.Objects;

public class Globals {
    public static final String  SONG_LIST_FRAGMENT_TAG = "All Songs";
    public static final String  ARTISTS_FRAGMENT_TAG = "Artists";
    public static final String  PLAYLIST_FRAGMENT_TAG = "Playlists";
    public static final String  ALBUM_SONG_LIST_FRAGMENT_TAG = "Album Songs";
    public static final String PLAY_SONG_ACTIVITY_TAG = "Play Song Fragment Tag";
    public static final String ADAPTER_POSITION = "Position";
    public static final String SONGS_LIST = "Song";
    public static final String SONGS = "Songs";
    public static final String STRING_EXTRA = "String extra";
    public static final String ALBUMS_FRAGMENT_TAG = "Albums";

    //AudioPlayer notification
    public static final int NOTIFICATION_ID = 101;
    public static final String MUSIC_CHANNEL = "Music channel";
    public static final String NOTIFICATION_CHANNEL = "Notification channel";
    public static final String PLAYER_NOTIFICATION = "Player notifications";
    public static final String MUSIC_PLAYER_NOTIFICATION_CONTROL = "Player notification controls";

    public static final String ACTION_PLAY = "com.alle.san.musicplayer.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.alle.san.musicplayer.ACTION_PAUSE";
    public static final String ACTION_PREVIOUS = "com.alle.san.musicplayer.ACTION_PREVIOUS";
    public static final String ACTION_NEXT = "com.alle.san.musicplayer.ACTION_NEXT";
    public static final String ACTION_STOP = "com.alle.san.musicplayer.ACTION_STOP";

    //Methods
    public static Bitmap albumBitmap(Context context, String dataPath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        byte[] image;
        try {
            retriever.setDataSource(dataPath);
            image = retriever.getEmbeddedPicture();
        } catch (IllegalArgumentException | SecurityException iE) {
            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
            image = null;
        }
        retriever.release();
        if (image != null) return BitmapFactory.decodeByteArray(image, 0, image.length);
        else
            return ((BitmapDrawable) Objects.requireNonNull(ContextCompat.getDrawable(context, R.drawable.allecon))).getBitmap();
    }

}
