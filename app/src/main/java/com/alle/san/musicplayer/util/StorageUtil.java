package com.alle.san.musicplayer.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.alle.san.musicplayer.models.MusicFile;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;

public class StorageUtil {
    private static final String STORAGE = " com.valdioveliu.valdio.audioplayer.STORAGE";
    private static SharedPreferences preferences;
    private static final String TAG = "ReadExternalStorage";

    public static ArrayList<MusicFile> filesInRoot(File file){
        ArrayList<MusicFile> songsList = new ArrayList<>();
        File [] directoryFiles = file.listFiles();
        for (File file1: directoryFiles){
            if (file1.isDirectory() && !file1.isHidden()){
                songsList.addAll(filesInRoot(file1));
            }else{
                String fileName = file1.getName();
                if (    fileName.endsWith(".mp3") || fileName.endsWith(".m4a") ||
                        fileName.endsWith(".wav") || fileName.endsWith(".acc") )
                {

                    songsList.add(new MusicFile(fileName, "", 0,"",file1.getPath(), null));
                }
            }
        }
        return songsList;
    }

    public static HashSet<File> songFolders(File file){
        HashSet<File> songsList = new HashSet<File>();
        File [] directoryFiles = file.listFiles();
        for (File file1: directoryFiles){
            String fileName = file1.getName();
            if (    fileName.endsWith(".mp3") || fileName.endsWith(".m4a") ||
                    fileName.endsWith(".wav") || fileName.endsWith(".acc") )
            {
                songsList.add(file);
            }
            if (file1.isDirectory() && !file1.isHidden()){
                songsList.addAll(songFolders(file1));
            }else{

            }
        }
        return songsList;
    }


    public static ArrayList<MusicFile> getSongsFromStorage(Context context){
        ArrayList<MusicFile> musicFiles = new ArrayList<>();
        Uri uri;
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) uri = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        else uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]{
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA
        };
        Cursor cursor = context.getContentResolver()
                .query(uri, projection, null, null, MediaStore.Audio.Media.TITLE);
        if(cursor != null){
            while (cursor.moveToNext()){
                String songName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String songID = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));

                String title = delimitTitle(songName) ;
                int length;
                if (duration == null) length = 0; else length = Integer.parseInt(duration);
                if (length>45000){
                    MusicFile musicFile = new MusicFile(songID, title, album, data, artist, length);
                    musicFiles.add(musicFile);
                }

            }
            cursor.close();
        }
        return musicFiles;
    }

    public static Bitmap getBitmap(String data){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(data);
        byte [] imageArt = retriever.getEmbeddedPicture();
        retriever.release();
        if (imageArt != null){
            return BitmapFactory.decodeByteArray(imageArt, 0, imageArt.length);
        }else {
            return null;
        }
    }

    public static String delimitTitle(String songName){
        String newTitle = "";
        if(songName.contains("(")| songName.contains("[")){
            String[] splitRes = songName.split(" ",15);
            StringBuilder buildSongName = new StringBuilder();
            for (String splitRe : splitRes) {
                if (splitRe.contains("(")) {
                    break;
                } else if (splitRe.contains("[")) {
                    break;
                } else {
                    buildSongName.append(splitRe).append(" ");
                }
            }
            newTitle = buildSongName.toString();
        }else if(songName.contains("|")){
            String[] splitRes = songName.split(" ",15);
            StringBuilder buildSongName = new StringBuilder();
            for (String splitRe : splitRes) {
                if (splitRe.contains("|")) {
                    break;
                } else {
                    buildSongName.append(splitRe).append(" ");
                }
            }
            newTitle = buildSongName.toString();
        }else if(songName.contains("_") && !songName.contains(" ")){
            String[] splitRes = songName.split("_",15);
            StringBuilder buildSongName = new StringBuilder();
            for (String splitRe : splitRes) buildSongName.append(splitRe).append(" ");
            newTitle = buildSongName.toString();
        }else {
            newTitle = songName;
        }
        return  newTitle;
    }

    public static void storeAudio(ArrayList<MusicFile> arrayList, Context context ) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(arrayList);
        editor.putString("audioArrayList", json);
        editor.apply();
    }

    public static ArrayList<MusicFile> loadAudio(Context context) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString("audioArrayList", null);
        Type type = new TypeToken<ArrayList<MusicFile>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public static void storeAudioIndex(int index, Context context) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("audioIndex", index);
        editor.apply();
    }

    public static int loadAudioIndex(Context context) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        return preferences.getInt("audioIndex", -1);//return -1 if no data found
    }

    public static void clearCachedAudioPlaylist(Context context) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

}
