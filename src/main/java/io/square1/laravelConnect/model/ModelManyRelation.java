package io.square1.laravelConnect.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by roberto on 03/06/2017.
 */

public class ModelManyRelation<T extends BaseModel> extends ModelRelation  {


    private Class<T> mRelationClass;


    public ModelManyRelation(BaseModel parent, String name, Class modelType){
        super(parent, name, BaseModel.ATTRIBUTE_REL_MANY);
        mRelationClass = modelType;
    }


    public Class<T> getRelationClass(){
        return mRelationClass;
    }

    public final ModelManyRelationList list(){
        return new ModelManyRelationList(this);
    }


}
