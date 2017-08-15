package io.square1.laravelConnect.model;

/**
 * Created by roberto on 05/06/2017.
 */

public class ModelOneRelation<T extends BaseModel> extends ModelRelation {


    private String mPrimaryKey;
    private T mRelation;
    private Class mRelationClass;

    public ModelOneRelation(BaseModel parent, String name, String primaryKey, Class relationClass){
        super(parent, name, BaseModel.ATTRIBUTE_REL_ONE);
        mPrimaryKey = primaryKey;
        mRelationClass = relationClass;
        //mRelation = model;
    }

    public final String getPrimaryKey(){
        return mPrimaryKey;
    }

    public final Class getRelationClass(){
        return mRelationClass;
    }

    public final T getValue(){
        return mRelation;
    }

    public void setValue(T value){
        mRelation = value;
    }

    @Override
    public String toString() {
        return "->" + ((mRelation != null) ? mRelation.toString() : mRelationClass.getSimpleName());
    }
}
