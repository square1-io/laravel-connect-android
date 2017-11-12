package io.square1.laravelConnect.model;

/**
 * Created by roberto on 05/06/2017.
 */

public class ModelOneRelation<T extends BaseModel> extends ModelRelation {


    private String mPrimaryKey;
    private T mRelation;


    public ModelOneRelation(BaseModel parent, String name, String primaryKey, Class relationClass){
        super(parent, name, relationClass);
        mPrimaryKey = primaryKey;

    }

    public final String getPrimaryKey(){
        return mPrimaryKey;
    }



    public final T getValue(){
        return mRelation;
    }

    public void setValue(T value){
        mRelation = value;
    }

    @Override
    public String toString() {
        return "->" + ((mRelation != null) ? mRelation.toString() : getDataClass().getSimpleName());
    }
}
