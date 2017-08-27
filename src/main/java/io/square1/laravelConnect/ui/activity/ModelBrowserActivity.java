package io.square1.laravelConnect.ui.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import io.square1.laravelConnect.R;
import io.square1.laravelConnect.model.BaseModel;
import io.square1.laravelConnect.model.ModelAttribute;
import io.square1.laravelConnect.model.ModelList;
import io.square1.laravelConnect.model.ModelManyRelation;
import io.square1.laravelConnect.model.ModelOneRelation;
import io.square1.laravelConnect.ui.adapters.ModelDetailsRecyclerViewAdapter;
import io.square1.laravelConnect.ui.fragments.BaseModelListFragment;
import io.square1.laravelConnect.ui.fragments.ModelClassListFragment;
import io.square1.laravelConnect.ui.fragments.ModelDetailsFragment;
import io.square1.laravelConnect.ui.fragments.ModelListFragment;
import io.square1.laravelConnect.ui.fragments.ModelManyRelationFragment;

public class ModelBrowserActivity extends AppCompatActivity implements BaseModelListFragment.OnListFragmentInteractionListener,
        ModelClassListFragment.OnModelClassListFragmentInteractionListener,
        ModelDetailsRecyclerViewAdapter.OnModelAttributeInteractionListener{

    public static final String EXTRA_PACKAGE = "EXTRA_PACKAGE";

    public final static void show(Activity activity, String packageName){
        Intent showActivity = new Intent(activity, ModelBrowserActivity.class);
        showActivity.putExtra(EXTRA_PACKAGE, packageName);
        activity.startActivity(showActivity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.laravel_connect_activity_model_browser);


        ModelClassListFragment fragment =  ModelClassListFragment.newInstance(getIntent().getStringExtra(EXTRA_PACKAGE));
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.container,
                            fragment,
                            ModelClassListFragment.TAG).commit();



    }


    @Override
    public void onListFragmentInteraction(Class modelClass) {

        final ModelList modelList = BaseModel.index(modelClass);

        ModelListFragment modelListFragment = ModelListFragment.getInstance(modelList);

        getFragmentManager()
                .beginTransaction()
                .add(R.id.container, modelListFragment, ModelClassListFragment.TAG)
                .addToBackStack(null)
                .commit();

    }

    @Override
    public void onListFragmentInteraction(BaseModel model, Fragment originFragment) {

        ModelDetailsFragment fragment = ModelDetailsFragment.getInstance(model);

        getFragmentManager()
                .beginTransaction()
                .add(R.id.container, fragment, ModelDetailsFragment.TAG)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onModelAttributeSelected(BaseModel model, ModelAttribute attribute) {

        if(attribute instanceof ModelManyRelation){
            onModelManyRelationSelected((ModelManyRelation) attribute);
        }
        if(attribute instanceof ModelOneRelation){
            ModelOneRelation relation = (ModelOneRelation)attribute;
            onListFragmentInteraction(relation.getValue(), null );
        }

    }


    private void onModelManyRelationSelected(ModelManyRelation relation) {

        ModelManyRelationFragment fragment = ModelManyRelationFragment.getInstance(relation.list());

        getFragmentManager()
                .beginTransaction()
                .add(R.id.container,
                        fragment , ModelManyRelationFragment.TAG).addToBackStack(null)
                .commit();
    }
}
