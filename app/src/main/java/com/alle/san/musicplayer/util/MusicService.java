package com.alle.san.musicplayer.util;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.alle.san.musicplayer.models.MusicFile;

import java.util.ArrayList;
import java.util.Random;

import static com.alle.san.musicplayer.MainActivity.allMusic;
import static com.alle.san.musicplayer.util.Globals.ADAPTER_POSITION;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener {
    private static final String TAG = "MusicService";
    UtilInterfaces.Buttons buttonControls;
    ArrayList<MusicFile> songs = new ArrayList<>();
    MediaPlayer mediaPlayer;
    IBinder binder = new MusicBinder();
    boolean shuffle, repeat = false;
    int position =-1;
    MusicFile previousSong;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: Service has been created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        songs = allMusic;
        position = intent.getIntExtra(ADAPTER_POSITION, -1);
        if (previousSong == null){
            if (!songs.isEmpty() && position != -1) {
                previousSong = songs.get(position);
                createMediaPlayer(position);
                start();
                mediaPlayer.setOnCompletionListener(this);
            }
        } else {
            if (!songs.isEmpty() && position != -1 && !previousSong.equals(songs.get(position))) {
                previousSong = songs.get(position);
                createMediaPlayer(position);
                start();
                mediaPlayer.setOnCompletionListener(this);
            }
        }
        return START_STICKY;
    }

    public void seekTo(int duration) {
        mediaPlayer.seekTo(duration);
    }

    public void pause() {
        mediaPlayer.pause();
    }

    public void start() {
        mediaPlayer.start();
    }

    public int getCurrentPosition() {
        if (mediaPlayer != null) return mediaPlayer.getCurrentPosition();
        else return 0;
    }

    public void setShuffle(boolean shuffle) {
        this.shuffle = shuffle;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public boolean isShuffle() {
        return shuffle;
    }


    public void stop() {
        mediaPlayer.stop();
    }

    public void initButtonControls(Context context){
        buttonControls = (UtilInterfaces.Buttons) context;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (repeat) {
            buttonControls.playSong(songs.get(position));
        } else {
            buttonControls.playNextSong();
        }
    }

    public void createMediaPlayer(int songPosition) {
        Uri uri = Uri.parse(songs.get(songPosition).getData());
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(getBaseContext(), uri);
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            Log.d(TAG, "getService: The service has been bound");
            return MusicService.this;
        }
    }
}
