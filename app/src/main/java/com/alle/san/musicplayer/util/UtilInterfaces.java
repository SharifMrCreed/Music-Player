package com.alle.san.musicplayer.util;

import android.content.Intent;

import com.alle.san.musicplayer.models.ArtistModel;
import com.alle.san.musicplayer.models.MusicFile;

import java.util.ArrayList;

public interface UtilInterfaces {


    interface ViewChanger{
        void changeFragment(String tag, ArrayList<MusicFile> songs, int position);
        void changeFragment(MusicFile musicFile, String tag);
        void changeFragment(ArtistModel artistModel, String tag);
        void onBackPressed();
    }
    interface Filter{
        void filter(String s);
    }
    interface Buttons{
        void songPlayPause();
        void playNextSong();
        void playPreviousSong();
        void playSong(MusicFile musicFile);
        void buildNotification(PlaybackStatus paused);
        void removeNotification();
        void handleIncomingActions(Intent intent);
    }
}
