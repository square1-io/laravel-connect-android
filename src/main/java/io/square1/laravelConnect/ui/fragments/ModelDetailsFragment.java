package io.square1.laravelConnect.ui.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import io.square1.laravelConnect.R;
import io.square1.laravelConnect.client.ApiRequest;
import io.square1.laravelConnect.client.LaravelConnectClient;
import io.square1.laravelConnect.client.results.Pagination;
import io.square1.laravelConnect.client.results.Result;
import io.square1.laravelConnect.client.results.ResultList;
import io.square1.laravelConnect.client.results.ResultObj;
import io.square1.laravelConnect.model.BaseModel;
import io.square1.laravelConnect.model.ModelList;
import io.square1.laravelConnect.ui.adapters.BaseModelRecyclerViewAdapter;
import io.square1.laravelConnect.ui.adapters.ModelDetailsRecyclerViewAdapter;

/**
 * Created by roberto on 16/08/2017.
 */

public class ModelDetailsFragment extends Fragment implements LaravelConnectClient.Observer {

    public static final String TAG = ModelDetailsFragment.class.getName();

    private TextView mTitle;
    private RecyclerView mRecyclerView;

    private ModelDetailsRecyclerViewAdapter.OnModelAttributeInteractionListener mListener;
    private ModelDetailsRecyclerViewAdapter mModelDetailsRecyclerViewAdapter;
    private ApiRequest mRequest;

    public final static ModelDetailsFragment getInstance(BaseModel model){
        Bundle bundle = model.pack();
        ModelDetailsFragment fragment = new ModelDetailsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ModelClassListFragment.OnModelClassListFragmentInteractionListener) {
            mListener = (ModelDetailsRecyclerViewAdapter.OnModelAttributeInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnModelClassListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(mRequest != null){
            mRequest.cancel();
        }
        mListener = null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle pack = getArguments();
        mRequest = BaseModel.unpack(pack, this);

        mModelDetailsRecyclerViewAdapter = new ModelDetailsRecyclerViewAdapter(mListener);
        mRecyclerView.setAdapter(mModelDetailsRecyclerViewAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.laravel_connect_fragment_model_list, container, false);

        mTitle = (TextView) view.findViewById(R.id.name);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.list);

        Context context = view.getContext();
        LinearLayoutManager mngr = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mngr);


        return view;
    }

    private void  modelRequestCompleted(BaseModel baseModel){
        mTitle.setText(String.valueOf(baseModel));
        mModelDetailsRecyclerViewAdapter.setModel(baseModel);
    }

    @Override
    public void onRequestCompleted(Result result) {
        mRequest = null;

        if(result.isSuccessful()){
            if(result instanceof ResultObj) {
                modelRequestCompleted(((ResultObj) result).getData());
            }
        }else if(getContext() != null) {

            Toast.makeText(getContext(),
                    "there was an error with the request",
                    Toast.LENGTH_LONG)
                    .show();
        }

    }
}
