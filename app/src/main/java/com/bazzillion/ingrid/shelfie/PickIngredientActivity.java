package com.bazzillion.ingrid.shelfie;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PickIngredientActivity extends DrawerActivity {

    private Base selectedBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_ingredient);

        if (savedInstanceState == null){
            selectedBase = getIntent().getParcelableExtra(AddOnFragment.KEY_BASE);
        } else {
            selectedBase = savedInstanceState.getParcelable(AddOnFragment.KEY_BASE);
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.pick_ingredient_fragment, PickIngredientFragment.newInstance(selectedBase)).commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(AddOnFragment.KEY_BASE, selectedBase);
    }
}
