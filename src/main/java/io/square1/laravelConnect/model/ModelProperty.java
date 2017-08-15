package io.square1.laravelConnect.model;

import android.os.Parcel;

import java.util.HashMap;

/**
 * Created by roberto on 25/06/2017.
 */

public class ModelProperty<T> extends ModelAttribute {

    public static final String PROPERTY_VALUE = "PROPERTY_VALUE";
    public static final String PROPERTY_TYPE = "PROPERTY_TYPE";

    private T mValue;
    private Class<T> mType;


    public ModelProperty(String name, Class<T> type){
        super(name, BaseModel.ATTRIBUTE_PROPERTY);
        mType = type;

    }


    public Class<T> getPropertyClass(){
        return mType;
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
        pack.put(PROPERTY_TYPE, String.valueOf(mType));
        pack.put(PROPERTY_VALUE, String.valueOf(mValue));
        return pack;
    }

}
