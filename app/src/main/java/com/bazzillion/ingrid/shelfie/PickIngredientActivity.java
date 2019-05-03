package com.bazzillion.ingrid.shelfie;

import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class PickIngredientActivity extends DrawerActivity {

    private Base selectedBase;
    private int isOptional;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_ingredient);
        super.onCreateDrawer();

        if (savedInstanceState == null){
            selectedBase = getIntent().getParcelableExtra(AddOnFragment.KEY_BASE);
            isOptional = getIntent().getIntExtra(AddOnFragment.KEY_IS_OPTIONAL, AddOnFragment.COMPULSORY_ADD_ON);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.pick_ingredient_fragment, PickIngredientFragment.newInstance(selectedBase, isOptional)).commit();

        } else {
            selectedBase = savedInstanceState.getParcelable(AddOnFragment.KEY_BASE);
            isOptional = savedInstanceState.getInt(AddOnFragment.KEY_IS_OPTIONAL);

        }
        setTitle(selectedBase.name);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(AddOnFragment.KEY_BASE, selectedBase);
        outState.putInt(AddOnFragment.KEY_IS_OPTIONAL, isOptional);
    }
}
