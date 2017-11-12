package io.square1.laravelConnect.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

/**
 * Created by roberto on 06/08/2017.
 */

public abstract class ModelAttribute  {

    public static final String  ATTR_NAME = "NAME";
    public static final String ATTR_CLASS = "CLASS";


    private String mName;
    private Class mDataClass;

    private boolean mChanged;

    public ModelAttribute(String name, Class dataClass){
        mName = name;
        mDataClass = dataClass;
        mChanged = false;
    }

    public final String getName(){
        return mName;
    }

    public final Class getDataClass(){
        return mDataClass;
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
        pack.put(ATTR_CLASS, mDataClass.getName());
        return pack;
    }

}
