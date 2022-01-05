package com.statix.android.systemui.adaptivecharging;

import android.content.Context;
import android.os.IHwBinder;
import android.os.RemoteException;
import android.provider.DeviceConfig;
import android.provider.Settings;
import android.util.Log;

import java.util.NoSuchElementException;

import vendor.google.google_battery.V1_0.IGoogleBattery;

public class AdaptiveChargingManager {
    private static final boolean DEBUG = Log.isLoggable("AdaptiveChargingManager", 3);
    private Context mContext;

    public interface AdaptiveChargingStatusReceiver {
        void onDestroyInterface();

        void onReceiveStatus(String str, int i);
    }

    public AdaptiveChargingManager(Context context) {
        mContext = context;
    }

    private static IGoogleBattery initHalInterface(IHwBinder.DeathRecipient deathRecipient) {
        if (DEBUG) {
            Log.d("AdaptiveChargingManager", "initHalInterface");
        }
        try {
            IGoogleBattery service = IGoogleBattery.getService();
            if (!(service == null || deathRecipient == null)) {
                service.linkToDeath(deathRecipient, 0);
            }
            return service;
        } catch (RemoteException | NoSuchElementException e) {
            Log.e("AdaptiveChargingManager", "failed to get Google Battery HAL: ", e);
            return null;
        }
    }

    private void destroyHalInterface(IGoogleBattery iGoogleBattery, IHwBinder.DeathRecipient deathRecipient) {
        if (DEBUG) {
            Log.d("AdaptiveChargingManager", "destroyHalInterface");
        }
        if (deathRecipient != null) {
            try {
                iGoogleBattery.unlinkToDeath(deathRecipient);
            } catch (RemoteException e) {
                Log.e("AdaptiveChargingManager", "unlinkToDeath failed: ", e);
            }
        }
    }

    private boolean hasAdaptiveChargingFeature() {
        return mContext.getPackageManager().hasSystemFeature("com.google.android.feature.ADAPTIVE_CHARGING");
    }

    public boolean isAvailable() {
        if (!hasAdaptiveChargingFeature() || !DeviceConfig.getBoolean("adaptive_charging", "adaptive_charging_enabled", true)) {
            return false;
        }
        return true;
    }

    public boolean isEnabled() {
        return Settings.Secure.getInt(mContext.getContentResolver(), "adaptive_charging_enabled", 1) == 1;
    }

    public void setEnabled(boolean z) {
        Settings.Secure.putInt(this.mContext.getContentResolver(), "adaptive_charging_enabled", z ? 1 : 0);
    }

    public boolean setAdaptiveChargingDeadline(int i) {
        IGoogleBattery initHalInterface = initHalInterface(null);
        boolean z = false;
        if (initHalInterface == null) {
            return false;
        }
        try {
            if (initHalInterface.setChargingDeadline(i) == 0) {
                z = true;
            }
        } catch (RemoteException e) {
            Log.e("AdaptiveChargingManager", "setChargingDeadline failed: ", e);
        }
        destroyHalInterface(initHalInterface, null);
        return z;
    }

    public static boolean isStageActive(String str) {
        return "Active".equals(str);
    }

    public static boolean isStageEnabled(String str) {
        return "Enabled".equals(str);
    }

    public static boolean isStageActiveOrEnabled(String str) {
        return isStageActive(str) || isStageEnabled(str);
    }

    public void queryStatus(final AdaptiveChargingStatusReceiver adaptiveChargingStatusReceiver) {
        final IHwBinder.DeathRecipient r0 = new IHwBinder.DeathRecipient() { // from class: com.google.android.systemui.adaptivecharging.AdaptiveChargingManager.1
            @Override
            public void serviceDied(long j) {
                if (AdaptiveChargingManager.DEBUG) {
                    Log.d("AdaptiveChargingManager", "serviceDied");
                }
                adaptiveChargingStatusReceiver.onDestroyInterface();
            }
        };
        final IGoogleBattery initHalInterface = initHalInterface(r0);
        if (initHalInterface == null) {
            adaptiveChargingStatusReceiver.onDestroyInterface();
            return;
        }
        try {
            initHalInterface.getChargingStageAndDeadline(new IGoogleBattery.getChargingStageAndDeadlineCallback() { // from class: com.google.android.systemui.adaptivecharging.AdaptiveChargingManager.2
                @Override // vendor.google.google_battery.V1_0.IGoogleBattery.getChargingStageAndDeadlineCallback
                public void onValues(byte status, String stage, int seconds) {
                    if (AdaptiveChargingManager.DEBUG) {
                        Log.d("AdaptiveChargingManager", "getChargingStageDeadlineCallback result: " + ((int) status) + ", stage: \"" + stage + "\", seconds: " + seconds);
                    }
                    if (status == 0) {
                        adaptiveChargingStatusReceiver.onReceiveStatus(stage, seconds);
                    }
                    destroyHalInterface(initHalInterface, r0);
                    adaptiveChargingStatusReceiver.onDestroyInterface();
                }
            });
        } catch (RemoteException e) {
            Log.e("AdaptiveChargingManager", "Failed to get Adaptive Charging status: ", e);
            destroyHalInterface(initHalInterface, r0);
            adaptiveChargingStatusReceiver.onDestroyInterface();
        }
    }
}
