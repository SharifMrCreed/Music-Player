package com.alle.san.musicplayer.util;

import android.content.ServiceConnection;

import com.alle.san.musicplayer.MinimizedPlayerFragment;
import com.alle.san.musicplayer.models.ArtistModel;
import com.alle.san.musicplayer.models.MusicFile;

import java.util.ArrayList;

public interface UtilInterfaces {


    interface ViewChanger{
        void changeFragment(String tag, ArrayList<MusicFile> songs, int position);
        void changeFragment(MusicFile musicFile, String tag);
        void changeFragment(ArtistModel artistModel, String tag);
        void changeFragment(String playlistName, String tag);
        void openPlaySongActivity(MusicFile song);
        void onBackPressed();
    }
    interface Filter{
        void filter(String s);
    }
    interface MusicServiceCallbacks{
        void bindMusicService(ServiceConnection serviceConnection);
        void unbindService(ServiceConnection serviceConnection);
        void startMusicService();
    }
    interface Buttons{
        void playSong(MusicFile currentSong, int position);
        void songPause();
        void songPlay();
    }
}
