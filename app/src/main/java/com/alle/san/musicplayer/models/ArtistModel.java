package com.alle.san.musicplayer.models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class ArtistModel implements Parcelable {
    String name;
    Bitmap pic1;
    Bitmap pic2;
    Bitmap pic3;
    Bitmap pic4;

    public ArtistModel(String name, Bitmap pic1, Bitmap pic2, Bitmap pic3, Bitmap pic4){
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
        pic1 = in.readParcelable(Bitmap.class.getClassLoader());
        pic2 = in.readParcelable(Bitmap.class.getClassLoader());
        pic3 = in.readParcelable(Bitmap.class.getClassLoader());
        pic4 = in.readParcelable(Bitmap.class.getClassLoader());
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

    public Bitmap getPic4() {
        return pic4;
    }

    public Bitmap getPic1() {
        return pic1;
    }

    public Bitmap getPic2() {
        return pic2;
    }

    public Bitmap getPic3() {
        return pic3;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeParcelable(pic1, flags);
        dest.writeParcelable(pic2, flags);
        dest.writeParcelable(pic3, flags);
        dest.writeParcelable(pic4, flags);
    }
}
