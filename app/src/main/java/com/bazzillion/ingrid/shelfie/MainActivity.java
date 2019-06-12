package com.bazzillion.ingrid.shelfie;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.bazzillion.ingrid.shelfie.Database.Repository;
import com.bazzillion.ingrid.shelfie.Utils.FirebaseDataWriting;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends DrawerActivity {

    private ListView productListView;
    private ArrayAdapter<String> productArrayAdapter;
    private static final String LIST_VIEW_STATE = "list_view_state";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        super.onCreateDrawer();

            productArrayAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1,
                    getResources().getStringArray(R.array.product_array));
            productListView = findViewById(R.id.product_list_view);
            productListView.setAdapter(productArrayAdapter);

            productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(MainActivity.this, NewRecipeActivity.class);
                    intent.putExtra(Repository.KEY_RECIPE_MODE, Repository.RecipeMode.Create);
                    intent.putExtra(Repository.KEY_PRODUCT_TYPE, getResources().getStringArray(R.array.product_array)[position]);
                    startActivity(intent);
                }
            });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(LIST_VIEW_STATE, productListView.onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null){
            productListView.onRestoreInstanceState(savedInstanceState.getParcelable(LIST_VIEW_STATE));
        }
    }
}
