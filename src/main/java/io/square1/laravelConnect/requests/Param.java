package io.square1.laravelConnect.requests;

/**
 * Created by roberto on 11/08/2017.
 */

public class Param<T> {

    private final T mValue;
    private final String mName;
    private final String mKey;

    public Param(String name, T value){
        this(name, null, value);
    }

    public Param(String name, String key, T value){
        mKey = key;
        mName = name;
        mValue = value;
    }

    public String getName(){
        return mName;
    }

    public T getValue(){
        return mValue;
    }

    public String getKey(){
        return mKey;
    }

}
