package com.bazzillion.ingrid.shelfie;

import android.os.Bundle;
import android.widget.RadioGroup;

import com.bazzillion.ingrid.shelfie.Adapters.RecipeAdapter;
import com.bazzillion.ingrid.shelfie.Database.AppDatabase;
import com.bazzillion.ingrid.shelfie.Database.AppExecutors;
import com.bazzillion.ingrid.shelfie.Database.Recipe;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MyRecipesActivity extends DrawerActivity {

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
        recyclerView = findViewById(R.id.recipe_list_view);
        recipeAdapter = new RecipeAdapter() {


        };
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
                        List<Recipe> recipes = recipeAdapter.getRecipes().getValue();
                        appDatabase.recipeDao().deleteRecipe(recipes.get(position));
                        updateRecipes();
                    }
                });

            }
        }).attachToRecyclerView(recyclerView);
    }

    private void updateRecipes(){
        AppExecutors.getInstance().getDiskIo().execute(new Runnable() {
            @Override
            public void run() {
                myRecipes = appDatabase.recipeDao().loadMyRecipes();
                if (myRecipes != null){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recipeAdapter.setRecipes(myRecipes);

                        }
                    });

                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateRecipes();
    }

    //    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//
//    }

}
