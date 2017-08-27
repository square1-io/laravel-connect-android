package io.square1.laravelConnect.ui.fragments;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import io.square1.laravelConnect.model.BaseModel;
import io.square1.laravelConnect.ui.adapters.BaseModelRecyclerViewAdapter;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public abstract class BaseModelListFragment extends Fragment implements LaravelConnectClient.Observer, SearchView.OnQueryTextListener {


    private TextView mTitle;
    private RecyclerView mRecyclerView;
    private BaseModelRecyclerViewAdapter mAdapter;
    private ApiRequest mRequest;
    private String mSearchTerm;
    private Pagination mCurrentPage = Pagination.NOPAGE;
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BaseModelListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.laravel_connect_model_list_menu, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);

        super.onCreateOptionsMenu(menu, inflater);
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
        mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListenerInternal(mngr));
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    public void setTitle(String title){
        if(mTitle != null){
            mTitle.setText(title);
        }
    }

    public void notifyDataSetChanged(){
        mAdapter.notifyDataSetChanged();
    }

    public String getSearchTerm(){
        return mSearchTerm;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
            mAdapter = new BaseModelRecyclerViewAdapter(this, mListener);
            mAdapter.setPresentBaseModelClass(getModelClassPresenter());
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnModelClassListFragmentInteractionListener");
        }


    }

    public BaseModelRecyclerViewAdapter.PresentBaseModelClass getModelClassPresenter() {
        return null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        refresh();
    }

    public abstract void initRequest();

    public void refresh(){
        initRequest();
        mCurrentPage = Pagination.NOPAGE;
        load();
    }

    private ApiRequest load(){
        if(mRequest != null){
            mRequest.cancel();
        }

        return mRequest = loadPage(mCurrentPage.next(), mCurrentPage.getPageSize());
    }

    public abstract ApiRequest loadPage(int page, int pageSize);

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if(mRequest != null){
            mRequest.cancel();
            mRequest = null;
        }

        mListener = null;
    }

    @Override
    public void onRequestCompleted(Result result) {
        mRequest = null;

        if(result.isSuccessful()){
            if(result instanceof ResultList) {
                onListRequestCompleted((ResultList) result);
            }
        }else if(getContext() != null) {

            Toast.makeText(getContext(),
                    "there was an error with the request",
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void onListRequestCompleted(ResultList resultList){
        mCurrentPage = resultList.getPagination();
        if(mCurrentPage.isFirstPage() == true){
            mAdapter.clear();
        }
        mAdapter.addItems(resultList.getData());
    }

    public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
        if(mCurrentPage.hasNext() == true) {
            loadPage(mCurrentPage.next(), mCurrentPage.getPageSize());
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(BaseModel model, Fragment originFragment);
    }


    @Override
    public boolean onQueryTextChange(String query) {
        // Here is where we are going to implement the filter logic
        mSearchTerm = query;
        onFilterContent(query);
        refresh();
        return false;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        mSearchTerm = query;
        return false;
    }


    public abstract void onFilterContent(String searchTerm);


    private class EndlessRecyclerViewScrollListenerInternal extends EndlessRecyclerViewScrollListener {

        public EndlessRecyclerViewScrollListenerInternal(LinearLayoutManager layoutManager) {
            super(layoutManager);
        }


        @Override
        public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
            BaseModelListFragment.this.onLoadMore(page, totalItemsCount, view);
        }
    };
}
