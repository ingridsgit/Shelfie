package com.bazzillion.ingrid.shelfie;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bazzillion.ingrid.shelfie.Database.AppDatabase;
import com.bazzillion.ingrid.shelfie.Database.Recipe;
import com.bazzillion.ingrid.shelfie.Utils.FirebaseDataWriting;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;

public class MyRecipesActivity extends DrawerActivity {

    private RadioGroup radioGroup;
    private AppDatabase appDatabase;
    private ListView listView;
    private ArrayAdapter<String> recipeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_recipes);
        super.onCreateDrawer();
        appDatabase = AppDatabase.getInstance(getApplicationContext());
        listView = findViewById(R.id.recipe_list_view);
        recipeAdapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1);
        listView.setAdapter(recipeAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LiveData<List<Recipe>> myRecipes = appDatabase.recipeDao().loadMyRecipes();
        if (myRecipes != null){
            List<String> myRecipeNames = new ArrayList<>();
//            for (Recipe recipe : myRecipes){
//                myRecipeNames.add(recipe.getName());
//            }
            recipeAdapter.addAll(myRecipeNames);
        }

    }

    //    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//
//    }
}
