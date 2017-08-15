package io.square1.laravelConnect.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

/**
 * Created by roberto on 06/08/2017.
 */

public abstract class ModelAttribute  {

    public static final String  ATTR_NAME = "NAME";
    public static final String ATTR_TYPE = "ATTR_TYPE";


    private String mName;
    private final int mType;

    private boolean mChanged;

    public ModelAttribute(String name, int type){
        mType = type;
        mName = name;
        mChanged = false;
    }

    public int getType(){
        return mType;
    }

    public final String getName(){
        return mName;
    }

    public final boolean getChanged(){
        return mChanged;
    }

    void setChanged(boolean changed){
        mChanged = changed;
    }


    public HashMap<String, String> pack(){
        HashMap<String,String> pack = new HashMap<>();
        pack.put(ATTR_NAME, mName);
        pack.put(ATTR_TYPE, String.valueOf(mType));
        return pack;
    }

}
