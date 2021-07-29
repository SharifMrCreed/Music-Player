package com.alle.san.musicplayer.util;

import com.alle.san.musicplayer.models.ArtistModel;
import com.alle.san.musicplayer.models.MusicFile;

import java.util.ArrayList;

public interface UtilInterfaces {


    interface ViewChanger{
        void changeFragment(String tag, ArrayList<MusicFile> songs, int position);
        void changeFragment(MusicFile musicFile, String tag);
        void changeFragment(ArtistModel artistModel, String tag);
        void changeFragment(String playlistName, String tag);
        void onBackPressed();
    }
    interface Filter{
        void filter(String s);
    }
    interface Buttons{
        void playSong(MusicFile currentSong, int position);
        void songPause();
        void songPlay();
    }
}
