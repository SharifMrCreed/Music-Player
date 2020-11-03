package com.alle.san.musicplayer.util;

import android.provider.BaseColumns;

public final class MusicContract implements BaseColumns {
    private MusicContract() {
    }

    public static final class MusicTable{
        public static final String _ID = BaseColumns._ID;
        public static final String TITLE = "title";
        public static final String ALBUM = "album";
        public static final String DATA = "path";
        public static final String ARTIST = "artist";
        public static final String DURATION = "duration";
        public static final String ALBUM_IMAGE = "albumArt";
    }
}
