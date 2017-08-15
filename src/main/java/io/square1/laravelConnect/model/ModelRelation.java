package io.square1.laravelConnect.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

/**
 * Created by roberto on 26/06/2017.
 */

public abstract class ModelRelation extends ModelAttribute {

    public static final String PARENT_CLASS = "PARENT_CLASS";
    public static final String PARENT_ID = "PARENT_ID";

    private BaseModel mParent;

    public ModelRelation(BaseModel parent, String name, int type){
        super(name, type);
        mParent = parent;
    }

    public final BaseModel getParent(){
        return mParent;
    }


    @Override
    public HashMap<String, String> pack(){
        HashMap<String,String> pack = super.pack();
        pack.put(PARENT_CLASS, String.valueOf(mParent.getClass()));
        pack.put(PARENT_ID, String.valueOf(mParent.getIdValue()));
        return pack;
    }


}
