package io.square1.laravelConnect.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

import okhttp3.FormBody;
import okhttp3.MultipartBody;

/**
 * Created by roberto on 16/04/2017.
 */

public class UploadedFile implements Parcelable {

    private String mUrl;




    public UploadedFile(String url){
        mUrl = url;


    }

    public String getUrl(){
        return mUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mUrl);
    }

    protected UploadedFile(Parcel in) {
        this.mUrl = in.readString();
    }

    public static final Parcelable.Creator<UploadedFile> CREATOR = new Parcelable.Creator<UploadedFile>() {
        @Override
        public UploadedFile createFromParcel(Parcel source) {
            return new UploadedFile(source);
        }

        @Override
        public UploadedFile[] newArray(int size) {
            return new UploadedFile[size];
        }
    };
}
