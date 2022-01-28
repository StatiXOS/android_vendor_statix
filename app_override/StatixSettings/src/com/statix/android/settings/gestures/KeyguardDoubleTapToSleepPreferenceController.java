/**
 * Copyright (C) 2021 StatiX
 * SPDX-License-Identifer: Apache-2.0
 */

package com.statix.android.settings.gestures;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;

public class KeyguardDoubleTapToSleepPreferenceController extends BasePreferenceController {

    private static final String KEY = "double_tap_sleep_summary";
    private final String STATUSBAR = Settings.System.DOUBLE_TAP_SLEEP_GESTURE;
    private final String LOCKSCREEN = Settings.System.DOUBLE_TAP_SLEEP_LOCKSCREEN;

    public KeyguardDoubleTapToSleepPreferenceController(Context context, String key) {
        super(context, key);
    }

    @Override
    public CharSequence getSummary() {
        return mContext.getText(R.string.double_tap_sleep_summary);
    }

    public int getAvailabilityStatus() {
        return AVAILABLE;
    }
}