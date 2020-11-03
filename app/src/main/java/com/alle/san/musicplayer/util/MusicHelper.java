package com.alle.san.musicplayer.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.alle.san.musicplayer.util.MusicContract.MusicTable.ALBUM;
import static com.alle.san.musicplayer.util.MusicContract.MusicTable.ALBUM_IMAGE;
import static com.alle.san.musicplayer.util.MusicContract.MusicTable.ARTIST;
import static com.alle.san.musicplayer.util.MusicContract.MusicTable.DATA;
import static com.alle.san.musicplayer.util.MusicContract.MusicTable.DURATION;
import static com.alle.san.musicplayer.util.MusicContract.MusicTable.TITLE;
import static com.alle.san.musicplayer.util.MusicContract.MusicTable._ID;

public class MusicHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MusicFileDataBase";
    public static final int VERSION_NUMBER = 1;
    public MusicHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, VERSION_NUMBER, null);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String CREATE = "CREATE TABLE " + DATABASE_NAME +"("+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TITLE + " TEXT, " + ALBUM  + " TEXT, " + DATA + " TEXT, " + ARTIST + " TEXT, " + DURATION + " INTEGER, "
                + ALBUM_IMAGE  + " BLOB)";

        sqLiteDatabase.execSQL(CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
