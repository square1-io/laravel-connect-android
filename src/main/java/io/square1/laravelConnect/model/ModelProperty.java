package io.square1.laravelConnect.model;

import android.os.Parcel;

import java.util.HashMap;

/**
 * Created by roberto on 25/06/2017.
 */

public class ModelProperty<T> extends ModelAttribute {

    public static final String PROPERTY_VALUE = "VALUE";

    private T mValue;



    public ModelProperty(String name, Class<T> type){
        super(name, type);

    }


    public void setValue(T value){
        mValue = value;
        setChanged(true);
    }

    public T getValue(){
        return mValue;
    }


    @Override
    public HashMap<String, String> pack(){
        HashMap<String,String> pack = super.pack();
        pack.put(PROPERTY_VALUE, String.valueOf(mValue));
        return pack;
    }

}
