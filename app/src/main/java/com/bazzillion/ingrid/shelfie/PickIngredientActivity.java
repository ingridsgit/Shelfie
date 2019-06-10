package com.bazzillion.ingrid.shelfie;

import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.bazzillion.ingrid.shelfie.Database.Base;
import com.bazzillion.ingrid.shelfie.Database.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.bazzillion.ingrid.shelfie.Database.Repository.COMPULSORY_ADD_ON;

public class PickIngredientActivity extends DrawerActivity {

    private Base selectedBase;
    private int isOptional;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_ingredient);
        super.onCreateDrawer();

        if (savedInstanceState == null){
            selectedBase = getIntent().getParcelableExtra(Repository.KEY_BASE);
            isOptional = getIntent().getIntExtra(Repository.KEY_IS_OPTIONAL, COMPULSORY_ADD_ON);
            List<String> preselectedIngredients = new ArrayList<>();
            if (isOptional == COMPULSORY_ADD_ON){
                preselectedIngredients = getIntent().getStringArrayListExtra(Repository.KEY_COMPULSORY_ADD_ONS);
            } else {
                preselectedIngredients = getIntent().getStringArrayListExtra(Repository.KEY_OPTIONAL_ADD_ONS);
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.pick_ingredient_fragment,
                    PickIngredientFragment.newInstance(selectedBase, isOptional, preselectedIngredients)).commit();

        } else {
            selectedBase = savedInstanceState.getParcelable(Repository.KEY_BASE);
            isOptional = savedInstanceState.getInt(Repository.KEY_IS_OPTIONAL);

        }
        setTitle(selectedBase.name);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Repository.KEY_BASE, selectedBase);
        outState.putInt(Repository.KEY_IS_OPTIONAL, isOptional);
    }
}
