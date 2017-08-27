package io.square1.laravelConnect.ui.adapters;

import android.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;



import java.util.ArrayList;
import java.util.List;

import io.square1.laravelConnect.R;
import io.square1.laravelConnect.model.BaseModel;
import io.square1.laravelConnect.model.ModelProperty;
import io.square1.laravelConnect.ui.fragments.BaseModelListFragment;


public class BaseModelRecyclerViewAdapter extends RecyclerView.Adapter<BaseModelRecyclerViewAdapter.ViewHolder> {


    public interface PresentBaseModelClass<T extends BaseModel> {

        public String getName(T model);
        public String getId(T model);
    }



    private final ArrayList<BaseModel> mValues;
    private final Fragment mFragment;
    private final BaseModelListFragment.OnListFragmentInteractionListener mListener;
    private PresentBaseModelClass mPresentBaseModelClass = null;
    private PresentBaseModelClass mInternalBaseModelClass = new PresentBaseModelClass() {

        @Override
        public String getName(BaseModel model) {
            ModelProperty property = model.getProperty("name");
            if(property == null){
                property = model.getProperty("content");
            }
            return property != null ? String.valueOf(property.getValue()) : String.valueOf(model);
        }

        @Override
        public String getId(BaseModel model) {
            return String.valueOf(model.getId().getValue());
        }
    };

    public BaseModelRecyclerViewAdapter(Fragment fragment, BaseModelListFragment.OnListFragmentInteractionListener listener) {
        mValues = new ArrayList<>();
        mListener = listener;
        mFragment = fragment;
        mPresentBaseModelClass = mInternalBaseModelClass;

    }

    public void setPresentBaseModelClass(PresentBaseModelClass presentBaseModelClass){
        if(presentBaseModelClass != null) {
            mPresentBaseModelClass = presentBaseModelClass;
        }else {
            mPresentBaseModelClass = mInternalBaseModelClass;
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.laravel_connect_base_model_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mPresentBaseModelClass.getId(mValues.get(position)));
        holder.mContentView.setText(mPresentBaseModelClass.getName(mValues.get(position)));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem, mFragment );
                }
            }
        });
    }

    public void clear() {
        mValues.clear();
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void addItems(List data) {
        mValues.addAll(data);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public BaseModel mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
