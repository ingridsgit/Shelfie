package com.bazzillion.ingrid.shelfie.Database;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface RecipeDao {

    @Query("SELECT * FROM recipe")
    LiveData<List<Recipe>> loadMyRecipes();

    @Query("SELECT * FROM recipe WHERE id = :id")
    LiveData<Recipe> getRecipeById(int id);

    @Insert
    void insertNewRecipe(Recipe recipe); // no need for LiveData

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateRecipe(Recipe recipe); // no need for LiveData

    @Delete
    void deleteRecipe(Recipe recipe); // no need for LiveData
}
