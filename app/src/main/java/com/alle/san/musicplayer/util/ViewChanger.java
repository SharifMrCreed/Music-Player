package com.alle.san.musicplayer.util;

import com.alle.san.musicplayer.models.MusicFile;

import java.util.ArrayList;

public interface ViewChanger {

    void changeFragment(String tag, ArrayList<MusicFile> songs, int position);
    void onBackPressed();
}
