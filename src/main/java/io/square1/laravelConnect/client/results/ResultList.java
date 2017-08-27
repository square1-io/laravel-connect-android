package io.square1.laravelConnect.client.results;

import java.util.ArrayList;

import io.square1.laravelConnect.model.BaseModel;

/**
 * Created by roberto on 02/06/2017.
 */

public class ResultList<T extends BaseModel> extends Result{


    private Pagination mPagination;
    private ArrayList<T> mData;

    public ResultList(){
        super();
        mData = new ArrayList<>();
    }

    public  boolean isSuccessful() {
        return true;
    }


    protected void setPagination(Pagination pagination){
        mPagination = pagination;
    }
    public Pagination getPagination(){
        return mPagination;
    }

    public ArrayList<T> getData(){
        return mData;
    }


}
