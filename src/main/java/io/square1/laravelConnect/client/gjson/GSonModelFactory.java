package io.square1.laravelConnect.client.gjson;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import io.square1.laravelConnect.model.BaseModel;
import io.square1.laravelConnect.model.ModelAttribute;
import io.square1.laravelConnect.model.ModelRelation;
import io.square1.laravelConnect.model.ModelManyRelation;
import io.square1.laravelConnect.model.ModelOneRelation;
import io.square1.laravelConnect.model.ModelProperty;
import io.square1.laravelConnect.model.ModelUtils;

/**
 * Created by roberto on 26/06/2017.
 */

public class GSonModelFactory {


    public static <T extends BaseModel> T getModelInstance(Gson gson, JsonElement json, Class<T> tClass) {

        String currentkey = "";

        try {

            T model = tClass.newInstance();

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

            final String modelPrimaryKey = ModelUtils.privateKeyForModel(tClass);

            if(!jsonObject.has(modelPrimaryKey)){
                return null;
            }

            int id = jsonObject.get(modelPrimaryKey).getAsInt();
            model.getId().setValue(id);

            //loop over the attributes and set values
            final Set<Map.Entry<String, ModelAttribute>> attributesSet =  model.getAttributes()
                    .entrySet();

            //sometimes we don't get the full related model object but just the id, like user_id instead
            // of the full user object. in this case we create faulted Model Object carrying only the
            // so we can fetch it at a later stage.
            ArrayList<ModelOneRelation> faultedRelations = new ArrayList<>();

            for(Map.Entry<String, ModelAttribute> entry : attributesSet) {

                currentkey = entry.getKey();

                if("business".equalsIgnoreCase(currentkey)){
                    Log.e("","");
                }

                JsonElement jsonElement = jsonObject.get(currentkey);

                if(jsonElement instanceof com.google.gson.JsonNull){
                    Log.d("JSON", "skipping null " + tClass + " key " + currentkey) ;
                    continue;
                }
                Log.d("JSON", "Parsing " + tClass + " key " + currentkey) ;

                if(jsonElement != null){
                    //parsing basic properties
                    if(entry.getValue().getType() == BaseModel.ATTRIBUTE_PROPERTY){
                        ModelProperty modelProperty = (ModelProperty)entry.getValue();
                        Log.d("JSON", "Parsing p:" + tClass + " key " + currentkey) ;
                        Class propertyClass = modelProperty.getPropertyClass();
                        if(BaseModel.class.isAssignableFrom(propertyClass)) {
                            modelProperty.setValue(getModelInstance(gson, jsonElement, modelProperty.getPropertyClass()));
                        }else {
                            modelProperty.setValue(gson.fromJson(jsonElement, modelProperty.getPropertyClass()));
                        }
                    }
                    else if(entry.getValue().getType() == BaseModel.ATTRIBUTE_REL_ONE){

                            Log.d("JSON", "Parsing one-r:" + tClass + " key " + currentkey) ;
                            ModelOneRelation modelOneRelation = (ModelOneRelation) entry.getValue();
                            BaseModel relationObject = getModelInstance(gson,  jsonElement,
                                    modelOneRelation.getRelationClass());
                           if(relationObject != null) {
                               modelOneRelation.setValue(relationObject);
                           }else {
                               faultedRelations.add(modelOneRelation);
                           }


                    }
                    else if(entry.getValue().getType() == BaseModel.ATTRIBUTE_REL_MANY){
                        /// because the number of entries in undefined for Many relation
                        // and we are missing pagination here we ignore them here for now .
                        ModelManyRelation modelManyRelation = (ModelManyRelation)entry.getValue();
                       // modelManyRelation.clear();
                        ArrayList objects = getModelListInstance(gson,
                                (JsonArray)jsonElement ,
                                modelManyRelation.getRelationClass());

                       // modelManyRelation.addAll(objects);
                    }

                }
            }

            /// any faulted relations ?
            for(ModelOneRelation relation : faultedRelations){
                JsonElement element = jsonObject.get(relation.getPrimaryKey());
                relation.setValue(getModelInstance(gson,element, relation.getRelationClass()));
            }

            return model;
        }catch (Exception e){
            Log.e("JSON", "Error parsing " + currentkey) ;
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
