package com.statix.android.settings.fuelgauge;

import static com.android.settings.core.BasePreferenceController.AVAILABLE;
import static com.android.settings.core.BasePreferenceController.UNSUPPORTED_ON_DEVICE;

import android.content.Context;
import android.content.IntentFilter;
import androidx.preference.Preference;

import com.android.internal.annotations.VisibleForTesting;

import com.android.settings.core.TogglePreferenceController;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.slices.SliceBackgroundWorker;

import com.statix.android.systemui.adaptivecharging.AdaptiveChargingManager;

public class AdaptiveChargingPreferenceController extends TogglePreferenceController {

    @VisibleForTesting
    private AdaptiveChargingManager mAdaptiveChargingManager;
    private boolean mChecked;

    public AdaptiveChargingPreferenceController(Context context, String preferenceKey) {
        super(context, preferenceKey);
        mAdaptiveChargingManager = new AdaptiveChargingManager(context);
    }

    @Override
    public int getAvailabilityStatus() {
        return mAdaptiveChargingManager.isAvailable() ? AVAILABLE : UNSUPPORTED_ON_DEVICE;
    }

    @Override
    public boolean isChecked() {
        return mAdaptiveChargingManager.isEnabled();
    }

    @Override
    public void updateState(Preference preference) {
        super.updateState(preference);
        mChecked = isChecked();
    }

    @Override
    public boolean setChecked(boolean enabled) {
        mAdaptiveChargingManager.setEnabled(enabled);
        if (!enabled) {
            mAdaptiveChargingManager.setAdaptiveChargingDeadline(-1);
        }
        if (mChecked == enabled) {
            return true;
        }
        mChecked = enabled;
//        FeatureFactory.getFactory(mContext).getMetricsFeatureProvider().action(mContext, 1781, enabled);
        return true;
    }
}
