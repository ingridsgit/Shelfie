package com.bazzillion.ingrid.shelfie.Database;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;


public class SingleRecipeViewModel extends ViewModel {

    private LiveData<Recipe> recipe;

    public SingleRecipeViewModel(AppDatabase appDatabase, int recipeId){
        recipe = appDatabase.recipeDao().getRecipeById(recipeId);
    }

    public LiveData<Recipe> getRecipe() {
        return recipe;
    }
}
