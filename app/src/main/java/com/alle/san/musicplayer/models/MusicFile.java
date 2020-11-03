package com.alle.san.musicplayer.models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class MusicFile implements Parcelable {
    String title;
    String album;
    String data;
    String artist;
    int duration;
    Bitmap albumImage;

    protected MusicFile(Parcel in) {
        title = in.readString();
        album = in.readString();
        data = in.readString();
        artist = in.readString();
        duration = in.readInt();
        albumImage = in.readParcelable(Bitmap.class.getClassLoader());
        playlist = in.createStringArray();
    }

    public static final Creator<MusicFile> CREATOR = new Creator<MusicFile>() {
        @Override
        public MusicFile createFromParcel(Parcel in) {
            return new MusicFile(in);
        }

        @Override
        public MusicFile[] newArray(int size) {
            return new MusicFile[size];
        }
    };

    public String[] getPlaylist() {
        return playlist;
    }

    public void setPlaylist(String[] playlist) {
        this.playlist = playlist;
    }

    String[] playlist;

    public MusicFile() {
    }

    public MusicFile(String title, String album, String data, String artist, int duration) {
        this.title = title;
        this.album = album;
        this.data = data;
        this.artist = artist;
        this.duration = duration;
    }

    public MusicFile(String title, String album, int duration, String artist, String data, Bitmap albumImage) {
        this.title = title;
        this.album = album;
        this.data = data;
        this.artist = artist;
        this.duration = duration;
        this.albumImage = albumImage;
    }



    public Bitmap getAlbumImage() {
        return albumImage;
    }

    public void setAlbumImage(Bitmap albumImage) {
        this.albumImage = albumImage;
    }

    public String getTitle() {
        return title;
    }

    public String getAlbum() {
        return album;
    }

    public String getData() {
        return data;
    }

    public String getArtist() {
        return artist;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return "{\n title: '" + title + '\'' +
                ", album: '" + album + '\'' +
                ", data: '" + data + '\'' +
                ", artist: '" + artist + '\'' +
                ", duration: '" + duration + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(album);
        parcel.writeString(data);
        parcel.writeString(artist);
        parcel.writeInt(duration);
        parcel.writeParcelable(albumImage, i);
        parcel.writeStringArray(playlist);
    }
}
