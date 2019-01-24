package com.bazzillion.ingrid.shelfie;

import android.os.AsyncTask;
import android.os.Bundle;

public class MyRecipesActivity extends DrawerActivity {
    static AsyncTask<String, Void, String> backgroundQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_recipes);
        super.onCreateDrawer();

    }





}
