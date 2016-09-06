package com.nano.aditya.atombeammonitor.app.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
//import android.preference.PreferenceFragment;
//import android.support.v4.preference.PreferenceFragment;

import com.nano.aditya.atombeammonitor.app.R;

/**
 * Created by adity on 8/31/2016.
 */
public class SettingsFragment extends PreferenceFragmentCompat {
    public SettingsFragment() {
    }

    public static final String KEY_PREF_EXERCISES = "pref_number_of_exercises";
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings);


        /*Preference edit1= findPreference("edittext_preference");
        EditTextPreference editt1 = (EditTextPreference) edit1;

        System.out.println(String.valueOf(editt1.getText().toString()));*/
    }

}
