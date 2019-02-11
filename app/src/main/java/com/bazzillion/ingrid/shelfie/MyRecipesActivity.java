package com.bazzillion.ingrid.shelfie;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bazzillion.ingrid.shelfie.Utils.FirebaseDataWriting;

public class MyRecipesActivity extends DrawerActivity {

    private RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_recipes);
        super.onCreateDrawer();
        radioGroup = findViewById(R.id.radio_group);
        Toast.makeText(this, String.valueOf(radioGroup.isSaveEnabled()), Toast.LENGTH_SHORT).show();
        radioGroup.setSaveEnabled(true);
        radioGroup.setSaveFromParentEnabled(true);

        if (savedInstanceState != null){

            RadioButton radioButton = (RadioButton)radioGroup.getChildAt(savedInstanceState.getInt("KEY"));
            if (radioButton != null){
                radioButton.setChecked(true);
            }
        }


    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        int checkedId = radioGroup.getCheckedRadioButtonId();
//        outState.putInt("KEY", checkedId);
//        Toast.makeText(this, String.valueOf(checkedId), Toast.LENGTH_LONG).show();
//    }
}
