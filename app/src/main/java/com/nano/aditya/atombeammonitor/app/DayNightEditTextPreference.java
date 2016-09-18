package com.nano.aditya.atombeammonitor.app;


import android.app.AlertDialog;
import android.content.Context;
import android.preference.EditTextPreference;

/**
 * Created by adity on 9/18/2016.
 */
public class DayNightEditTextPreference extends EditTextPreference {
    public DayNightEditTextPreference(Context context) {
        super(context);
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        builder.getContext().setTheme(R.style.editTextDialogStyle);
        super.onPrepareDialogBuilder(builder);
    }
}
