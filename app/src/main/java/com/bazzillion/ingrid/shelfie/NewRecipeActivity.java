package com.bazzillion.ingrid.shelfie;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class NewRecipeActivity extends BaseActivity {

    private String productType = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recipe);
        super.onCreateDrawer();
        productType = getIntent().getStringExtra(MainActivity.PRODUCT_TYPE);
        if (productType != null){
            setTitle(productType);

        }
    }
}
