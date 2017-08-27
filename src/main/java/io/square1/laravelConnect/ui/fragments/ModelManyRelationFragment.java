package io.square1.laravelConnect.ui.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import io.square1.laravelConnect.client.ApiRequest;
import io.square1.laravelConnect.model.Criteria;
import io.square1.laravelConnect.model.Filter;
import io.square1.laravelConnect.model.ModelManyRelationList;
import io.square1.laravelConnect.model.ModelProperty;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ModelManyRelationFragment extends BaseModelListFragment {

    public static final String TAG = ModelManyRelationFragment.class.getName();

    private static final String ARG_RELATION = "ARG_RELATION";

    public static ModelManyRelationFragment getInstance(ModelManyRelationList relation){
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_RELATION, relation);
        ModelManyRelationFragment fragment = new ModelManyRelationFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private ModelManyRelationList mModelManyRelationList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ModelManyRelationFragment() {
        super();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setTitle(mModelManyRelationList.toString());
    }

    @Override
    public void initRequest() {
        mModelManyRelationList = getArguments().getParcelable(ARG_RELATION);
    }

    @Override
    public void onFilterContent(String searchTerm) {

        if(TextUtils.isEmpty(searchTerm) == false) {
            ModelProperty<String> nameFilter = new ModelProperty("name", String.class);
            nameFilter.setValue(searchTerm);
            Filter filter = new Filter().addCriteria(Criteria.contains(nameFilter));
            mModelManyRelationList = mModelManyRelationList.buildUpon().filter(filter).build();
        }else {
            mModelManyRelationList = mModelManyRelationList.buildUpon().filter(null).build();
        }
        refresh();

    }

    @Override
    public ApiRequest loadPage(int page, int pageSize) {
        return mModelManyRelationList.next(this);
    }



}
