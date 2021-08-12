package com.alle.san.musicplayer.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

public class FolderModel extends MyModels implements Parcelable{
    File name;

    public FolderModel(File name, String pic1, String pic2, String pic3, String pic4){
        this.name = name;
        this.pic1 = pic1;
        this.pic3 = pic3;
        this.pic2 = pic2;
        this.pic4 = pic4;
    }

    public FolderModel() {
    }


    protected FolderModel(Parcel in) {
        pic1 = in.readString();
        pic2 = in.readString();
        pic3 = in.readString();
        pic4 = in.readString();
    }

    public static final Creator<FolderModel> CREATOR = new Creator<FolderModel>() {
        @Override
        public FolderModel createFromParcel(Parcel in) {
            return new FolderModel(in);
        }

        @Override
        public FolderModel[] newArray(int size) {
            return new FolderModel[size];
        }
    };

    public File getName() {
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(pic1);
        dest.writeString(pic2);
        dest.writeString(pic3);
        dest.writeString(pic4);
    }
}
