package com.statix.android.systemui.qs.tileimpl;

// keep in sync with frameworks/base/packages/SystemUI/src/com/android/systemui/qs/tileimpl/QSFactoryImpl.java
import com.android.systemui.dagger.SysUISingleton;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.QSHost;
import com.android.systemui.qs.external.CustomTile;
import com.android.systemui.qs.tileimpl.QSFactoryImpl;
import com.android.systemui.qs.tileimpl.QSTileImpl;
import com.android.systemui.qs.external.CustomTile;
import com.android.systemui.qs.tiles.AirplaneModeTile;
import com.android.systemui.qs.tiles.AlarmTile;
import com.android.systemui.qs.tiles.BatterySaverTile;
import com.android.systemui.qs.tiles.BluetoothTile;
import com.android.systemui.qs.tiles.CameraToggleTile;
import com.android.systemui.qs.tiles.CastTile;
import com.android.systemui.qs.tiles.CellularTile;
import com.android.systemui.qs.tiles.ColorInversionTile;
import com.android.systemui.qs.tiles.DataSaverTile;
import com.android.systemui.qs.tiles.DeviceControlsTile;
import com.android.systemui.qs.tiles.DndTile;
import com.android.systemui.qs.tiles.FlashlightTile;
import com.android.systemui.qs.tiles.HotspotTile;
import com.android.systemui.qs.tiles.InternetTile;
import com.android.systemui.qs.tiles.LocationTile;
import com.android.systemui.qs.tiles.MicrophoneToggleTile;
import com.android.systemui.qs.tiles.NfcTile;
import com.android.systemui.qs.tiles.NightDisplayTile;
import com.android.systemui.qs.tiles.QuickAccessWalletTile;
import com.android.systemui.qs.tiles.ReduceBrightColorsTile;
import com.android.systemui.qs.tiles.RotationLockTile;
import com.android.systemui.qs.tiles.ScreenRecordTile;
import com.android.systemui.qs.tiles.UiModeNightTile;
import com.android.systemui.qs.tiles.UserTile;
import com.android.systemui.qs.tiles.WifiTile;
import com.android.systemui.qs.tiles.WorkModeTile;
import com.android.systemui.util.leak.GarbageMonitor;

// Custom tiles
import com.statix.android.systemui.qs.tiles.GloveModeTile;
import com.statix.android.systemui.qs.tiles.PowerShareTile;
import com.statix.android.systemui.qs.tiles.CaffeineTile;

import javax.inject.Inject;
import javax.inject.Provider;

import dagger.Lazy;

public class QSFactoryImplStatix extends QSFactoryImpl {

    private final Provider<CaffeineTile> mCaffeineTileProvider;
    private final Provider<GloveModeTile> mGloveModeTileProvider;
    private final Provider<PowerShareTile> mPowerShareTileProvider;

    @Inject
    public QSFactoryImplStatix(
            Lazy<QSHost> qsHostLazy,
            Provider<CustomTile.Builder> customTileBuilderProvider,
            Provider<WifiTile> wifiTileProvider,
            Provider<InternetTile> internetTileProvider,
            Provider<BluetoothTile> bluetoothTileProvider,
            Provider<CellularTile> cellularTileProvider,
            Provider<DndTile> dndTileProvider,
            Provider<ColorInversionTile> colorInversionTileProvider,
            Provider<AirplaneModeTile> airplaneModeTileProvider,
            Provider<WorkModeTile> workModeTileProvider,
            Provider<RotationLockTile> rotationLockTileProvider,
            Provider<FlashlightTile> flashlightTileProvider,
            Provider<LocationTile> locationTileProvider,
            Provider<CastTile> castTileProvider,
            Provider<HotspotTile> hotspotTileProvider,
            Provider<UserTile> userTileProvider,
            Provider<BatterySaverTile> batterySaverTileProvider,
            Provider<DataSaverTile> dataSaverTileProvider,
            Provider<NightDisplayTile> nightDisplayTileProvider,
            Provider<NfcTile> nfcTileProvider,
            Provider<GarbageMonitor.MemoryTile> memoryTileProvider,
            Provider<UiModeNightTile> uiModeNightTileProvider,
            Provider<ScreenRecordTile> screenRecordTileProvider,
            Provider<ReduceBrightColorsTile> reduceBrightColorsTileProvider,
            Provider<CameraToggleTile> cameraToggleTileProvider,
            Provider<MicrophoneToggleTile> microphoneToggleTileProvider,
            Provider<DeviceControlsTile> deviceControlsTileProvider,
            Provider<AlarmTile> alarmTileProvider,
            Provider<QuickAccessWalletTile> quickAccessWalletTileProvider,
            Provider<CaffeineTile> caffeineTileProvider,
            Provider<PowerShareTile> powerShareTileProvider,
            Provider<GloveModeTile> gloveModeTileProvider) {
        super(qsHostLazy, customTileBuilderProvider, wifiTileProvider, internetTileProvider, bluetoothTileProvider, cellularTileProvider, dndTileProvider, colorInversionTileProvider,
            airplaneModeTileProvider, workModeTileProvider, rotationLockTileProvider, flashlightTileProvider, locationTileProvider, castTileProvider, hotspotTileProvider, userTileProvider,
            batterySaverTileProvider, dataSaverTileProvider, nightDisplayTileProvider, nfcTileProvider, memoryTileProvider, uiModeNightTileProvider, screenRecordTileProvider, reduceBrightColorsTileProvider,
            cameraToggleTileProvider, microphoneToggleTileProvider, deviceControlsTileProvider, alarmTileProvider, quickAccessWalletTileProvider);
        // custom tile
        mCaffeineTileProvider = caffeineTileProvider;
        mGloveModeTileProvider = gloveModeTileProvider;
        mPowerShareTileProvider = powerShareTileProvider;
    }

    private QSTileImpl createTileStatix(String tileSpec) {
        switch(tileSpec) {
            case "caffeine":
                return mCaffeineTileProvider.get();
            case "glovemode":
                return mGloveModeTileProvider.get();
            case "powershare":
                return mPowerShareTileProvider.get();
            default:
                return null;
        }
    }

    @Override
    public QSTile createTile(String tileSpec) {
        QSTile tile = createTileStatix(tileSpec);
        return tile != null ? tile : super.createTile(tileSpec);
    }
}
