package com.bazzillion.ingrid.shelfie;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends BaseActivity {
    public static final String PRODUCT_TYPE = "product_type";

    private ListView productListView;
    private ArrayAdapter<String> productArrayAdapter;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        super.onCreateDrawer();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference typeReference = databaseReference.child("type");

        productArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.product_array));
        productListView = findViewById(R.id.product_list_view);
        productListView.setAdapter(productArrayAdapter);

        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, NewRecipeActivity.class);
                intent.putExtra(PRODUCT_TYPE, getResources().getStringArray(R.array.product_array)[position]);
                startActivity(intent);
            }
        });

    }


}
