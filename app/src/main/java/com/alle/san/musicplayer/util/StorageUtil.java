package com.alle.san.musicplayer.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import com.alle.san.musicplayer.models.ArtistModel;
import com.alle.san.musicplayer.models.MusicFile;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;

import static com.alle.san.musicplayer.util.Globals.ALBUMS_FRAGMENT_TAG;
import static com.alle.san.musicplayer.util.Globals.ARTISTS_FRAGMENT_TAG;
import static com.alle.san.musicplayer.util.Globals.AUDIO_PLAYER_PLAYLISTS;
import static com.alle.san.musicplayer.util.Globals.AUDIO_PLAYER_STORAGE;
import static com.alle.san.musicplayer.util.Globals.CURRENT_SONG;
import static com.alle.san.musicplayer.util.Globals.FOLDERS_FRAGMENT_TAG;
import static com.alle.san.musicplayer.util.Globals.ORDER;
import static com.alle.san.musicplayer.util.Globals.PLAYLIST_KEY;
import static com.alle.san.musicplayer.util.Globals.POSITION_KEY;
import static com.alle.san.musicplayer.util.Globals.REPEAT_KEY;
import static com.alle.san.musicplayer.util.Globals.SHUFFLE_KEY;
import static com.alle.san.musicplayer.util.Globals.SONGS_KEY;
import static com.alle.san.musicplayer.util.Globals.SORT_ORDER;

public class StorageUtil {
    private static SharedPreferences preferences;

    public static HashSet<String> getSongFolders(File file){
        HashSet<String> songsList = new HashSet<>();
        ArrayList<String> folderNames = new ArrayList<>();
        File [] directoryFiles = file.listFiles();
        if (directoryFiles!= null) {
            for (File file1 : directoryFiles) {
                String fileName = file1.getName();
                if (fileName.endsWith(".mp3") || fileName.endsWith(".m4a") ||
                        fileName.endsWith(".wav") || fileName.endsWith(".acc") && !folderNames.contains(file.getName())) {
                    if (file.getName().length() > 1)songsList.add(file.getName());
                    folderNames.add(file.getName());

                }
                if (file1.isDirectory() && !file1.isHidden()) {
                    songsList.addAll(getSongFolders(file1));
                }
            }
        }
        return songsList;
    }

    public static ArrayList<MusicFile> getSongsFromStorage(Context context, String sortOrder, String order){
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
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DATE_ADDED
        };
        Cursor cursor = context.getContentResolver()
                .query(uri, projection, null, null, sortOrder + " " + order);
        if(cursor != null){
            while (cursor.moveToNext()){
                String songName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String songID = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String dateAdded = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED));

                String title = delimitTitle(songName) ;
                int length;
                if (duration == null) length = 0; else length = Integer.parseInt(duration);
                if (length>45000){
                    MusicFile musicFile = new MusicFile(songID, title, album, data, artist, length, dateAdded);
                    musicFiles.add(musicFile);
                }

            }
            cursor.close();
        }
        return musicFiles;
    }

    public static ArrayList<MusicFile> getSongsFromFolder(Context context, String folderName){
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
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DATE_ADDED
        };
        Cursor cursor = context.getContentResolver()
                .query(uri, projection, MediaStore.Audio.Media.DATA + " like ? ",
                        new String[]{"%"+folderName+"%"}, MediaStore.Audio.Media.TITLE);
        if(cursor != null){
            while (cursor.moveToNext()){
                String songName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String songID = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String dateAdded = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED));

                String title = delimitTitle(songName) ;
                int length;
                if (duration == null) length = 0; else length = Integer.parseInt(duration);
                if (length>45000){
                    MusicFile musicFile = new MusicFile(songID, title, album, data, artist, length, dateAdded);
                    musicFiles.add(musicFile);
                }

            }
            cursor.close();
        }
        return musicFiles;
    }

    public static String delimitTitle(String songName){
        String newTitle;
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
        preferences = context.getSharedPreferences(AUDIO_PLAYER_STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(arrayList);
        editor.putString(SONGS_KEY, json);
        editor.apply();
    }

    public static void setCurrentSong(MusicFile song, Context context) {
        preferences = context.getSharedPreferences(AUDIO_PLAYER_STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(song);
        editor.putString(CURRENT_SONG, json);
        editor.apply();
    }

    public static void setArtists(ArrayList<ArtistModel> song, Context context) {
        preferences = context.getSharedPreferences(AUDIO_PLAYER_STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(song);
        editor.putString(ARTISTS_FRAGMENT_TAG, json);
        editor.apply();
    }

    public static void setFolders(ArrayList<ArtistModel> song, Context context) {
        preferences = context.getSharedPreferences(AUDIO_PLAYER_STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(song);
        editor.putString(FOLDERS_FRAGMENT_TAG, json);
        editor.apply();
    }

    public static ArrayList<ArtistModel> getFolders(Context context) {
        preferences = context.getSharedPreferences(AUDIO_PLAYER_STORAGE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString(FOLDERS_FRAGMENT_TAG, null);
        Type type = new TypeToken<ArrayList<ArtistModel>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public static ArrayList<ArtistModel> getArtists(Context context) {
        preferences = context.getSharedPreferences(AUDIO_PLAYER_STORAGE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString(ARTISTS_FRAGMENT_TAG, null);
        Type type = new TypeToken<ArrayList<ArtistModel>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public static void setAlbums(ArrayList<MusicFile> song, Context context) {
        preferences = context.getSharedPreferences(AUDIO_PLAYER_STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(song);
        editor.putString(ALBUMS_FRAGMENT_TAG, json);
        editor.apply();
    }

    public static ArrayList<MusicFile> getAlbums(Context context) {
        preferences = context.getSharedPreferences(AUDIO_PLAYER_STORAGE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString(ALBUMS_FRAGMENT_TAG, null);
        Type type = new TypeToken<ArrayList<MusicFile>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public static MusicFile getCurrentSong(Context context) {
        preferences = context.getSharedPreferences(AUDIO_PLAYER_STORAGE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString(CURRENT_SONG, null);
        Type type = new TypeToken<MusicFile>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public static ArrayList<MusicFile> getPlayingSongs(Context context) {
        preferences = context.getSharedPreferences(AUDIO_PLAYER_STORAGE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString(SONGS_KEY, null);
        Type type = new TypeToken<ArrayList<MusicFile>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public static boolean isShuffle(Context context){
        preferences = context.getSharedPreferences(AUDIO_PLAYER_STORAGE, Context.MODE_PRIVATE);
        return preferences.getBoolean(SHUFFLE_KEY, false);
    }

    public static void setShuffle(Context context){
        preferences = context.getSharedPreferences(AUDIO_PLAYER_STORAGE, Context.MODE_PRIVATE);
        boolean isShuffle = preferences.getBoolean(SHUFFLE_KEY, false);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(SHUFFLE_KEY, !isShuffle);
        editor.apply();
    }

    public static boolean isRepeat(Context context){
        preferences = context.getSharedPreferences(AUDIO_PLAYER_STORAGE, Context.MODE_PRIVATE);
        return preferences.getBoolean(REPEAT_KEY, false);
    }

    public static void setRepeat(Context context){
        preferences = context.getSharedPreferences(AUDIO_PLAYER_STORAGE, Context.MODE_PRIVATE);
        boolean isRepeating = preferences.getBoolean(REPEAT_KEY, false);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(REPEAT_KEY, !isRepeating);
        editor.apply();
    }

    public static boolean isWhichOrder(Context context){
        preferences = context.getSharedPreferences(AUDIO_PLAYER_PLAYLISTS, Context.MODE_PRIVATE);
        return preferences.getBoolean(ORDER, false);
    }

    public static void setWhichOrder(Context context){
        preferences = context.getSharedPreferences(AUDIO_PLAYER_PLAYLISTS, Context.MODE_PRIVATE);
        boolean isRepeating = preferences.getBoolean(ORDER, false);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(ORDER, !isRepeating);
        editor.apply();
    }

    public static void setPosition(int index, Context context) {
        preferences = context.getSharedPreferences(AUDIO_PLAYER_STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(POSITION_KEY, index);
        editor.apply();
    }

    public static int getPosition(Context context) {
        preferences = context.getSharedPreferences(AUDIO_PLAYER_STORAGE, Context.MODE_PRIVATE);
        return preferences.getInt(POSITION_KEY, -1);//return -1 if no data found
    }


    public static void addToPlaylist(String playlistName, MusicFile song, Context context ) {
        ArrayList<MusicFile> arrayList = getPlaylistSongs(context, playlistName);
        if (arrayList == null) arrayList = new ArrayList<>();
        arrayList.add(song);
        preferences = context.getSharedPreferences(AUDIO_PLAYER_PLAYLISTS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(arrayList);
        editor.putString(playlistName, json);
        editor.apply();
    }


    public static void createPlaylist(String playlistName, Context context ) {
        ArrayList<String> arrayList = getPlaylists(context);
        if (arrayList == null) arrayList = new ArrayList<>();
        arrayList.add(playlistName);
        preferences = context.getSharedPreferences(AUDIO_PLAYER_PLAYLISTS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(arrayList);
        editor.putString(PLAYLIST_KEY, json);
        editor.apply();
    }

    public static void setSortOrder(String sortBy, Context context ) {
        preferences = context.getSharedPreferences(AUDIO_PLAYER_PLAYLISTS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SORT_ORDER, sortBy);
        editor.apply();
    }
    public static String getSortOrder(Context context ) {
        preferences = context.getSharedPreferences(AUDIO_PLAYER_PLAYLISTS, Context.MODE_PRIVATE);
        return preferences.getString(SORT_ORDER, Globals.TITLE);
    }

    public static ArrayList<String> getPlaylists(Context context) {
        preferences = context.getSharedPreferences(AUDIO_PLAYER_PLAYLISTS, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString(PLAYLIST_KEY, null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        return gson.fromJson(json, type);
    }


    public static ArrayList<MusicFile> getPlaylistSongs(Context context, String playlistName) {
        preferences = context.getSharedPreferences(AUDIO_PLAYER_PLAYLISTS, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString(playlistName, null);
        Type type = new TypeToken<ArrayList<MusicFile>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public static void clearCachedAudioPlaylist(Context context) {
        preferences = context.getSharedPreferences(AUDIO_PLAYER_STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

}
