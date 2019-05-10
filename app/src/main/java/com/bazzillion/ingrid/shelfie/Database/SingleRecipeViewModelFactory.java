package com.bazzillion.ingrid.shelfie.Database;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class SingleRecipeViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase appDatabase;
    private final int recipeId;

    public SingleRecipeViewModelFactory(AppDatabase appDatabase, int recipeId){
        this.appDatabase = appDatabase;
        this.recipeId = recipeId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new SingleRecipeViewModel(appDatabase, recipeId);
    }
}
