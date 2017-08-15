package io.square1.laravelConnect.client;

import android.os.Parcel;
import android.os.Parcelable;

import io.square1.laravelConnect.model.ModelAttribute;
import io.square1.laravelConnect.model.ModelProperty;
import io.square1.laravelConnect.model.ModelRelation;

/**
 * Created by roberto on 27/06/2017.
 */

public class Sort implements Parcelable {

    public static final int ASC = 1;
    public static final int DESC = 2;

    private String mName;
    private String mValue;
    private int mOrder;

    public Sort(ModelProperty attribute, int sort){
        this(attribute.getName(), attribute.getName(), ASC);
    }

    public Sort(String name, String value, int sort){
        mName = name;
        mValue = value;
        mOrder = sort;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mName);
        dest.writeString(this.mValue);
        dest.writeInt(this.mOrder);
    }

    protected Sort(Parcel in) {
        this.mName = in.readString();
        this.mValue = in.readString();
        this.mOrder = in.readInt();
    }

    public static final Parcelable.Creator<Sort> CREATOR = new Parcelable.Creator<Sort>() {
        @Override
        public Sort createFromParcel(Parcel source) {
            return new Sort(source);
        }

        @Override
        public Sort[] newArray(int size) {
            return new Sort[size];
        }
    };
}
