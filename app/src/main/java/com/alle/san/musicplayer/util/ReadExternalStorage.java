package com.alle.san.musicplayer.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;

import com.alle.san.musicplayer.models.MusicFile;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

public class ReadExternalStorage {
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
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]{
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
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                int duration = Integer.parseInt(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));

                String title = delimitTitle(songName) ;
                MusicFile musicFile = new MusicFile(title, album, data, artist, duration);
                musicFiles.add(musicFile);

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
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageArt, 0, imageArt.length);
            return bitmap;
        }else {
            return null;
        }
    }

    public static String delimitTitle(String songName){
        String newTitle = "";
        if(songName.contains("(")){
            String[] splitRes = songName.split(" ",15);
            StringBuilder buildSongName = new StringBuilder();
            for (String splitRe : splitRes) {
                if (splitRe.contains("(")) {
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
        }else {
            newTitle = songName;
        }
        return  newTitle;
    }

}
