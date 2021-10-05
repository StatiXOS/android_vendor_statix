package com.statix.android.systemui.theme;

import android.annotation.NonNull;
import android.annotation.Nullable;
import android.app.WallpaperColors;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.om.FabricatedOverlay;
import android.os.Handler;
import android.os.UserManager;
import android.util.Log;
import android.util.TypedValue;

import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.dagger.qualifiers.Background;
import com.android.systemui.dagger.qualifiers.Main;
import com.android.systemui.dagger.SysUISingleton;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.settings.UserTracker;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.theme.ThemeOverlayApplier;
import com.android.systemui.theme.ThemeOverlayController;
import com.android.systemui.util.settings.SecureSettings;

import com.android.internal.graphics.ColorUtils;

import dev.kdrag0n.monet.colors.Color;
import dev.kdrag0n.monet.colors.Srgb;
import dev.kdrag0n.monet.theme.DynamicColorScheme;
import dev.kdrag0n.monet.theme.MaterialYouTargets;

import java.util.concurrent.Executor;
import java.util.stream.IntStream;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

@SysUISingleton
public class ThemeOverlayControllerStatix extends ThemeOverlayController {
    protected static final String TAG = "ThemeOverlayControllerStatix";

    private static final int NEUTRAL = 0;
    private static final int ACCENT = 1;

    @Inject
    public ThemeOverlayControllerStatix(Context context, BroadcastDispatcher broadcastDispatcher,
            @Background Handler bgHandler, @Main Executor mainExecutor,
            @Background Executor bgExecutor, ThemeOverlayApplier themeOverlayApplier,
            SecureSettings secureSettings, WallpaperManager wallpaperManager,
            UserManager userManager, DeviceProvisionedController deviceProvisionedController,
            UserTracker userTracker, DumpManager dumpManager, FeatureFlags featureFlags,
            WakefulnessLifecycle wakefulnessLifecycle) {
        super(context, broadcastDispatcher, bgHandler, mainExecutor, bgExecutor, themeOverlayApplier, secureSettings, wallpaperManager, userManager, deviceProvisionedController, userTracker, dumpManager, featureFlags, wakefulnessLifecycle);
    }

    @Override
    protected int getAccentColor(@NonNull WallpaperColors wallpaperColors) {
        return getNeutralColor(wallpaperColors);
    }


    @Override
    protected @Nullable FabricatedOverlay getOverlay(int color, int type) {
        DynamicColorScheme dcs = new DynamicColorScheme(new MaterialYouTargets(), new Srgb(color), 1.0, true);
        String name = type == NEUTRAL ? "neutral" : "accent";
        List<Map<Integer, Color>> clrlist = type == NEUTRAL ? dcs.getNeutralColors() : dcs.getAccentColors();

        FabricatedOverlay.Builder overlay = new FabricatedOverlay.Builder("com.android.systemui", name, "android");
        IntStream.range(0, clrlist.size()).forEach(i -> {
            clrlist.get(i).forEach((j, colorInternal) -> {
                Srgb clr = colorInternal.toLinearSrgb().toSrgb();
                String resource = "android:color/system_" + name + (i + 1) + "_" + j;
                int value = ColorUtils.setAlphaComponent(clr.quantize8(), 0xFF);
                Log.d(TAG, "resource: " + resource + " value: #" + Integer.toHexString(value).substring(2));
                overlay.setResourceValue(resource, TypedValue.TYPE_INT_COLOR_ARGB8, value);
            });
        });
        return overlay.build();
    }
}
