package com.statix.launcher.settings;

import static com.statix.launcher.OverlayCallbackImpl.KEY_ENABLE_MINUS_ONE;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class StatixSettingsActivity extends SettingsActivity {

    public static class LauncherSettingsFragment extends PreferenceFragmentCompat {

        protected static final String GSA_PACKAGE = "com.google.android.googlequicksearchbox";

        private Preference mShowGoogleAppPref;

        protected boolean initPreference(Preference preference) {
            switch (preference.getKey()) {
                case KEY_ENABLE_MINUS_ONE:
                    mShowGoogleAppPref = preference;
                    updateIsGoogleAppEnabled();
                    return true;
            }

        public static boolean isGSAEnabled(Context context) {
            try {
                return context.getPackageManager().getApplicationInfo(GSA_PACKAGE, 0).enabled;
            } catch (PackageManager.NameNotFoundException e) {
                return false;
            }
        }

        private void updateIsGoogleAppEnabled() {
            if (mShowGoogleAppPref != null) {
                mShowGoogleAppPref.setEnabled(isGSAEnabled(getContext()));
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            updateIsGoogleAppEnabled();
        }
