package com.bazzillion.ingrid.shelfie;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.TextView;
import android.widget.Toast;

public class NewRecipeActivity extends DrawerActivity {

    private static final String KEY_SAVED_PRODUCT = "saved_product";
    private String productType = null;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recipe);
        super.onCreateDrawer();

        if (savedInstanceState == null){
            productType = getIntent().getStringExtra(MainActivity.PRODUCT_TYPE);
        } else {
            productType = savedInstanceState.getString(KEY_SAVED_PRODUCT);
        }
        if (productType != null){
            setTitle(productType);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.base_fragment, BaseFragment.newInstance(productType))
            .commit();
        } else {
            Toast.makeText(this, "productType is null", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (productType != null){
            outState.putString(KEY_SAVED_PRODUCT, productType);
        }

    }
}
