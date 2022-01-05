package com.statix.android.systemui.statusbar;

import android.app.IActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.DeviceConfig;
import android.text.TextUtils;
import android.text.format.DateFormat;

import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.IBatteryStats;
import com.android.internal.widget.LockPatternUtils;

import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;

import com.android.settingslib.fuelgauge.BatteryStatus;

import com.android.systemui.R;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.dagger.SysUISingleton;
import com.android.systemui.dock.DockManager;
import com.android.systemui.keyguard.KeyguardIndication;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.KeyguardIndicationController;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.util.DeviceConfigProxy;
import com.android.systemui.util.concurrency.DelayableExecutor;
import com.android.systemui.util.time.DateFormatUtil;
import com.android.systemui.util.wakelock.WakeLock;

import com.google.android.systemui.adaptivecharging.AdaptiveChargingManager;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

@SysUISingleton
public class KeyguardIndicationControllerGoogle extends KeyguardIndicationController {
    private boolean mAdaptiveChargingActive;
    private boolean mAdaptiveChargingEnabledInSettings;
    @VisibleForTesting
    protected AdaptiveChargingManager mAdaptiveChargingManager;
    private final IBatteryStats mBatteryInfo;
    private int mBatteryLevel;
    private final BroadcastDispatcher mBroadcastDispatcher;
    private final Context mContext;
    private final DateFormatUtil mDateFormatUtil;
    private final DeviceConfigProxy mDeviceConfig;
    private long mEstimatedChargeCompletion;
    private boolean mInited;
    private boolean mIsCharging;
    private StatusBar mStatusBar;
    private final TunerService mTunerService;
    private KeyguardUpdateMonitorCallback mUpdateMonitorCallback;
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.google.android.systemui.adaptivecharging.ADAPTIVE_CHARGING_DEADLINE_SET")) {
                triggerAdaptiveChargingStatusUpdate();
            }
        }
    };
    @VisibleForTesting
    protected AdaptiveChargingManager.AdaptiveChargingStatusReceiver mAdaptiveChargingStatusReceiver = new AdaptiveChargingManager.AdaptiveChargingStatusReceiver() {
        @Override
        public void onDestroyInterface() {
        }

        @Override
        public void onReceiveStatus(String str, int i) {
            boolean z = mAdaptiveChargingActive;
            mAdaptiveChargingActive = AdaptiveChargingManager.isStageActiveOrEnabled(str) && i > 0;
            long j = mEstimatedChargeCompletion;
            long currentTimeMillis = System.currentTimeMillis();
            TimeUnit timeUnit = TimeUnit.SECONDS;
            mEstimatedChargeCompletion = currentTimeMillis + timeUnit.toMillis((long) (i + 29));
            long abs = Math.abs(mEstimatedChargeCompletion - j);
            if (z != mAdaptiveChargingActive || (mAdaptiveChargingActive && abs > timeUnit.toMillis(30))) {
                updateIndication(true);
            }
        }
    };

    @Inject
    public KeyguardIndicationControllerGoogle(Context context, WakeLock.Builder builder, KeyguardStateController keyguardStateController, StatusBarStateController statusBarStateController, KeyguardUpdateMonitor keyguardUpdateMonitor, DockManager dockManager, BroadcastDispatcher broadcastDispatcher, DevicePolicyManager devicePolicyManager, IBatteryStats iBatteryStats, UserManager userManager, TunerService tunerService, DeviceConfigProxy deviceConfigProxy, DelayableExecutor delayableExecutor, FalsingManager falsingManager, LockPatternUtils lockPatternUtils, IActivityManager iActivityManager, KeyguardBypassController keyguardBypassController) {
        super(context, builder, keyguardStateController, statusBarStateController, keyguardUpdateMonitor, dockManager, broadcastDispatcher, devicePolicyManager, iBatteryStats, userManager, delayableExecutor, falsingManager, lockPatternUtils, iActivityManager, keyguardBypassController);
        mContext = context;
        mBroadcastDispatcher = broadcastDispatcher;
        mTunerService = tunerService;
        mDeviceConfig = deviceConfigProxy;
        mAdaptiveChargingManager = new AdaptiveChargingManager(context);
        mBatteryInfo = iBatteryStats;
        mDateFormatUtil = new DateFormatUtil(context);
    }

    @Override
    public void init() {
        super.init();
        if (!mInited) {
            mInited = true;
            mTunerService.addTunable(new TunerService.Tunable() {
                @Override
                public final void onTuningChanged(String key, String newValue) {
                    refreshAdaptiveChargingEnabled();
                }
            }, "adaptive_charging_enabled");
            mDeviceConfig.addOnPropertiesChangedListener("adaptive_charging", mContext.getMainExecutor(), new DeviceConfig.OnPropertiesChangedListener() {
                public final void onPropertiesChanged(DeviceConfig.Properties properties) {
                    if (properties.getKeyset().contains("adaptive_charging_enabled")) {
                        triggerAdaptiveChargingStatusUpdate();
                    }
                }
            });
            triggerAdaptiveChargingStatusUpdate();
            mBroadcastDispatcher.registerReceiver(mBroadcastReceiver, new IntentFilter("com.google.android.systemui.adaptivecharging.ADAPTIVE_CHARGING_DEADLINE_SET"), null, UserHandle.ALL);
        }
    }

    private void refreshAdaptiveChargingEnabled() {
        mAdaptiveChargingEnabledInSettings = mAdaptiveChargingManager.isAvailable() && mAdaptiveChargingManager.isEnabled();
    }

    @Override
    public String computePowerIndication() {
        if (!mIsCharging || !mAdaptiveChargingEnabledInSettings || !mAdaptiveChargingActive) {
            return super.computePowerIndication();
        }
        return mContext.getResources().getString(R.string.adaptive_charging_time_estimate, NumberFormat.getPercentInstance().format((double) (((float) mBatteryLevel) / 100.0f)), DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(), mDateFormatUtil.is24HourFormat() ? "Hm" : "hma"), mEstimatedChargeCompletion).toString());
    }

    @Override
    protected KeyguardUpdateMonitorCallback getKeyguardCallback() {
        if (mUpdateMonitorCallback == null) {
            mUpdateMonitorCallback = new GoogleKeyguardCallback();
        }
        return mUpdateMonitorCallback;
    }

    public void setReverseChargingMessage(CharSequence charSequence) {
        if (TextUtils.isEmpty(charSequence)) {
            mRotateTextViewController.hideIndication(10);
        } else {
            mRotateTextViewController.updateIndication(10, new KeyguardIndication.Builder().setMessage(charSequence).setIcon(mContext.getDrawable(R.anim.reverse_charging_animation)).setTextColor(mInitialTextColorState).build(), false);
        }
    }

    public void setStatusBar(StatusBar statusBar) {
        mStatusBar = statusBar;
    }

    public void triggerAdaptiveChargingStatusUpdate() {
        refreshAdaptiveChargingEnabled();
        if (mAdaptiveChargingEnabledInSettings) {
            mAdaptiveChargingManager.queryStatus(mAdaptiveChargingStatusReceiver);
        } else {
            mAdaptiveChargingActive = false;
        }
    }

    protected class GoogleKeyguardCallback extends KeyguardIndicationController.BaseKeyguardCallback {
        @Override
        public void onRefreshBatteryInfo(BatteryStatus batteryStatus) {
            mIsCharging = batteryStatus.status == 2;
            mBatteryLevel = batteryStatus.level;
            super.onRefreshBatteryInfo(batteryStatus);
            if (mIsCharging) {
                triggerAdaptiveChargingStatusUpdate();
            } else {
                mAdaptiveChargingActive = false;
            }
        }
    }
}
