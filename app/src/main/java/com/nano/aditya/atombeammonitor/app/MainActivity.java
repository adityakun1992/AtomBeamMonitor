package com.nano.aditya.atombeammonitor.app;

//import android.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.nano.aditya.atombeammonitor.app.fragments.ContactFragment;
import com.nano.aditya.atombeammonitor.app.fragments.GraphFragment;
import com.nano.aditya.atombeammonitor.app.fragments.ManualFragment;
import com.nano.aditya.atombeammonitor.app.fragments.MonitorFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    Toolbar toolbar;
    FragmentManager manager;
    public String getURL;
    public boolean darkMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadPreferences();
        getWindow().setWindowAnimations(0);
        if (darkMode == true){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.fragment_content, new MonitorFragment()).commit();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar.setTitle("AtomBeam Monitor");
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        manager = getSupportFragmentManager();
        int id = item.getItemId();

        if (id == R.id.nav_monitor) {
            toolbar.setTitle("AtomBeam Monitor");
            Log.i(LOG_TAG, "Clear and replace");
            clearBackStack();
            manager.beginTransaction().replace(R.id.fragment_content, new MonitorFragment()).commit();
        } else if (id == R.id.nav_graphic) {
            if(getCurrentFragment() instanceof MonitorFragment) {
                toolbar.setTitle("Graphical Representation");
                manager.beginTransaction().replace(R.id.fragment_content, new GraphFragment()).addToBackStack("fragback").commit();
            }
            else {
                toolbar.setTitle("Graphical Representation");
                manager.popBackStack();
                manager.beginTransaction().replace(R.id.fragment_content, new GraphFragment()).addToBackStack("fragback").commit();
            }
        } else if (id == R.id.nav_manual) {
            toolbar.setTitle("User Manual");
            if (getCurrentFragment() instanceof MonitorFragment){
                manager.beginTransaction().replace(R.id.fragment_content, new ManualFragment()).addToBackStack("fragback").commit();
            }
            else{
                toolbar.setTitle("User Manual");
                manager.popBackStack();
                manager.beginTransaction().replace(R.id.fragment_content, new ManualFragment()).addToBackStack("fragback").commit();
            }
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_contact) {
            toolbar.setTitle("Contact");
            if(getCurrentFragment() instanceof MonitorFragment) {
                manager.beginTransaction().replace(R.id.fragment_content, new ContactFragment()).addToBackStack("fragback").commit();
            }
            else {
                toolbar.setTitle("Contact");
                manager.popBackStack();
                manager.beginTransaction().replace(R.id.fragment_content, new ContactFragment()).commit();
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void clearBackStack(){
        manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
    protected Fragment getCurrentFragment() {
        Fragment currentFragment = this.getSupportFragmentManager().findFragmentById(R.id.fragment_content);
        return currentFragment;
    }

    private void loadPreferences(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        darkMode = sharedPreferences.getBoolean("theme",false);
        getURL = sharedPreferences.getString("url", "None");
    }

    public void restartActivity(){
        Intent mIntent = getIntent();
        finish();
        startActivity(mIntent);
    }
}
