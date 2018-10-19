package com.bazzillion.ingrid.shelfie;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {
    private ListView productListView;
    private ArrayAdapter<String> productArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        productArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.product_array));
        productListView = findViewById(R.id.product_list_view);
        productListView.setAdapter(productArrayAdapter);
    }
}
