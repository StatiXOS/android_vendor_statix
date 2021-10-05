package com.google.android.systemui.power;

import static android.content.pm.PackageManager.MATCH_DISABLED_COMPONENTS;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Settings;
import android.util.KeyValueListParser;
import android.util.Log;
import com.android.settingslib.fuelgauge.Estimate;
import com.android.settingslib.utils.PowerUtil;
import com.android.systemui.power.EnhancedEstimates;
import java.time.Duration;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class EnhancedEstimatesGoogleImpl implements EnhancedEstimates {
    protected static final String TAG = "EnhancedEstimatesGoogleImpl";

    private Context mContext;
    private final KeyValueListParser mParser = new KeyValueListParser(',');

    @Inject
    public EnhancedEstimatesGoogleImpl(Context context) {
        this.mContext = context;
    }

    @Override
    public boolean isHybridNotificationEnabled() {
        try {
            if (!this.mContext.getPackageManager().getPackageInfo("com.google.android.apps.turbo", MATCH_DISABLED_COMPONENTS).applicationInfo.enabled) {
                return false;
            }
            updateFlags();
            return this.mParser.getBoolean("hybrid_enabled", true);
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    @Override
    public Estimate getEstimate() {
        try {
            Cursor query = this.mContext.getContentResolver().query(new Uri.Builder().scheme("content").authority("com.google.android.apps.turbo.estimated_time_remaining").appendPath("time_remaining").build(), null, null, null, null);
            if (query != null) {
                try {
                    if (query.moveToFirst()) {
                        boolean isBasedOnUsage = true;
                        if (query.getColumnIndex("is_based_on_usage") != -1) {
                            if (query.getInt(query.getColumnIndex("is_based_on_usage")) == 0) {
                                isBasedOnUsage = false;
                            }
                        }
                        int averageBatteryLife = query.getColumnIndex("average_battery_life");
                        long averageDischargeTime = -1;
                        if (averageBatteryLife != -1) {
                            long averageBatteryLifeMillis = query.getLong(averageBatteryLife);
                            if (averageBatteryLifeMillis != -1) {
                                long thresholdMillis = Duration.ofMinutes(15).toMillis();
                                if (Duration.ofMillis(averageBatteryLifeMillis).compareTo(Duration.ofDays(1)) >= 0) {
                                    thresholdMillis = Duration.ofHours(1).toMillis();
                                }
                                averageDischargeTime = PowerUtil.roundTimeToNearestThreshold(averageBatteryLifeMillis, thresholdMillis);
                            }
                        }
                        Estimate estimate = new Estimate(query.getLong(query.getColumnIndex("battery_estimate")), isBasedOnUsage, averageDischargeTime);
                        query.close();
                        return estimate;
                    }
                } catch (Throwable t) {
                    t.addSuppressed(t);
                }
            }
            if (query != null) {
                query.close();
            }
        } catch (Exception e) {
            Log.d(TAG, "Something went wrong when getting an estimate from Turbo", e);
        }
        return new Estimate(-1, false, -1);
    }

    @Override
    public long getLowWarningThreshold() {
        updateFlags();
        return this.mParser.getLong("low_threshold", Duration.ofHours(3).toMillis());
    }

    @Override
    public long getSevereWarningThreshold() {
        updateFlags();
        return this.mParser.getLong("severe_threshold", Duration.ofHours(1).toMillis());
    }

    @Override
    public boolean getLowWarningEnabled() {
        updateFlags();
        return this.mParser.getBoolean("low_warning_enabled", false);
    }

    public void updateFlags() {
        try {
            this.mParser.setString(Settings.Global.getString(this.mContext.getContentResolver(), "hybrid_sysui_battery_warning_flags"));
        } catch (IllegalArgumentException unused) {
            Log.e(TAG, "Bad hybrid sysui warning flags");
        }
    }
}
