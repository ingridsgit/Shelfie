package com.bazzillion.ingrid.shelfie;

import android.os.Bundle;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import android.view.View;
import android.widget.Toast;

import com.bazzillion.ingrid.shelfie.Adapters.BaseAdapter;
import com.bazzillion.ingrid.shelfie.Database.AppDatabase;
import com.bazzillion.ingrid.shelfie.Database.AppExecutors;
import com.bazzillion.ingrid.shelfie.Database.Recipe;

public class NewRecipeActivity extends DrawerActivity implements BaseAdapter.BaseClickHandler {

    private String productType = null;
    private int activityMode;
    private int recipeId;
    private View baseFragmentView;
    private FragmentManager fragmentManager;
    private LiveData<Recipe> currentRecipe;
    public static final int CREATE_RECIPE = 80;
    public static final int UPDATE_RECIPE = 90;
    public static final String KEY_ACTIVITY_MODE = "activity_mode";
    public static final String KEY_RECIPE_ID = "recipe_id";
    public static final String KEY_PRODUCT_TYPE = "product_type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recipe);
        super.onCreateDrawer();
        fragmentManager = getSupportFragmentManager();
        baseFragmentView = findViewById(R.id.base_fragment);

        if (savedInstanceState == null){
            activityMode = getIntent().getIntExtra(KEY_ACTIVITY_MODE, CREATE_RECIPE);
            productType = getIntent().getStringExtra(KEY_PRODUCT_TYPE);
            if (activityMode == UPDATE_RECIPE){
                recipeId = getIntent().getIntExtra(KEY_RECIPE_ID, 1);
            }
        } else {
            activityMode = savedInstanceState.getInt(KEY_ACTIVITY_MODE);
            productType = savedInstanceState.getString(KEY_PRODUCT_TYPE);
            if (activityMode == UPDATE_RECIPE){
                recipeId = savedInstanceState.getInt(KEY_RECIPE_ID, 1);
            }
        }
        if (productType != null){
            setTitle(productType);
            baseFragmentView.setVisibility(View.VISIBLE);
            fragmentManager.beginTransaction().replace(R.id.base_fragment, BaseFragment.newInstance(productType))
            .commit();
            if (activityMode == UPDATE_RECIPE){
                baseFragmentView.setVisibility(View.GONE);
                currentRecipe = AppDatabase.getInstance(NewRecipeActivity.this).recipeDao().getRecipeById(recipeId);
                currentRecipe.observe(this, new Observer<Recipe>() {
                    @Override
                    public void onChanged(Recipe recipe) {
                        fragmentManager.beginTransaction().replace(R.id.add_on_fragment, AddOnFragment.newInstance(recipe.getBaseName(), recipeId));
                    }
                });


            }
        } else {
            Toast.makeText(this, getText(R.string.no_product), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBaseClick(String base) {
        fragmentManager.beginTransaction()
                .replace(R.id.add_on_fragment, AddOnFragment.newInstance(base))
                .commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_ACTIVITY_MODE, CREATE_RECIPE);
        if (activityMode == UPDATE_RECIPE){
            outState.putInt(KEY_RECIPE_ID, recipeId);
        }
        if (productType != null){
            outState.putString(KEY_PRODUCT_TYPE, productType);
        }

    }
}
