package com.nano.aditya.atombeammonitor.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.nano.aditya.atombeammonitor.app.fragments.SettingsFragment;

/**
 * Created by adity on 9/17/2016.
 */
public class SettingsActivity extends AppCompatActivity{
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //setTheme(R.style.PreferencesTheme);

        toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_content, new SettingsFragment()).commit();

    }

    /*@Override
    public void onBackPressed() {

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }*/

}
