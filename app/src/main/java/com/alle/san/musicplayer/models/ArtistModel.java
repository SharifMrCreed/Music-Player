package com.alle.san.musicplayer.models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class ArtistModel implements Parcelable {
    String name;
    String pic1;
    String pic2;
    String pic3;
    String pic4;

    public ArtistModel(String name, String pic1, String pic2, String pic3, String pic4){
        this.name = name;
        this.pic1 = pic1;
        this.pic3 = pic3;
        this.pic2 = pic2;
        this.pic4 = pic4;
    }

    public ArtistModel() {
    }


    protected ArtistModel(Parcel in) {
        name = in.readString();
        pic1 = in.readString();
        pic2 = in.readString();
        pic3 = in.readString();
        pic4 = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(pic1);
        dest.writeString(pic2);
        dest.writeString(pic3);
        dest.writeString(pic4);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ArtistModel> CREATOR = new Creator<ArtistModel>() {
        @Override
        public ArtistModel createFromParcel(Parcel in) {
            return new ArtistModel(in);
        }

        @Override
        public ArtistModel[] newArray(int size) {
            return new ArtistModel[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getPic4() {
        return pic4;
    }

    public String getPic1() {
        return pic1;
    }

    public String getPic2() {
        return pic2;
    }

    public String getPic3() {
        return pic3;
    }


}
