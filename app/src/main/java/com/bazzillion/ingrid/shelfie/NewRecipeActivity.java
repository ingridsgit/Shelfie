package com.bazzillion.ingrid.shelfie;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.bazzillion.ingrid.shelfie.Utils.FirebaseDataWriting;

public class NewRecipeActivity extends BaseActivity {

    private String productType = null;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recipe);
        super.onCreateDrawer();
        textView = findViewById(R.id.new_recipe_textview);
        productType = getIntent().getStringExtra(MainActivity.PRODUCT_TYPE);

        if (productType != null){
            setTitle(productType);
            switch (productType){
                case "Toothpaste": FirebaseDataWriting.writeToDb(this);
            }
        }
    }
}
