package com.nano.aditya.atombeammonitor.app.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nano.aditya.atombeammonitor.app.R;

/**
 * Created by adity on 8/31/2016.
 */
public class ManualFragment extends Fragment {

    public ManualFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.manual_fragment,container,false);
    }
}
