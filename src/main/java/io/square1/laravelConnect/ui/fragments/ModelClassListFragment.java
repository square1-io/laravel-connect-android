package io.square1.laravelConnect.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import io.square1.laravelConnect.R;
import io.square1.laravelConnect.model.ModelUtils;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnModelClassListFragmentInteractionListener}
 * interface.
 */
public class ModelClassListFragment extends Fragment {

    public static final String TAG = ModelClassListFragment.class.getName();

    private static final String ARG_PACKAGE_NAME = "ARG_PACKAGE_NAME";


    private OnModelClassListFragmentInteractionListener mListener;


    public ModelClassListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ModelClassListFragment newInstance(String packageName) {
        ModelClassListFragment fragment = new ModelClassListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PACKAGE_NAME, packageName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.laravel_connect_fragment_model_class_list, container, false);

        String packageName = getArguments().getString(ARG_PACKAGE_NAME);

        TextView title = (TextView)view.findViewById(R.id.title);
        title.setText(packageName);

        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));


        ArrayList<Class> classArrayList = ModelUtils.getClassesForPackage(getContext(), packageName);
        recyclerView.setAdapter(new ModelClassRecyclerViewAdapter(classArrayList, mListener));

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnModelClassListFragmentInteractionListener) {
            mListener = (OnModelClassListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnModelClassListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public interface OnModelClassListFragmentInteractionListener {

        void onListFragmentInteraction(Class item);
    }
}
