package io.square1.laravelConnect.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Date;

import io.square1.laravelConnect.R;
import io.square1.laravelConnect.model.BaseModel;
import io.square1.laravelConnect.model.ModelAttribute;
import io.square1.laravelConnect.model.ModelManyRelation;
import io.square1.laravelConnect.model.ModelOneRelation;
import io.square1.laravelConnect.model.ModelProperty;
import io.square1.laravelConnect.ui.fragments.BaseModelListFragment;


public class ModelDetailsRecyclerViewAdapter extends RecyclerView.Adapter<ModelAttributeViewHolder> {


    public final static int VIEW_TYPE_INT = 10;
    public final static int VIEW_TYPE_DOUBLE = 11;
    public final static int VIEW_TYPE_DATE = 12;
    public final static int VIEW_TYPE_STRING = 13;
    public final static int VIEW_TYPE_ONE_RELATION = 14;
    public final static int VIEW_TYPE_MANY_RELATION = 15;

    public interface OnModelAttributeInteractionListener {
        void onModelAttributeSelected(BaseModel parent, ModelAttribute attribute);
    }

    private BaseModel mModel;
    private ArrayList<ModelAttribute> mModelAttributes;


    private final OnModelAttributeInteractionListener mListener;


    public ModelDetailsRecyclerViewAdapter(OnModelAttributeInteractionListener listener) {
        mListener = listener;
        mModelAttributes = new ArrayList<>();
    }


    @Override
    public ModelAttributeViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.laravel_connect_string_property_list_item, parent, false);

        ModelAttributeViewHolder holder = new ModelAttributeViewHolder(view);

        switch (viewType) {

            case VIEW_TYPE_INT: {

            }
            break;

            case VIEW_TYPE_DOUBLE: {

            }
            break;

            case VIEW_TYPE_DATE: {

            }
            break;

            case VIEW_TYPE_ONE_RELATION: {

            }
            break;

            case VIEW_TYPE_MANY_RELATION: {

            }
            break;

            default:
            case VIEW_TYPE_STRING: {

            }
        }

        return holder;
    }

    @Override
    public int getItemViewType(int position) {


        ModelAttribute attribute = mModelAttributes.get(position);

        if (attribute instanceof ModelProperty) {

            ModelProperty property = (ModelProperty) attribute;

            if (property.getDataClass() == Integer.class) {
                return VIEW_TYPE_INT;
            } else if (property.getDataClass() == Double.class) {
                return VIEW_TYPE_DOUBLE;
            } else if (property.getDataClass() == Date.class) {
                return VIEW_TYPE_DATE;
            } else {
                return VIEW_TYPE_STRING;
            }

        } else if (attribute instanceof ModelOneRelation) {
            return VIEW_TYPE_ONE_RELATION;
        } else if (attribute instanceof ModelManyRelation) {
            return VIEW_TYPE_MANY_RELATION;
        }

        return VIEW_TYPE_STRING;

    }

    public void setModel(BaseModel model) {
        mModel = model;
        mModelAttributes.clear();
        mModelAttributes.addAll(model.getAttributes().values());
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final ModelAttributeViewHolder holder, int position) {

         ModelAttribute property = mModelAttributes.get(position);
         holder.setAttribute(property);

//        holder.mItem = mValues.get(position);
//        holder.mIdView.setText(mPresentBaseModelClass.getId(mValues.get(position)));
//        holder.mContentView.setText(mPresentBaseModelClass.getName(mValues.get(position)));
//
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onModelAttributeSelected(mModel, holder.getAttribute());
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return mModelAttributes.size();
    }
}



