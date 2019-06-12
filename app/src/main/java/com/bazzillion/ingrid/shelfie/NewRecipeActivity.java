package com.bazzillion.ingrid.shelfie;

import android.os.Bundle;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;


import android.view.View;
import android.widget.Toast;

import com.bazzillion.ingrid.shelfie.Adapters.BaseAdapter;
import com.bazzillion.ingrid.shelfie.Database.Recipe;
import com.bazzillion.ingrid.shelfie.Database.Repository;

import static com.bazzillion.ingrid.shelfie.Database.Repository.INVALID_RECIPE_ID;
import static com.bazzillion.ingrid.shelfie.Database.Repository.KEY_RECIPE_MODE;
import static com.bazzillion.ingrid.shelfie.Database.Repository.KEY_PRODUCT_TYPE;
import static com.bazzillion.ingrid.shelfie.Database.Repository.KEY_RECIPE_ID;
import static com.bazzillion.ingrid.shelfie.Database.Repository.RecipeMode;

public class NewRecipeActivity extends DrawerActivity implements BaseAdapter.BaseClickHandler {

    private String productType = null;
    private String baseName = null;
    private RecipeMode recipeMode;
    private int recipeId;
    private View baseFragmentView;
    private FragmentManager fragmentManager;
    private LiveData<Recipe> currentRecipe;

    private static final String KEY_BASE_NAME = "base_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recipe);
        super.onCreateDrawer();
        fragmentManager = getSupportFragmentManager();
        baseFragmentView = findViewById(R.id.base_fragment);

        if (savedInstanceState == null){
            recipeMode = (RecipeMode) getIntent().getSerializableExtra(KEY_RECIPE_MODE);
            productType = getIntent().getStringExtra(KEY_PRODUCT_TYPE);

            if (recipeMode == RecipeMode.Read){
                recipeId = getIntent().getIntExtra(KEY_RECIPE_ID, 1);
                final LiveData<Recipe> currentRecipe = Repository.getInstance(this).getRecipeById(this, recipeId);
                currentRecipe.observe(this, new Observer<Recipe>() {
                    @Override
                    public void onChanged(Recipe recipe) {
                        currentRecipe.removeObserver(this);
                        baseName = recipe.getBaseName();
                        fragmentManager.beginTransaction().replace(R.id.add_on_fragment,
                                AddOnFragment.newInstance(baseName, recipeId, recipeMode))
                                .commit();
                        setTitle(baseName);
                    }
                });

            } else {
                fragmentManager.beginTransaction().replace(R.id.base_fragment, BaseFragment.newInstance(productType))
                        .commit();
            }
        } else {
            recipeMode = (RecipeMode) savedInstanceState.getSerializable(KEY_RECIPE_MODE);
            productType = savedInstanceState.getString(KEY_PRODUCT_TYPE);
            if (recipeMode == RecipeMode.Read){
                recipeId = savedInstanceState.getInt(KEY_RECIPE_ID, 1);
                baseName = savedInstanceState.getString(KEY_BASE_NAME);
                setTitle(baseName);
            }
        }


        if (recipeMode == RecipeMode.Create){
            baseFragmentView.setVisibility(View.VISIBLE);
            if (productType != null){
                setTitle(productType);
            } else {
                Toast.makeText(this, getText(R.string.no_product), Toast.LENGTH_LONG).show();
            }
        } else {
            baseFragmentView.setVisibility(View.GONE);

        }
    }

    @Override
    public void onBaseClick(String base) {
        fragmentManager.beginTransaction()
                .replace(R.id.add_on_fragment, AddOnFragment.newInstance(base, INVALID_RECIPE_ID, RecipeMode.Create))
                .commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_RECIPE_MODE, recipeMode);
        if (recipeMode == RecipeMode.Read){
            outState.putInt(KEY_RECIPE_ID, recipeId);
            outState.putString(KEY_BASE_NAME, baseName);
        }
        if (productType != null){
            outState.putString(KEY_PRODUCT_TYPE, productType);
        }

    }
}
