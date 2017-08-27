package io.square1.laravelConnect.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import io.square1.laravelConnect.R;
import io.square1.laravelConnect.model.BaseModel;
import io.square1.laravelConnect.model.ModelAttribute;
import io.square1.laravelConnect.model.ModelManyRelation;
import io.square1.laravelConnect.model.ModelOneRelation;
import io.square1.laravelConnect.model.ModelProperty;

/**
 * Created by roberto on 15/08/2017.
 */

public  class ModelAttributeViewHolder extends RecyclerView.ViewHolder {

    private ModelAttribute mAttribute;
    private TextView mName;
    private TextView mType;
    private TextView mValue;

    public ModelAttributeViewHolder(View itemView) {
        super(itemView);
        mName = (TextView)itemView.findViewById(R.id.name);
        mType = (TextView)itemView.findViewById(R.id.type);
        mValue = (TextView)itemView.findViewById(R.id.value);
    }

    public void setAttribute(ModelAttribute attribute){
        mAttribute = attribute;
        mName.setText(mAttribute.getName());
        mType.setText(parseType(mAttribute));
        mValue.setText(parseValue(mAttribute));
    }


    public ModelAttribute getAttribute(){
        return mAttribute;
    }

    private String parseType(ModelAttribute attribute){

        if(attribute instanceof ModelProperty) {
            Object value = ((ModelProperty)attribute).getValue();
            return value != null ? value.getClass().getSimpleName() : "null";
        }

        if(attribute instanceof ModelOneRelation) {
            ModelOneRelation relation = ((ModelOneRelation)attribute);
            return "One relation to " + relation.getRelationClass().getSimpleName();
        }

        if(attribute instanceof ModelManyRelation) {
            ModelManyRelation relation = ((ModelManyRelation)attribute);
            return "Many relation to " + relation.getRelationClass().getSimpleName();
        }

        return "none";

    }

    private String parseValue(ModelAttribute attribute){

        if(attribute instanceof ModelProperty) {

            Object value = ((ModelProperty)attribute).getValue();

            if(value instanceof BaseModel){
                return String.valueOf(((BaseModel)value).getIdValue());
            }
            
            return String.valueOf(value);
        }

        if(attribute instanceof ModelOneRelation) {
            ModelOneRelation relation = ((ModelOneRelation)attribute);
            return "One relation to " + relation.getRelationClass().getSimpleName();
        }

        if(attribute instanceof ModelManyRelation) {
            ModelManyRelation relation = ((ModelManyRelation)attribute);
            return "Many relation to " + relation.getRelationClass().getSimpleName();
        }

        return "none";

    }



}
