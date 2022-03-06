package com.statix.android.settings;

import com.android.settings.SettingsActivity;

public class StatixSettingsActivity extends SettingsActivity {

    @Override
    protected boolean isValidFragment(String fragmentName) {
        // Almost all fragments are wrapped in this,
        // except for a few that have their own activities.
        for (int i = 0; i < StatixSettingsGateway.ENTRY_FRAGMENTS.length; i++) {
            if (StatixSettingsGateway.ENTRY_FRAGMENTS[i].equals(fragmentName)) return true;
        }
        return false;
    }

}
