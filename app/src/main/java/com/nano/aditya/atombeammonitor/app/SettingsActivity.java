package com.nano.aditya.atombeammonitor.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;

import com.nano.aditya.atombeammonitor.app.fragments.SettingsFragment;
import com.ti.aditya.swipecloseactivity.lib.SwipeCloseActivity;
import com.ti.aditya.swipecloseactivity.lib.SwipeCloseLayout;

/**
 * Created by adity on 9/17/2016.
 */
public class SettingsActivity extends SwipeCloseActivity{
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    Toolbar toolbar;
    private boolean darkMode;
    private String getURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        darkMode = sharedPreferences.getBoolean("theme",false);
        getURL = sharedPreferences.getString("url", "None");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //setTheme(R.style.PreferencesTheme);

        toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        setDragEdge(SwipeCloseLayout.DragEdge.LEFT);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_content, new SettingsFragment()).commit();

    }

    @Override
    protected void onDestroy() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (darkMode == sharedPreferences.getBoolean("theme",false) && getURL == sharedPreferences.getString("url", "None")){
            super.onDestroy();
        }
        else{
            //startActivity(new Intent(this, MainActivity.class));
            super.onDestroy();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            overridePendingTransition( 0, 0);
            startActivity(intent);
            overridePendingTransition( 0, 0);
            /*finish();
            overridePendingTransition( 0, 0);
            startActivity(intent);
            overridePendingTransition( 0, 0);*/

        }
    }

    /*@Override
    public void onBackPressed() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (darkMode == sharedPreferences.getBoolean("theme",false) && getURL == sharedPreferences.getString("url", "None")){
            super.onBackPressed();
        }
        else{
            //startActivity(new Intent(this, MainActivity.class));
            finish();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }


    }*/

}
