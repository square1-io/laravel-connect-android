package io.square1.laravelConnect.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by roberto on 03/06/2017.
 */

public class ModelManyRelation<T extends BaseModel> extends ModelRelation  {


    public ModelManyRelation(BaseModel parent, String name, Class dataClass){
        super(parent, name, dataClass);
    }


    public final ModelManyRelationList list(){
        return new ModelManyRelationList(this);
    }


}
