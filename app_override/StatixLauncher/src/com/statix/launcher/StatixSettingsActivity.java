package com.statix.launcher;

import android.content.SharedPreferences;

import com.android.launcher3.settings.SettingsActivity;

import com.statix.launcher.StatixUtilities;

public class StatixSettingsActivity extends SettingsActivity {

   @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                StatixUtilities.restart(this);
   }

}
