package io.square1.laravelConnect.model;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashMap;

import io.square1.laravelConnect.client.ApiRequest;
import io.square1.laravelConnect.client.LaravelConnectClient;
import io.square1.laravelConnect.client.results.Result;
import io.square1.laravelConnect.requests.ParamListBuilder;

/**
 * Created by roberto on 10/05/2017.
 */

public abstract class BaseModel  {

    /*package*/ HashMap<String, ModelManyRelation> mManyRelations;
    /*package*/ HashMap<String, ModelOneRelation> mRelations;
    /*package*/ HashMap<String, ModelProperty> mProperties;



    private  class InternalObserver implements LaravelConnectClient.Observer {

        private LaravelConnectClient.Observer mWrappedObserver;

        public InternalObserver(LaravelConnectClient.Observer observer){
            mWrappedObserver = observer;
        }

        @Override
        public void onRequestCompleted(Result result) {

            if(result.isSuccessful()){
                BaseModel model = (BaseModel)result.getData();
                ModelUtils.copyValues(BaseModel.this, model);
            }
            if(mWrappedObserver != null){
                mWrappedObserver.onRequestCompleted(result);
            }
        }

    }

    public final static int ID_UNSET = -1;


    private  ModelProperty<Integer> mId;

    public ModelProperty getId(){
        return mId;
    }

    void setId(int id) {
        mId.setValue(id);
    }


    public BaseModel(String primaryKey){
        mRelations = new HashMap<>();
        mManyRelations = new HashMap<>();
        mProperties = new HashMap<>();

        mId = addProperty(primaryKey, Integer.class);
        mId.setValue(ID_UNSET);
    }


    protected final ModelProperty addProperty(String name, Class<?> tClass) {
        ModelProperty newProperty = mProperties.get(name);
        if(newProperty == null) {
            newProperty = new ModelProperty(name, tClass);
            mProperties.put(name, newProperty);
        }
        return newProperty;
    }

    protected final ModelManyRelation addRelation(String name, Class<? extends BaseModel> tClass) {
        ModelManyRelation newRelation = new ModelManyRelation(this, name, tClass);
        mManyRelations.put(name,newRelation);
        return newRelation;
    }

    public int getIdValue(){
        return mId.getValue();
    }

    protected final ModelOneRelation addRelation(String name, String key, Class<? extends BaseModel> tClass) {
            ModelOneRelation relation = new ModelOneRelation(this, name, key, tClass);
            mRelations.put(key, relation);

            return  relation;
    }

    public final HashMap<String, ModelAttribute> getAttributes(){
        HashMap<String, ModelAttribute> attributeHashMap = new HashMap<>();
        attributeHashMap.putAll(mManyRelations);
        attributeHashMap.putAll(mRelations);
        attributeHashMap.putAll(mProperties);
        return attributeHashMap;
    }

    public final HashMap<String, ModelOneRelation> getOneRelations(){
        return mRelations;
    }

    public final HashMap<String, ModelManyRelation> getManyRelations(){
        return mManyRelations;
    }

    public final HashMap<String, ModelProperty> getProperties(){
        return mProperties;
    }

    public final ModelProperty getProperty(String name){
        return mProperties.get(name);
    }


    public boolean isSet(){
        return mId.getValue() != ID_UNSET;
    }

    public void refresh(LaravelConnectClient.Observer observer){

        if(isSet() == true){
            LaravelConnectClient apiClient = LaravelConnectClient.getInstance();
            apiClient.show(getClass(), mId.getValue(), new InternalObserver(observer));
        }
    }

    public void save(LaravelConnectClient.Observer observer){

        LaravelConnectClient apiClient = LaravelConnectClient.getInstance();
        if(isSet() == true){
            apiClient.update(this, new InternalObserver(observer));
        }else {
            apiClient.create(this, new InternalObserver(observer));
        }

    }


    public static ModelList index(Class<? extends BaseModel> modelClass){
        return new ModelList(modelClass);

    }



    protected static ApiRequest get(Class<? extends BaseModel> modelClass,
                                     int id, LaravelConnectClient.Observer observer){

        LaravelConnectClient apiClient = LaravelConnectClient.getInstance();
        return apiClient.show(modelClass, id, observer);

    }


    @Override
    public String toString() {
        return getClass().getSimpleName() + " Id=" + mId.getValue();
    }


    private final static String PACK_CLASS = "PACK_CLASS";
    private final  static String PACK_ID = "PACK_ID";

    public Bundle pack(){
        Bundle pack = new Bundle();
        pack.putSerializable(PACK_CLASS, getClass());
        pack.putInt(PACK_ID, getIdValue());
        return pack;
    }

    public static ApiRequest unpack(Bundle pack, LaravelConnectClient.Observer observer){
        Class modelClass = (Class) pack.getSerializable(PACK_CLASS);
        int modelId = pack.getInt(PACK_ID, 0);
        return get(modelClass, modelId, observer);
    }
}
