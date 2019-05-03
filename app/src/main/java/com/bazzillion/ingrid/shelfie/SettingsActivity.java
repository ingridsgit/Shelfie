package com.bazzillion.ingrid.shelfie;
import android.os.Bundle;

public class SettingsActivity extends DrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        super.onCreateDrawer();
    }
}
