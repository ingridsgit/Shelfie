package com.bazzillion.ingrid.shelfie;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bazzillion.ingrid.shelfie.Adapters.RecipeAdapter;
import com.bazzillion.ingrid.shelfie.Database.AppDatabase;
import com.bazzillion.ingrid.shelfie.Database.AppExecutors;
import com.bazzillion.ingrid.shelfie.Database.MainViewModel;
import com.bazzillion.ingrid.shelfie.Database.Recipe;
import com.bazzillion.ingrid.shelfie.Database.Repository;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MyRecipesActivity extends DrawerActivity implements RecipeAdapter.RecipeClickListener {

    private RecyclerView recyclerView;
    private RecipeAdapter recipeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_recipes);
        super.onCreateDrawer();
        setUpUi();
        Repository.getInstance(this).setUpViewModel(this, recipeAdapter);
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
                int position = viewHolder.getAdapterPosition();
                List<Recipe> recipes = recipeAdapter.getRecipes();
                Repository.getInstance(MyRecipesActivity.this).deleteRecipe(recipes.get(position));
                Toast.makeText(MyRecipesActivity.this, getResources().getString(R.string.deleted, recipes.get(position).getName()), Toast.LENGTH_LONG).show();
            }
        }).attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onRecipeClick(int recipeId) {
        Intent intent = new Intent(MyRecipesActivity.this, NewRecipeActivity.class);
        intent.putExtra(Repository.KEY_RECIPE_MODE, Repository.RecipeMode.Read);
        intent.putExtra(Repository.KEY_RECIPE_ID, recipeId);
        intent.putExtra(Repository.KEY_PRODUCT_TYPE, getResources().getStringArray(R.array.product_array)[0]);
        startActivity(intent);

    }

    //    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//
//    }

}
