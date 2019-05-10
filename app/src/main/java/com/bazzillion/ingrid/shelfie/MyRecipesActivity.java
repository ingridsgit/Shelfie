package com.bazzillion.ingrid.shelfie;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioGroup;

import com.bazzillion.ingrid.shelfie.Adapters.RecipeAdapter;
import com.bazzillion.ingrid.shelfie.Database.AppDatabase;
import com.bazzillion.ingrid.shelfie.Database.AppExecutors;
import com.bazzillion.ingrid.shelfie.Database.MainViewModel;
import com.bazzillion.ingrid.shelfie.Database.Recipe;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MyRecipesActivity extends DrawerActivity implements RecipeAdapter.RecipeClickListener {

    private RadioGroup radioGroup;
    private AppDatabase appDatabase;
    private RecyclerView recyclerView;
    private RecipeAdapter recipeAdapter;
    private LiveData<List<Recipe>> myRecipes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_recipes);
        super.onCreateDrawer();
        appDatabase = AppDatabase.getInstance(getApplicationContext());
        setUpUi();
        setUpViewModel();
    }

    private void setUpUi(){
        recyclerView = findViewById(R.id.recipe_list_view);
        recipeAdapter = new RecipeAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(recipeAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final @NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                AppExecutors.getInstance().getDiskIo().execute(new Runnable() {
                    @Override
                    public void run() {
                        int position = viewHolder.getAdapterPosition();
                        List<Recipe> recipes = recipeAdapter.getRecipes();
                        appDatabase.recipeDao().deleteRecipe(recipes.get(position));
                    }
                });

            }
        }).attachToRecyclerView(recyclerView);
    }

    private void setUpViewModel(){
        MainViewModel mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        myRecipes = mainViewModel.getMyRecipes();
        myRecipes.observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(List<Recipe> recipes) {
                recipeAdapter.setRecipes(recipes);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onRecipeClick(int recipeId) {
        Intent intent = new Intent(MyRecipesActivity.this, NewRecipeActivity.class);
        intent.putExtra(NewRecipeActivity.KEY_ACTIVITY_MODE, NewRecipeActivity.UPDATE_RECIPE);
        intent.putExtra(NewRecipeActivity.KEY_RECIPE_ID, recipeId);
        intent.putExtra(NewRecipeActivity.KEY_PRODUCT_TYPE, getResources().getStringArray(R.array.product_array)[0]);
        startActivity(intent);

    }

    //    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//
//    }

}
