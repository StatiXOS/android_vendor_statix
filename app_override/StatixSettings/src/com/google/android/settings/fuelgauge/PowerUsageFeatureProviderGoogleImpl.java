package com.google.android.settings.fuelgauge;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.SparseIntArray;

import androidx.window.R;

import com.android.settings.fuelgauge.BatteryHistEntry;
import com.android.settings.fuelgauge.PowerUsageFeatureProviderImpl;
import com.android.settingslib.fuelgauge.Estimate;
import com.android.settingslib.utils.PowerUtil;

import com.google.android.settings.experiments.PhenotypeProxy;
import com.google.android.settings.fuelgauge.DatabaseUtils;

import java.time.Clock;
import java.time.Duration;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PowerUsageFeatureProviderGoogleImpl extends PowerUsageFeatureProviderImpl {
    static final String ACTION_RESUME_CHARGING = "PNW.defenderResumeCharging.settings";
    static final String AVERAGE_BATTERY_LIFE_COL = "average_battery_life";
    static final String BATTERY_ESTIMATE_BASED_ON_USAGE_COL = "is_based_on_usage";
    static final String BATTERY_ESTIMATE_COL = "battery_estimate";
    static final String BATTERY_LEVEL_COL = "battery_level";
    static final int CUSTOMIZED_TO_USER = 1;
    static final String GFLAG_ADDITIONAL_BATTERY_INFO_ENABLED = "settingsgoogle:additional_battery_info_enabled";
    static final String GFLAG_BATTERY_ADVANCED_UI_ENABLED = "settingsgoogle:battery_advanced_ui_enabled";
    static final String GFLAG_POWER_ACCOUNTING_TOGGLE_ENABLED = "settingsgoogle:power_accounting_toggle_enabled";
    static final String IS_EARLY_WARNING_COL = "is_early_warning";
    static final int NEED_EARLY_WARNING = 1;
    static final String PACKAGE_NAME_SYSTEMUI = "com.android.systemui";
    static final String TIMESTAMP_COL = "timestamp_millis";
    private static boolean sChartGraphEnabled;
    private static boolean sChartGraphSlotsEnabled;
    private static final String[] PACKAGES_SERVICE = {"com.google.android.gms", "com.google.android.apps.gcs"};
    static boolean sChartConfigurationLoaded = false;

    public PowerUsageFeatureProviderGoogleImpl(Context context) {
        super(context);
    }

    void setPackageManager(PackageManager packageManager) {
        this.mPackageManager = packageManager;
    }

    @Override // com.android.settings.fuelgauge.PowerUsageFeatureProviderImpl, com.android.settings.fuelgauge.PowerUsageFeatureProvider
    public Estimate getEnhancedBatteryPrediction(Context context) {
        long j;
        Cursor query = context.getContentResolver().query(getEnhancedBatteryPredictionUri(), null, null, null, null);
        if (query != null) {
            try {
                if (query.moveToFirst()) {
                    int columnIndex = query.getColumnIndex(BATTERY_ESTIMATE_BASED_ON_USAGE_COL);
                    boolean z = true;
                    if (!(columnIndex == -1 || 1 == query.getInt(columnIndex))) {
                        z = false;
                    }
                    int columnIndex2 = query.getColumnIndex(AVERAGE_BATTERY_LIFE_COL);
                    if (columnIndex2 != -1) {
                        long j2 = query.getLong(columnIndex2);
                        if (j2 != -1) {
                            long millis = Duration.ofMinutes(15).toMillis();
                            if (Duration.ofMillis(j2).compareTo(Duration.ofDays(1)) >= 0) {
                                millis = Duration.ofHours(1).toMillis();
                            }
                            j = PowerUtil.roundTimeToNearestThreshold(j2, millis);
                            Estimate estimate = new Estimate(query.getLong(query.getColumnIndex(BATTERY_ESTIMATE_COL)), z, j);
                            query.close();
                            return estimate;
                        }
                    }
                    j = -1;
                    Estimate estimate2 = new Estimate(query.getLong(query.getColumnIndex(BATTERY_ESTIMATE_COL)), z, j);
                    query.close();
                    return estimate2;
                }
            } catch (Throwable th) {
                try {
                    query.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        }
        if (query == null) {
            return null;
        }
        query.close();
        return null;
    }

    @Override // com.android.settings.fuelgauge.PowerUsageFeatureProviderImpl, com.android.settings.fuelgauge.PowerUsageFeatureProvider
    public SparseIntArray getEnhancedBatteryPredictionCurve(Context context, long j) {
        try {
            Cursor query = context.getContentResolver().query(getEnhancedBatteryPredictionCurveUri(), null, null, null, null);
            if (query == null) {
                if (query != null) {
                    query.close();
                }
                return null;
            }
            int columnIndex = query.getColumnIndex(TIMESTAMP_COL);
            int columnIndex2 = query.getColumnIndex(BATTERY_LEVEL_COL);
            SparseIntArray sparseIntArray = new SparseIntArray(query.getCount());
            while (query.moveToNext()) {
                sparseIntArray.append((int) (query.getLong(columnIndex) - j), query.getInt(columnIndex2));
            }
            query.close();
            return sparseIntArray;
        } catch (NullPointerException unused) {
            return null;
        }
    }

    @Override // com.android.settings.fuelgauge.PowerUsageFeatureProviderImpl, com.android.settings.fuelgauge.PowerUsageFeatureProvider
    public boolean isEnhancedBatteryPredictionEnabled(Context context) {
        if (!isTurboEnabled(context)) {
            return false;
        }
        try {
            if (!this.mPackageManager.getPackageInfo("com.google.android.apps.turbo", 512).applicationInfo.enabled) {
                return false;
            }
            return true;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    private Uri getEnhancedBatteryPredictionUri() {
        return new Uri.Builder().scheme("content").authority("com.google.android.apps.turbo.estimated_time_remaining").appendPath("time_remaining").build();
    }

    private Uri getEnhancedBatteryPredictionCurveUri() {
        return new Uri.Builder().scheme("content").authority("com.google.android.apps.turbo.estimated_time_remaining").appendPath("discharge_curve").build();
    }

    @Override // com.android.settings.fuelgauge.PowerUsageFeatureProviderImpl, com.android.settings.fuelgauge.PowerUsageFeatureProvider
    public String getAdvancedUsageScreenInfoString() {
        return this.mContext.getString(R.string.advanced_battery_graph_subtext);
    }

    @Override // com.android.settings.fuelgauge.PowerUsageFeatureProviderImpl, com.android.settings.fuelgauge.PowerUsageFeatureProvider
    public boolean getEarlyWarningSignal(Context context, String str) {
        Uri.Builder appendPath = new Uri.Builder().scheme("content").authority("com.google.android.apps.turbo.estimated_time_remaining").appendPath("early_warning").appendPath("id");
        if (TextUtils.isEmpty(str)) {
            appendPath.appendPath(context.getPackageName());
        } else {
            appendPath.appendPath(str);
        }
        Cursor query = context.getContentResolver().query(appendPath.build(), null, null, null, null);
        boolean z = false;
        if (query != null) {
            try {
                if (query.moveToFirst()) {
                    if (1 == query.getInt(query.getColumnIndex(IS_EARLY_WARNING_COL))) {
                        z = true;
                    }
                    query.close();
                    return z;
                }
            } catch (Throwable th) {
                try {
                    query.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        }
        if (query != null) {
            query.close();
        }
        return false;
    }

    boolean isTurboEnabled(Context context) {
        return PhenotypeProxy.getFlagByPackageAndKey(context, "com.google.android.apps.turbo", "NudgesBatteryEstimates__estimated_time_remaining_provider_enabled", false);
    }

    @Override // com.android.settings.fuelgauge.PowerUsageFeatureProviderImpl, com.android.settings.fuelgauge.PowerUsageFeatureProvider
    public boolean isChartGraphEnabled(Context context) {
        loadChartConfiguration(context);
        return sChartGraphEnabled;
    }

    @Override // com.android.settings.fuelgauge.PowerUsageFeatureProviderImpl, com.android.settings.fuelgauge.PowerUsageFeatureProvider
    public boolean isChartGraphSlotsEnabled(Context context) {
        loadChartConfiguration(context);
        return sChartGraphSlotsEnabled;
    }

    private void loadChartConfiguration(Context context) {
        if (!sChartConfigurationLoaded) {
            boolean isSettingsIntelligenceExist = isSettingsIntelligenceExist(context);
            boolean z = false;
            sChartGraphEnabled = isSettingsIntelligenceExist && DatabaseUtils.isContentProviderEnabled(context);
            if (isSettingsIntelligenceExist && PhenotypeProxy.getFlagByPackageAndKey(context, "com.google.android.settings.intelligence", "BatteryUsage__is_time_slot_supported", false)) {
                z = true;
            }
            sChartGraphSlotsEnabled = z;
            sChartConfigurationLoaded = true;
        }
    }

    @Override // com.android.settings.fuelgauge.PowerUsageFeatureProviderImpl, com.android.settings.fuelgauge.PowerUsageFeatureProvider
    public Map<Long, Map<String, BatteryHistEntry>> getBatteryHistory(Context context) {
        return DatabaseUtils.getHistoryMap(context, Clock.systemUTC(), true);
    }

    @Override // com.android.settings.fuelgauge.PowerUsageFeatureProviderImpl, com.android.settings.fuelgauge.PowerUsageFeatureProvider
    public Uri getBatteryHistoryUri() {
        return DatabaseUtils.BATTERY_CONTENT_URI;
    }

    @Override // com.android.settings.fuelgauge.PowerUsageFeatureProviderImpl, com.android.settings.fuelgauge.PowerUsageFeatureProvider
    public Set<CharSequence> getHideBackgroundUsageTimeSet(Context context) {
        HashSet hashSet = new HashSet();
        Collections.addAll(hashSet, context.getResources().getTextArray(R.array.allowlist_hide_background_in_battery_usage));
        return hashSet;
    }

    @Override // com.android.settings.fuelgauge.PowerUsageFeatureProviderImpl, com.android.settings.fuelgauge.PowerUsageFeatureProvider
    public CharSequence[] getHideApplicationEntries(Context context) {
        return context.getResources().getTextArray(R.array.allowlist_hide_entry_in_battery_usage);
    }

    @Override // com.android.settings.fuelgauge.PowerUsageFeatureProviderImpl, com.android.settings.fuelgauge.PowerUsageFeatureProvider
    public CharSequence[] getHideApplicationSummary(Context context) {
        return context.getResources().getTextArray(R.array.allowlist_hide_summary_in_battery_usage);
    }

    private boolean isSettingsIntelligenceExist(Context context) {
        try {
            if (!context.getPackageManager().getPackageInfo("com.google.android.settings.intelligence", 512).applicationInfo.enabled) {
                return false;
            }
            return true;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }
}
