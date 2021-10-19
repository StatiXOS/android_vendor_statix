package com.statix.android.systemui.biometrics;

import android.os.Handler;
import android.os.Looper;
import android.view.Surface;

import androidx.annotation.Nullable;

import com.android.systemui.biometrics.UdfpsHbmProvider;
import com.android.systemui.biometrics.UdfpsHbmTypes.HbmType;
import com.android.systemui.dagger.SysUISingleton;

import javax.inject.Inject;

@SysUISingleton
public class StatixUdfpsHbmProvider implements UdfpsHbmProvider {

    private Handler mHandler;

    @Inject
    public StatixUdfpsHbmProvider() {
        super();
        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void enableHbm(@HbmType int hbmType, @Nullable Surface surface,
            @Nullable Runnable onHbmEnabled) {
        // TO-DO send call to lineage biometric hal and/or add dummy jni that device could override
        if (onHbmEnabled != null) {
            mHandler.post(onHbmEnabled);
        }
    }

    @Override
    public void disableHbm(@Nullable Runnable onHbmDisabled) {
        // TO-DO send call to lineage biometric hal and/or add dummy jni that device could override
        Handler handler = new Handler(Looper.getMainLooper());
        if (onHbmDisabled != null) {
            mHandler.post(onHbmDisabled);
        }
    }
}
