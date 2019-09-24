package com.bazzillion.ingrid.shelfie.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Recipe.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "recipe_db";

    public static AppDatabase getInstance(Context context){
        if (instance == null){
            synchronized (LOCK){
                instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME)
                .build();
            }
        }
        return instance;
    }

    public abstract RecipeDao recipeDao();
}
