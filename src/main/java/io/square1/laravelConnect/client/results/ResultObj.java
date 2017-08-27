package io.square1.laravelConnect.client.results;

import io.square1.laravelConnect.model.BaseModel;


/**
 * Created by roberto on 02/06/2017.
 */

public class ResultObj<T extends BaseModel> extends Result {


    private T mData;

    public void setData(T data){
         mData = data;
    }

    public  boolean isSuccessful() {
        return mData != null;
    }

    public T getData(){
        return mData;
    }
}
