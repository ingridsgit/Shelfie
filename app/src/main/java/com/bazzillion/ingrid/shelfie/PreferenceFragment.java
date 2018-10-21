package com.bazzillion.ingrid.shelfie;


import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v14.preference.MultiSelectListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PreferenceFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        int count = preferenceScreen.getPreferenceCount();

        for (int i = 0; i < count; i++) {
            Preference preference = preferenceScreen.getPreference(i);
            if (!(preference instanceof CheckBoxPreference)) {
                setPreferenceSummary(preference);
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        if (preference != null) {
            if (!(preference instanceof CheckBoxPreference)) {
                setPreferenceSummary(preference);
            }
        }
    }


    private void setPreferenceSummary(Preference preference) {

        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            listPreference.setSummary(listPreference.getEntry());
        } else if (preference instanceof MultiSelectListPreference){
            MultiSelectListPreference multiSelectListPreference = (MultiSelectListPreference)preference;
            Set<String> values = multiSelectListPreference.getValues();
            Object[] valuesArray = values.toArray();
            List<String> entriesList = new ArrayList<>();
            for ( int i = 0 ; i < valuesArray.length ; i++){
                String value = (String) valuesArray[i];
                int index = multiSelectListPreference.findIndexOfValue(value);
                entriesList.add(multiSelectListPreference.getEntries()[index].toString());
            }
            multiSelectListPreference.setSummary(TextUtils.join(", ", entriesList));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
