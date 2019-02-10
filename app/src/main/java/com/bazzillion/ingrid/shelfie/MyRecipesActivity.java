package com.bazzillion.ingrid.shelfie;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bazzillion.ingrid.shelfie.Utils.FirebaseDataWriting;

public class MyRecipesActivity extends DrawerActivity {
    static AsyncTask<String, Void, String> backgroundQuery;
    private Button testButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_recipes);
        super.onCreateDrawer();
        testButton = findViewById(R.id.button);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }





}
