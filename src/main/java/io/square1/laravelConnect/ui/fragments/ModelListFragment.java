package io.square1.laravelConnect.ui.fragments;



import android.os.Bundle;

import io.square1.laravelConnect.client.ApiRequest;
import io.square1.laravelConnect.model.ModelList;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ModelListFragment extends BaseModelListFragment {


    private static final String ARG_LIST = "ARG_LIST";

    public final static ModelListFragment getInstance(ModelList list){
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_LIST, list);
        ModelListFragment fragment = new ModelListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private ModelList mModelList;




    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ModelListFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModelList = getArguments().getParcelable(ARG_LIST);
    }

    @Override
    public ApiRequest loadPage(int page, int pageSize) {
        return mModelList.next(this);
    }

    @Override
    public void onFilterContent(String searchTerm) {

    }


}
