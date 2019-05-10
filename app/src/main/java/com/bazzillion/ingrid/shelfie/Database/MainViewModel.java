package com.bazzillion.ingrid.shelfie.Database;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private LiveData<List<Recipe>> myRecipes;
    private static final String LOG_TAG = MainViewModel.class.getSimpleName();

    public MainViewModel(@NonNull Application application) {
        super(application);
        Log.i(LOG_TAG, "Retrieving Recipes from the Database");
        myRecipes = AppDatabase.getInstance(application).recipeDao().loadMyRecipes();
    }

    public LiveData<List<Recipe>> getMyRecipes() {
        return myRecipes;
    }

}
