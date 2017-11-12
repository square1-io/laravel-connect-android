package io.square1.laravelConnect.client.gjson;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.Collection;

import io.square1.laravelConnect.model.BaseModel;
import io.square1.laravelConnect.model.ModelOneRelation;
import io.square1.laravelConnect.model.ModelProperty;
import io.square1.laravelConnect.model.ModelUtils;

/**
 * Created by roberto on 26/06/2017.
 */

public class GSonModelFactory {


    public static <T extends BaseModel> T getModelInstance(Gson gson, JsonElement json, Class<T> tClass) {

        if(json == null){
            return null;
        }


        try {

            T model = tClass.newInstance();

            //sometimes we only have id and class for this object.
            if(json instanceof JsonPrimitive) {
                //we probably only have the id of the object so we just fault and set id if we can
                if(((JsonPrimitive)json).isNumber()){
                    model.getId().setValue(json.getAsInt());
                    return model;
                }

                return null;
            }

            if(!(json instanceof JsonObject)) {
                return null;
            }

            //set the Id
            JsonObject jsonObject = (JsonObject)json;

            final String modelPrimaryKey = ModelUtils.primaryKeyForModel(tClass);

            if(!jsonObject.has(modelPrimaryKey)){
                return null;
            }

            int id = jsonObject.get(modelPrimaryKey).getAsInt();
            model.getId().setValue(id);

            // 1) Loop over properties and set values :

            Collection<ModelProperty> properties = model.getProperties().values();

            for(ModelProperty property : properties){
                //see if we have a values for this property now
                JsonElement jsonElement = jsonObject.get(property.getName());

                //no value for this propery skip then
                if(jsonElement == null || jsonElement instanceof com.google.gson.JsonNull){
                    continue;
                }

                //what class is this property ?
                Class propertyClass = property.getDataClass();

                //is this property a relation to a model ? This shouldn't happen as
                // this should be a ModelOneRelation!
                if(BaseModel.class.isAssignableFrom(propertyClass)) {
                    throw new IllegalStateException( property.getName() + " with class "+ propertyClass  + " should be a relation instead ");
                }
                else {
                    property.setValue(gson.fromJson(jsonElement, propertyClass));
                }
            }

            //2) loop over single relations

            Collection<ModelOneRelation> oneRelations = model.getOneRelations().values();

            for(ModelOneRelation relation : oneRelations){

                //this can go two ways :
                //a) we have the full object
                JsonElement jsonElement = jsonObject.get(relation.getName());
                if(jsonElement != null  && !(jsonElement instanceof com.google.gson.JsonNull)){
                    //we have the data
                    BaseModel relatedModel = getModelInstance(gson, jsonElement, relation.getDataClass());
                    relation.setValue(relatedModel);
                }else { // b, we only have the id , like user_id for example instead of having the full user object

                     jsonElement = jsonObject.get(relation.getPrimaryKey());
                    if(jsonElement != null  && !(jsonElement instanceof com.google.gson.JsonNull)){
                        // we create an emply relation object and we only set the id.
                        BaseModel relatedModel = getModelInstance(gson, jsonElement, relation.getDataClass());
                        relation.setValue(relatedModel);

                    }
                }
            }

            //3) loop over many relations, for now we leave this as if there are too many items it
            //is not good to return them all from the API
           // Collection<ModelManyRelation> manyRelations = model.getManyRelations().values();

            return model;

        }catch (Exception e){
            Log.e("JSON", "Error parsing  " + tClass.getSimpleName() + " from " + json ) ;
            e.printStackTrace();
        }

        return null;
    }

    public static <T extends BaseModel> ArrayList<T> getModelListInstance(Gson gson, JsonArray jsonArray, Class<T> tClass){

        ArrayList arrayList = new ArrayList();

        for(JsonElement element : jsonArray){
            BaseModel model = getModelInstance(gson, element, tClass);
            arrayList.add(model);
        }

        return arrayList;
    }
}
