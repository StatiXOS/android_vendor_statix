package com.statix.android.systemui.dagger;

import static com.android.systemui.Dependency.ALLOW_NOTIFICATION_LONG_PRESS_NAME;
import static com.android.systemui.Dependency.LEAK_REPORT_EMAIL_NAME;
import static com.statix.android.systemui.Dependency.*;

import android.app.AlarmManager;
import android.app.IActivityManager;
import android.app.KeyguardManager;
import android.content.pm.LauncherApps;
import android.media.AudioManager;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.content.Context;
import android.hardware.SensorPrivacyManager;
import android.os.Handler;
import android.os.PowerManager;
import android.os.UserManager;
import android.os.Vibrator;
import android.util.Log;
import android.view.IWindowManager;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.android.internal.logging.UiEventLogger;

import com.google.android.systemui.assist.GoogleAssistLogger;
import com.google.android.systemui.assist.OpaEnabledDispatcher;
import com.google.android.systemui.assist.OpaEnabledReceiver;
import com.google.android.systemui.assist.OpaEnabledSettings;
import com.google.android.systemui.assist.uihints.AssistantPresenceHandler;
import com.google.android.systemui.assist.uihints.AssistantWarmer;
import com.google.android.systemui.assist.uihints.ColorChangeHandler;
import com.google.android.systemui.assist.uihints.ConfigurationHandler;
import com.google.android.systemui.assist.uihints.FlingVelocityWrapper;
import com.google.android.systemui.assist.uihints.GlowController;
import com.google.android.systemui.assist.uihints.GoBackHandler;
import com.google.android.systemui.assist.uihints.GoogleDefaultUiController;
import com.google.android.systemui.assist.uihints.IconController;
import com.google.android.systemui.assist.uihints.KeyboardMonitor;
import com.google.android.systemui.assist.uihints.LightnessProvider;
import com.google.android.systemui.assist.uihints.NavBarFader;
import com.google.android.systemui.assist.uihints.NgaMessageHandler;
import com.google.android.systemui.assist.uihints.NgaUiController;
import com.google.android.systemui.assist.uihints.OverlappedElementController;
import com.google.android.systemui.assist.uihints.OverlayUiHost;
import com.google.android.systemui.assist.uihints.ScrimController;
import com.google.android.systemui.assist.uihints.TakeScreenshotHandler;
import com.google.android.systemui.assist.uihints.TaskStackNotifier;
import com.google.android.systemui.assist.uihints.TimeoutManager;
import com.google.android.systemui.assist.uihints.TouchInsideHandler;
import com.google.android.systemui.assist.uihints.TouchOutsideHandler;
import com.google.android.systemui.assist.uihints.TranscriptionController;
import com.google.android.systemui.assist.uihints.edgelights.EdgeLightsController;
import com.google.android.systemui.assist.uihints.input.NgaInputHandler;
import com.google.android.systemui.assist.uihints.input.TouchActionRegion;
import com.google.android.systemui.assist.uihints.input.TouchInsideRegion;
import com.google.android.systemui.columbus.ColumbusContentObserver;
import com.google.android.systemui.columbus.ColumbusService;
import com.google.android.systemui.columbus.ColumbusServiceWrapper;
import com.google.android.systemui.columbus.ColumbusSettings;
import com.google.android.systemui.columbus.ColumbusStructuredDataManager;
import com.google.android.systemui.columbus.ContentResolverWrapper;
import com.google.android.systemui.columbus.PowerManagerWrapper;
import com.google.android.systemui.columbus.actions.Action;
import com.google.android.systemui.columbus.actions.DismissTimer;
import com.google.android.systemui.columbus.actions.LaunchApp;
import com.google.android.systemui.columbus.actions.LaunchOpa;
import com.google.android.systemui.columbus.actions.LaunchOverview;
import com.google.android.systemui.columbus.actions.ManageMedia;
import com.google.android.systemui.columbus.actions.OpenNotificationShade;
import com.google.android.systemui.columbus.actions.SettingsAction;
import com.google.android.systemui.columbus.actions.SilenceCall;
import com.google.android.systemui.columbus.actions.SnoozeAlarm;
import com.google.android.systemui.columbus.actions.TakeScreenshot;
import com.google.android.systemui.columbus.actions.UnpinNotifications;
import com.google.android.systemui.columbus.actions.UserAction;
import com.google.android.systemui.columbus.actions.UserSelectedAction;
import com.google.android.systemui.columbus.feedback.FeedbackEffect;
import com.google.android.systemui.columbus.feedback.HapticClick;
import com.google.android.systemui.columbus.feedback.UserActivity;
import com.google.android.systemui.columbus.gates.CameraVisibility;
import com.google.android.systemui.columbus.gates.ChargingState;
import com.google.android.systemui.columbus.gates.FlagEnabled;
import com.google.android.systemui.columbus.gates.Gate;
import com.google.android.systemui.columbus.gates.KeyguardProximity;
import com.google.android.systemui.columbus.gates.KeyguardVisibility;
import com.google.android.systemui.columbus.gates.PowerSaveState;
import com.google.android.systemui.columbus.gates.PowerState;
import com.google.android.systemui.columbus.gates.Proximity;
import com.google.android.systemui.columbus.gates.ScreenTouch;
import com.google.android.systemui.columbus.gates.SetupWizard;
import com.google.android.systemui.columbus.gates.SilenceAlertsDisabled;
import com.google.android.systemui.columbus.gates.SystemKeyPress;
import com.google.android.systemui.columbus.gates.TelephonyActivity;
import com.google.android.systemui.columbus.gates.UsbState;
import com.google.android.systemui.columbus.gates.VrMode;
import com.google.android.systemui.columbus.sensors.CHREGestureSensor;
import com.google.android.systemui.columbus.sensors.GestureController;
import com.google.android.systemui.columbus.sensors.GestureSensor;
import com.google.android.systemui.columbus.sensors.GestureSensorImpl;
import com.google.android.systemui.columbus.sensors.config.Adjustment;
import com.google.android.systemui.columbus.sensors.config.GestureConfiguration;
import com.google.android.systemui.columbus.sensors.config.LowSensitivitySettingAdjustment;
import com.google.android.systemui.columbus.sensors.config.SensorConfiguration;
import com.google.android.systemui.smartspace.BcSmartspaceDataProvider;
import com.google.android.systemui.smartspace.KeyguardMediaViewController;
import com.google.android.systemui.smartspace.KeyguardZenAlarmViewController;
import com.google.android.systemui.smartspace.SmartSpaceController;

import com.statix.android.systemui.StatixServices;
import com.statix.android.systemui.assist.AssistManagerGoogle;
import com.statix.android.systemui.biometrics.StatixUdfpsHbmProvider;
import com.statix.android.systemui.columbus.ColumbusTargetRequestServiceWrapper;
import com.statix.android.systemui.qs.tileimpl.QSFactoryImplStatix;
import com.statix.android.systemui.smartspace.KeyguardSmartspaceController;
import com.statix.android.systemui.theme.ThemeOverlayControllerStatix;

import com.android.internal.app.AssistUtils;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardViewController;
import com.android.systemui.assist.AssistLogger;
import com.android.systemui.assist.AssistManager;
import com.android.systemui.assist.PhoneStateMonitor;
import com.android.systemui.assist.ui.DefaultUiController;
import com.android.systemui.biometrics.UdfpsHbmProvider;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.dagger.qualifiers.Background;
import com.android.systemui.dagger.qualifiers.Main;
import com.android.systemui.dagger.SysUISingleton;
import com.android.systemui.demomode.DemoModeController;
import com.android.systemui.dock.DockManager;
import com.android.systemui.dock.DockManagerImpl;
import com.android.systemui.doze.DozeHost;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.systemui.media.dagger.MediaModule;
import com.android.systemui.model.SysUiState;
import com.android.systemui.navigationbar.NavigationBarController;
import com.android.systemui.navigationbar.NavigationModeController;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.BcSmartspaceDataPlugin;
import com.android.systemui.plugins.qs.QSFactory;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.power.EnhancedEstimates;
import com.android.systemui.power.EnhancedEstimatesImpl;
import com.android.systemui.power.PowerUI;
import com.android.systemui.power.PowerNotificationWarnings;
import com.android.systemui.qs.dagger.QSModule;
import com.android.systemui.qs.tileimpl.QSFactoryImpl;
import com.android.systemui.recents.OverviewProxyService;
import com.android.systemui.recents.Recents;
import com.android.systemui.recents.RecentsImplementation;
import com.android.systemui.settings.UserTracker;
import com.android.systemui.statusbar.commandline.CommandRegistry;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.NotificationShadeWindowController;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.collection.render.GroupMembershipManager;
import com.android.systemui.statusbar.phone.DozeServiceHost;
import com.android.systemui.statusbar.phone.HeadsUpManagerPhone;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.phone.KeyguardEnvironmentImpl;
import com.android.systemui.statusbar.phone.NotificationShadeWindowControllerImpl;
import com.android.systemui.statusbar.phone.ShadeController;
import com.android.systemui.statusbar.phone.ShadeControllerImpl;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.statusbar.policy.BatteryControllerImpl;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.statusbar.policy.DeviceProvisionedControllerImpl;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import com.android.systemui.statusbar.policy.IndividualSensorPrivacyController;
import com.android.systemui.statusbar.policy.IndividualSensorPrivacyControllerImpl;
import com.android.systemui.statusbar.policy.NextAlarmController;
import com.android.systemui.statusbar.policy.SensorPrivacyController;
import com.android.systemui.statusbar.policy.SensorPrivacyControllerImpl;
import com.android.systemui.statusbar.policy.ZenModeController;
import com.android.systemui.telephony.TelephonyListenerManager;
import com.android.systemui.theme.ThemeOverlayController;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.util.concurrency.DelayableExecutor;
import com.android.systemui.util.sensors.ProximitySensor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executor;

import javax.inject.Named;

import dagger.Binds;
import dagger.Lazy;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ElementsIntoSet;

@Module(includes = {
        MediaModule.class,
        QSModule.class
})
public abstract class SystemUIStatixModule {

    @SysUISingleton
    @Provides
    @Named(LEAK_REPORT_EMAIL_NAME)
    static String provideLeakReportEmail() {
        return "";
    }

    @Binds
    abstract EnhancedEstimates bindEnhancedEstimates(EnhancedEstimatesImpl enhancedEstimates);

    @Binds
    abstract NotificationLockscreenUserManager bindNotificationLockscreenUserManager(
            NotificationLockscreenUserManagerImpl notificationLockscreenUserManager);

    @Provides
    @SysUISingleton
    static BatteryController provideBatteryController(
            Context context,
            EnhancedEstimates enhancedEstimates,
            PowerManager powerManager,
            BroadcastDispatcher broadcastDispatcher,
            DemoModeController demoModeController,
            @Main Handler mainHandler,
            @Background Handler bgHandler) {
        BatteryController bC = new BatteryControllerImpl(
                context,
                enhancedEstimates,
                powerManager,
                broadcastDispatcher,
                demoModeController,
                mainHandler,
                bgHandler);
        bC.init();
        return bC;
    }

    @Provides
    @SysUISingleton
    static SensorPrivacyController provideSensorPrivacyController(
            SensorPrivacyManager sensorPrivacyManager) {
        SensorPrivacyController spC = new SensorPrivacyControllerImpl(sensorPrivacyManager);
        spC.init();
        return spC;
    }

    @Provides
    @SysUISingleton
    static IndividualSensorPrivacyController provideIndividualSensorPrivacyController(
            SensorPrivacyManager sensorPrivacyManager) {
        IndividualSensorPrivacyController spC = new IndividualSensorPrivacyControllerImpl(
                sensorPrivacyManager);
        spC.init();
        return spC;
    }

    @Binds
    @SysUISingleton
    public abstract QSFactory bindQSFactory(QSFactoryImplStatix qsFactoryImpl);

    @Binds
    abstract DockManager bindDockManager(DockManagerImpl dockManager);

    @Binds
    abstract NotificationEntryManager.KeyguardEnvironment bindKeyguardEnvironment(
            KeyguardEnvironmentImpl keyguardEnvironment);

    @Binds
    abstract ShadeController provideShadeController(ShadeControllerImpl shadeController);

    @SysUISingleton
    @Provides
    @Named(ALLOW_NOTIFICATION_LONG_PRESS_NAME)
    static boolean provideAllowNotificationLongPress() {
        return true;
    }

    @SysUISingleton
    @Provides
    static HeadsUpManagerPhone provideHeadsUpManagerPhone(
            Context context,
            StatusBarStateController statusBarStateController,
            KeyguardBypassController bypassController,
            GroupMembershipManager groupManager,
            ConfigurationController configurationController) {
        return new HeadsUpManagerPhone(context, statusBarStateController, bypassController,
                groupManager, configurationController);
    }

    @SysUISingleton
    @Provides
    static PowerUI.WarningsUI provideWarningsUi(PowerNotificationWarnings controllerImpl) {
        return controllerImpl;
    }

    @Binds
    abstract HeadsUpManager bindHeadsUpManagerPhone(HeadsUpManagerPhone headsUpManagerPhone);

    @Provides
    @SysUISingleton
    static Recents provideRecents(Context context, RecentsImplementation recentsImplementation,
            CommandQueue commandQueue) {
        return new Recents(context, recentsImplementation, commandQueue);
    }

    @Binds
    abstract DeviceProvisionedController bindDeviceProvisionedController(
            DeviceProvisionedControllerImpl deviceProvisionedController);

    @Binds
    abstract KeyguardViewController bindKeyguardViewController(
            StatusBarKeyguardViewManager statusBarKeyguardViewManager);

    @Binds
    abstract NotificationShadeWindowController bindNotificationShadeController(
            NotificationShadeWindowControllerImpl notificationShadeWindowController);

    @Binds
    abstract DozeHost provideDozeHost(DozeServiceHost dozeServiceHost);

    @Binds
    abstract ThemeOverlayController provideThemeOverlayController(ThemeOverlayControllerStatix themeOverlayController);

    @Binds
    @SysUISingleton
    abstract UdfpsHbmProvider bindUdfpsHbmProvider(StatixUdfpsHbmProvider udfpsHbmProvider);

    @Provides
    @SysUISingleton
    static StatixServices provideStatixServices(Context context, Lazy<ColumbusServiceWrapper> lazyB) {
        return new StatixServices(context, lazyB);
    }

    // Google

    // Smartspace

    @Provides
    @SysUISingleton
    static SmartSpaceController provideSmartSpaceController(Context context, KeyguardUpdateMonitor updateMonitor, Handler handler, AlarmManager am, DumpManager dm) {
        return new SmartSpaceController(context, updateMonitor, handler, am, dm);
    }

    @Provides
    @SysUISingleton
    static KeyguardSmartspaceController provideKeyguardSmartspaceController(Context context, FeatureFlags featureFlags,
            KeyguardZenAlarmViewController keyguardZenAlarmViewController, KeyguardMediaViewController keyguardMediaViewController) {
        return new KeyguardSmartspaceController(context, featureFlags, keyguardZenAlarmViewController, keyguardMediaViewController);
    }

    @Provides
    @SysUISingleton
    static KeyguardZenAlarmViewController provideKeyguardZenAlarmViewController(Context context, BcSmartspaceDataPlugin bcSmartspaceDataPlugin, ZenModeController zenModeController,
            AlarmManager alarmManager, NextAlarmController nextAlarmController, Handler handler) {
        return new KeyguardZenAlarmViewController(context, bcSmartspaceDataPlugin, zenModeController, alarmManager, nextAlarmController, handler);
    }

    @Provides
    @SysUISingleton
    static KeyguardMediaViewController provideKeyguardMediaViewController(Context context, BcSmartspaceDataPlugin bcSmartspaceDataPlugin,
            @Main DelayableExecutor delayableExecutor, NotificationMediaManager notificationMediaManager, BroadcastDispatcher broadcastDispatcher) {
        return new KeyguardMediaViewController(context, bcSmartspaceDataPlugin, delayableExecutor, notificationMediaManager, broadcastDispatcher);
    }

    @Provides
    @SysUISingleton
    static BcSmartspaceDataPlugin provideBcSmartspaceDataPlugin() {
        return new BcSmartspaceDataProvider();
    }

    // Columbus

    @Provides
    @SysUISingleton
    static ColumbusStructuredDataManager provideColumbusStructuredDataManager(Context context, UserTracker userTracker, @Background Executor executor) {
        return new ColumbusStructuredDataManager(context, userTracker, executor);
    }

    @Provides
    @SysUISingleton
    static ContentResolverWrapper provideContentResolverWrapper(Context context) {
        return new ContentResolverWrapper(context);
    }

    @Provides
    @SysUISingleton
    static ColumbusServiceWrapper provideColumbusServiceWrapper(ColumbusSettings columbusSettings, Lazy<ColumbusService> lazy, Lazy<SettingsAction> lazyB, Lazy<ColumbusStructuredDataManager> lazyC) {
        return new ColumbusServiceWrapper(columbusSettings, lazy, lazyB, lazyC);
    }

    @Provides
    @SysUISingleton
    static ColumbusService provideColumbusService(List<Action> list, Set<FeedbackEffect> set, Set<Gate> setB, GestureController gestureController, PowerManagerWrapper powerManagerWrapper) {
        return new ColumbusService(list, set, setB, gestureController, powerManagerWrapper);
    }

    @Provides
    @SysUISingleton
    static PowerManagerWrapper providePowerManagerWrapper(Context context) {
        return new PowerManagerWrapper(context);
    }

    @Provides
    @SysUISingleton
    static ColumbusSettings provideColumbusSettings(Context context, UserTracker userTracker, ColumbusContentObserver.Factory factory) {
        return new ColumbusSettings(context, userTracker, factory);
    }

    @Provides
    @SysUISingleton
    static ColumbusContentObserver.Factory provideColumbusContentObserver(ContentResolverWrapper contentResolverWrapper, UserTracker userTracker, @Background Handler handler, @Main Executor executor) {
        return new ColumbusContentObserver.Factory(contentResolverWrapper, userTracker, handler, executor);
    }

    @Provides
    @SysUISingleton
    static com.google.android.systemui.columbus.feedback.AssistInvocationEffect provideAssistInvocationEffectColumbus(AssistManager assistManager) {
        return new com.google.android.systemui.columbus.feedback.AssistInvocationEffect(assistManager);
    }

    @Provides
    @SysUISingleton
    static UserActivity provideUserActivity(Lazy<PowerManager> lazy) {
        return new UserActivity(lazy);
    }

    @Provides
    @SysUISingleton
    static HapticClick provideHapticClick(Lazy<Vibrator> lazy) {
        return new HapticClick(lazy);
    }

    @Provides
    @SysUISingleton
    static KeyguardProximity provideKeyguardProximity(Context context, KeyguardVisibility keyguardVisibility, Proximity proximity) {
        return new KeyguardProximity(context, keyguardVisibility, proximity);
    }

    @Provides
    @SysUISingleton
    static KeyguardVisibility provideKeyguardVisibility(Context context, Lazy<KeyguardStateController> lazy) {
        return new KeyguardVisibility(context, lazy);
    }

    @Provides
    @SysUISingleton
    static ChargingState provideChargingState(Context context, Handler handler, @Named(COLUMBUS_TRANSIENT_GATE_DURATION) long j) {
        return new ChargingState(context, handler, j);
    }

    @Provides
    @SysUISingleton
    static UsbState provideUsbState(Context context, Handler handler, @Named(COLUMBUS_TRANSIENT_GATE_DURATION) long j) {
        return new UsbState(context, handler, j);
    }

    @Provides
    @SysUISingleton
    static PowerSaveState providePowerSaveState(Context context) {
        return new PowerSaveState(context);
    }

    @Provides
    @SysUISingleton
    static SilenceAlertsDisabled provideSilenceAlertsDisabled(Context context, ColumbusSettings columbusSettings) {
        return new SilenceAlertsDisabled(context, columbusSettings);
    }

    @Provides
    @SysUISingleton
    static FlagEnabled provideFlagEnabled(Context context, ColumbusSettings columbusSettings, Handler handler) {
        return new FlagEnabled(context, columbusSettings, handler);
    }

    @Provides
    @SysUISingleton
    static CameraVisibility provideCameraVisibility(Context context, List<Action> list, KeyguardVisibility keyguardVisibility, PowerState powerState, IActivityManager iActivityManager, Handler handler) {
        return new CameraVisibility(context, list, keyguardVisibility, powerState, iActivityManager, handler);
    }

    @Provides
    @SysUISingleton
    static SetupWizard provideSetupWizard(Context context, @Named(COLUMBUS_SETUP_WIZARD_ACTIONS) Set<Action> set, Lazy<DeviceProvisionedController> lazy) {
        return new SetupWizard(context, set, lazy);
    }

    @Provides
    @SysUISingleton
    static PowerState providePowerState(Context context, Lazy<WakefulnessLifecycle> lazy) {
        return new PowerState(context, lazy);
    }

    @Provides
    @SysUISingleton
    static SystemKeyPress provideSystemKeyPress(Context context, Handler handler, CommandQueue commandQueue, @Named(COLUMBUS_TRANSIENT_GATE_DURATION) long j, @Named(COLUMBUS_BLOCKING_SYSTEM_KEYS) Set<Integer> set) {
        return new SystemKeyPress(context, handler, commandQueue, j, set);
    }

    @Provides
    @SysUISingleton
    static ScreenTouch provideScreenTouch(Context context, PowerState powerState, Handler handler) {
        return new ScreenTouch(context, powerState, handler);
    }

    @Provides
    @SysUISingleton
    static com.google.android.systemui.columbus.gates.TelephonyActivity provideTelephonyActivityColumbus(Context context, Lazy<TelephonyManager> lazy, Lazy<TelephonyListenerManager> lazyB) {
        return new com.google.android.systemui.columbus.gates.TelephonyActivity(context, lazy, lazyB);
    }

    @Provides
    @SysUISingleton
    static Proximity provideProximity(Context context, ProximitySensor proximitySensor) {
        return new Proximity(context, proximitySensor);
    }

    @Provides
    @SysUISingleton
    static VrMode provideVrMode(Context context) {
        return new VrMode(context);
    }

    @Provides
    @SysUISingleton
    static GestureSensorImpl provideGestureSensorImpl(Context context, UiEventLogger uiEventLogger, Handler handler) {
        return new GestureSensorImpl(context, uiEventLogger, handler);
    }

    @Provides
    @SysUISingleton
    static GestureController provideGestureController(GestureSensor gestureSensor, @Named(COLUMBUS_SOFT_GATES) Set<Gate> set, CommandRegistry commandRegistry, UiEventLogger uiEventLogger) {
        return new GestureController(gestureSensor, set, commandRegistry, uiEventLogger);
    }

    @Provides
    @SysUISingleton
    static CHREGestureSensor provideCHREGestureSensor(Context context, UiEventLogger uiEventLogger, GestureConfiguration gestureConfiguration, StatusBarStateController statusBarStateController, WakefulnessLifecycle wakefulnessLifecycle, Handler handler) {
        return new CHREGestureSensor(context, uiEventLogger, gestureConfiguration, statusBarStateController, wakefulnessLifecycle, handler);
    }

    @Provides
    @SysUISingleton
    static GestureConfiguration provideGestureConfiguration(List<Adjustment> list, SensorConfiguration sensorConfiguration) {
        return new GestureConfiguration(list, sensorConfiguration);
    }

    @Provides
    @SysUISingleton
    static SensorConfiguration provideSensorConfiguration(Context context) {
        return new SensorConfiguration(context);
    }

    @Provides
    @SysUISingleton
    static LowSensitivitySettingAdjustment provideLowSensitivitySettingAdjustment(Context context, ColumbusSettings columbusSettings, SensorConfiguration sensorConfiguration) {
        return new LowSensitivitySettingAdjustment(context, columbusSettings, sensorConfiguration);
    }

    @Provides
    @SysUISingleton
    static SettingsAction provideSettingsActionColumbus(Context context, StatusBar statusBar, UiEventLogger uiEventLogger) {
        return new SettingsAction(context, statusBar, uiEventLogger);
    }

    @Provides
    @SysUISingleton
    static UserSelectedAction provideUserSelectedAction(Context context, ColumbusSettings columbusSettings, Map<String, UserAction> map, TakeScreenshot takeScreenshot, KeyguardStateController keyguardStateController, PowerManagerWrapper powerManagerWrapper, WakefulnessLifecycle wakefulnessLifecycle) {
        return new UserSelectedAction(context, columbusSettings, map, takeScreenshot, keyguardStateController, powerManagerWrapper, wakefulnessLifecycle);
    }

    @Provides
    @SysUISingleton
    static DismissTimer provideDismissTimer(Context context, SilenceAlertsDisabled silenceAlertsDisabled, IActivityManager iActivityManager) {
        return new DismissTimer(context, silenceAlertsDisabled, iActivityManager);
    }

    @Provides
    @SysUISingleton
    static com.google.android.systemui.columbus.actions.UnpinNotifications provideUnpinNotificationsColumbus(Context context, SilenceAlertsDisabled silenceAlertsDisabled, Optional<HeadsUpManager> optional) {
        return new com.google.android.systemui.columbus.actions.UnpinNotifications(context, silenceAlertsDisabled, optional);
    }

    @Provides
    @SysUISingleton
    static ManageMedia provideManageMedia(Context context, AudioManager audioManager, UiEventLogger uiEventLogger) {
        return new ManageMedia(context, audioManager, uiEventLogger);
    }

    @Provides
    @SysUISingleton
    static LaunchApp provideLaunchApp(Context context, LauncherApps launcherApps, ActivityStarter activityStarter, StatusBarKeyguardViewManager statusBarKeyguardViewManager, IActivityManager iActivityManager, UserManager userManager, ColumbusSettings columbusSettings, KeyguardVisibility keyguardVisibility, KeyguardUpdateMonitor keyguardUpdateMonitor, @Main Handler handler, @Background Handler handlerB, Executor executor, UiEventLogger uiEventLogger, UserTracker userTracker) {
        return new LaunchApp(context, launcherApps, activityStarter, statusBarKeyguardViewManager, iActivityManager, userManager, columbusSettings, keyguardVisibility, keyguardUpdateMonitor, handler, handlerB, executor, uiEventLogger, userTracker);
    }

    @Provides
    @SysUISingleton
    static com.google.android.systemui.columbus.actions.SilenceCall provideSilenceCallColumbus(Context context, SilenceAlertsDisabled silenceAlertsDisabled, Lazy<TelecomManager> lazy, Lazy<TelephonyManager> lazyB, Lazy<TelephonyListenerManager> lazyC) {
        return new com.google.android.systemui.columbus.actions.SilenceCall(context, silenceAlertsDisabled, lazy, lazyB, lazyC);
    }

    @Provides
    @SysUISingleton
    static LaunchOverview provideLaunchOverview(Context context, Recents recents, UiEventLogger uiEventLogger) {
        return new LaunchOverview(context, recents, uiEventLogger);
    }

    @Provides
    @SysUISingleton
    static com.google.android.systemui.columbus.actions.LaunchOpa provideLaunchOpaColumbus(Context context, StatusBar statusBar, Set<FeedbackEffect> set, AssistManager assistManager, Lazy<KeyguardManager> lazy, TunerService tunerService, ColumbusContentObserver.Factory factory, UiEventLogger uiEventLogger) {
        return new com.google.android.systemui.columbus.actions.LaunchOpa(context, statusBar, set, assistManager, lazy, tunerService, factory, uiEventLogger);
    }

    @Provides
    @SysUISingleton
    static com.google.android.systemui.columbus.actions.SnoozeAlarm provideSnoozeAlarmColumbus(Context context, SilenceAlertsDisabled silenceAlertsDisabled, IActivityManager iActivityManager) {
        return new com.google.android.systemui.columbus.actions.SnoozeAlarm(context, silenceAlertsDisabled, iActivityManager);
    }

    @Provides
    @SysUISingleton
    static OpenNotificationShade provideOpenNotificationShade(Context context, Lazy<NotificationShadeWindowController> lazy, Lazy<StatusBar> lazyB, UiEventLogger uiEventLogger) {
        return new OpenNotificationShade(context, lazy, lazyB, uiEventLogger);
    }

    @Provides
    @SysUISingleton
    static TakeScreenshot provideTakeScreenshot(Context context, Handler handler, UiEventLogger uiEventLogger) {
        return new TakeScreenshot(context, handler, uiEventLogger);
    }

    @Provides
    @SysUISingleton
    @Named(COLUMBUS_TRANSIENT_GATE_DURATION)
    static long provideTransientGateDuration() {
        return 500;
    }

    @Provides
    @SysUISingleton
    @ElementsIntoSet
    static Set<Gate> provideColumbusGates(FlagEnabled flagEnabled, ChargingState chargingState, UsbState usbState, KeyguardProximity keyguardProximity, SetupWizard setupWizard, SystemKeyPress systemKeyPress, TelephonyActivity telephonyActivity, VrMode vrMode, CameraVisibility cameraVisibility, PowerSaveState powerSaveState, PowerState powerState, ScreenTouch screenTouch) {
        return new HashSet(Arrays.asList(flagEnabled, chargingState, usbState, keyguardProximity, setupWizard, systemKeyPress, telephonyActivity, vrMode, cameraVisibility, powerSaveState, powerState, screenTouch));
    }

    @Provides
    @SysUISingleton
    @ElementsIntoSet
    @Named(COLUMBUS_SOFT_GATES)
    static Set<Gate> provideColumbusSoftGates(ChargingState chargingState, UsbState usbState, SystemKeyPress systemKeyPress, ScreenTouch screenTouch) {
        return new HashSet(Arrays.asList(chargingState, usbState, systemKeyPress, screenTouch));
    }

    @Provides
    @SysUISingleton
    static Map<String, UserAction> provideUserSelectedActions(LaunchOpa launchOpa, ManageMedia manageMedia, TakeScreenshot takeScreenshot, LaunchOverview launchOverview, OpenNotificationShade openNotificationShade, LaunchApp launchApp) {
        Map<String, UserAction> result = new HashMap<>();
        result.put("assistant", launchOpa);
        result.put("media", manageMedia);
        result.put("screenshot", takeScreenshot);
        result.put("overview", launchOverview);
        result.put("notifications", openNotificationShade);
        result.put("launch", launchApp);
        return result;
    }

    @Provides
    @SysUISingleton
    static GestureSensor provideGestureSensor(Context context, ColumbusSettings columbusSettings, Lazy<CHREGestureSensor> gestureSensor, Lazy<GestureSensorImpl> apSensor) {
        if (columbusSettings.useApSensor() || !context.getPackageManager().hasSystemFeature("android.hardware.context_hub")) {
            Log.i("Columbus/Module", "Creating AP sensor");
            return apSensor.get();
        }
        Log.i("Columbus/Module", "Creating CHRE sensor");
        return gestureSensor.get();
    }

    @Provides
    @SysUISingleton
    static List<Action> provideColumbusActions(@Named(COLUMBUS_FULL_SCREEN_ACTIONS) List<Action> fullScreenActions, UnpinNotifications unpinNotifications, UserSelectedAction userSelectedAction) {
        List<Action> result = new ArrayList<>(fullScreenActions);
        result.add(unpinNotifications);
        result.add(userSelectedAction);
        return result;
    }

    @Provides
    @SysUISingleton
    @Named(COLUMBUS_FULL_SCREEN_ACTIONS)
    static List<Action> provideFullscreenActions(DismissTimer dismissTimer, SnoozeAlarm snoozeAlarm, SilenceCall silenceCall, SettingsAction settingsAction) {
        return Arrays.asList(dismissTimer, snoozeAlarm, silenceCall, settingsAction);
    }

    @Provides
    @SysUISingleton
    @ElementsIntoSet
    static Set<FeedbackEffect> provideColumbusEffects(HapticClick hapticClick, UserActivity userActivity) {
        return new HashSet(Arrays.asList(hapticClick, userActivity));
    }

    @Provides
    @SysUISingleton
    static List<Adjustment> provideGestureAdjustments(LowSensitivitySettingAdjustment lowSensitivitySettingAdjustment) {
        return Collections.singletonList(lowSensitivitySettingAdjustment);
    }

    @Provides
    @SysUISingleton
    @Named(COLUMBUS_BLOCKING_SYSTEM_KEYS)
    @ElementsIntoSet
    static Set<Integer> provideBlockingSystemKeys() {
        return new HashSet(Arrays.asList(24, 25, 26));
    }

    @Provides
    @SysUISingleton
    @Named(COLUMBUS_SETUP_WIZARD_ACTIONS)
    @ElementsIntoSet
    static Set<Action> provideSetupWizardActions(SettingsAction settingsAction) {
        return new HashSet(Arrays.asList(settingsAction));
    }

    // AssistManagerGoogle

    @Binds
    @SysUISingleton
    abstract AssistManager bindAssistManagerGoogle(AssistManagerGoogle assistManager);

    @Provides
    @SysUISingleton
    static OpaEnabledDispatcher provideOpaEnabledDispatcher(Lazy<StatusBar> lazy) {
        return new OpaEnabledDispatcher(lazy);
    }

    @Provides
    @SysUISingleton
    static GoogleAssistLogger provideGoogleAssistLogger(Context context, UiEventLogger uiEventLogger, AssistUtils assistUtils, PhoneStateMonitor phoneStateMonitor, AssistantPresenceHandler assistantPresenceHandler) {
        return new GoogleAssistLogger(context, uiEventLogger, assistUtils, phoneStateMonitor, assistantPresenceHandler);
    }

    @Provides
    @SysUISingleton
    static OpaEnabledReceiver provideOpaEnabledReceiver(Context context, BroadcastDispatcher broadcastDispatcher, @Main Executor executor, @Background Executor executorB, OpaEnabledSettings opaEnabledSettings) {
        return new OpaEnabledReceiver(context, broadcastDispatcher, executor, executorB, opaEnabledSettings);
    }

    @Provides
    @SysUISingleton
    static AssistManagerGoogle provideAssistManagerGoogle(DeviceProvisionedController deviceProvisionedController, Context context, AssistUtils assistUtils, NgaUiController ngaUiController, CommandQueue commandQueue, OpaEnabledReceiver opaEnabledReceiver, PhoneStateMonitor phoneStateMonitor, OverviewProxyService overviewProxyService, OpaEnabledDispatcher opaEnabledDispatcher, KeyguardUpdateMonitor keyguardUpdateMonitor, NavigationModeController navigationModeController, ConfigurationController configurationController, AssistantPresenceHandler assistantPresenceHandler, NgaMessageHandler ngaMessageHandler, Lazy<SysUiState> lazy, Handler handler, DefaultUiController defaultUiController, GoogleDefaultUiController googleDefaultUiController, IWindowManager iWindowManager, AssistLogger assistLogger) {
        return new AssistManagerGoogle(deviceProvisionedController, context, assistUtils, ngaUiController, commandQueue, opaEnabledReceiver, phoneStateMonitor, overviewProxyService, opaEnabledDispatcher, keyguardUpdateMonitor, navigationModeController, configurationController, assistantPresenceHandler, ngaMessageHandler, lazy, handler, defaultUiController, googleDefaultUiController, iWindowManager, assistLogger);
    }

    @Provides
    @SysUISingleton
    static OpaEnabledSettings provideOpaEnabledSettings(Context context) {
        return new OpaEnabledSettings(context);
    }

    @Provides
    @SysUISingleton
    static NavBarFader provideNavBarFader(Lazy<NavigationBarController> lazy, Handler handler) {
        return new NavBarFader(lazy, handler);
    }

    @Provides
    @SysUISingleton
    static FlingVelocityWrapper provideFlingVelocityWrapper() {
        return new FlingVelocityWrapper();
    }

    @Provides
    @SysUISingleton
    static TouchInsideHandler provideTouchInsideHandler(Lazy<AssistManager> lazy, NavigationModeController navigationModeController, AssistLogger assistLogger) {
        return new TouchInsideHandler(lazy, navigationModeController, assistLogger);
    }

    @Provides
    @SysUISingleton
    static OverlappedElementController provideOverlappedElementController(Lazy<StatusBar> lazy) {
        return new OverlappedElementController(lazy);
    }

    @Provides
    @SysUISingleton
    static AssistantPresenceHandler provideAssistantPresenceHandler(Context context, AssistUtils assistUtils) {
        return new AssistantPresenceHandler(context, assistUtils);
    }

    @Provides
    @SysUISingleton
    static ColorChangeHandler provideColorChangeHandler(Context context) {
        return new ColorChangeHandler(context);
    }

    @Provides
    @SysUISingleton
    static IconController provideIconController(LayoutInflater layoutInflater, @Named(OVERLAY_UI_HOST_PARENT_VIEW_GROUP) ViewGroup viewGroup, ConfigurationController configurationController) {
        return new IconController(layoutInflater, viewGroup, configurationController);
    }

    @Provides
    @SysUISingleton
    static AssistantWarmer provideAssistantWarmer(Context context) {
        return new AssistantWarmer(context);
    }

    @Provides
    @SysUISingleton
    static TranscriptionController provideTranscriptionController(@Named(OVERLAY_UI_HOST_PARENT_VIEW_GROUP) ViewGroup viewGroup, TouchInsideHandler touchInsideHandler, FlingVelocityWrapper flingVelocityWrapper, ConfigurationController configurationController) {
        return new TranscriptionController(viewGroup, touchInsideHandler, flingVelocityWrapper, configurationController);
    }

    @Provides
    @SysUISingleton
    static TouchOutsideHandler provideTouchOutsideHandler() {
        return new TouchOutsideHandler();
    }

    @Provides
    @SysUISingleton
    static ConfigurationHandler provideConfigurationHandler(Context context) {
        return new ConfigurationHandler(context);
    }

    @Provides
    @SysUISingleton
    static KeyboardMonitor provideKeyboardMonitor(Context context, Optional<CommandQueue> optional) {
        return new KeyboardMonitor(context, optional);
    }

    @Provides
    @SysUISingleton
    static TaskStackNotifier provideTaskStackNotifier() {
        return new TaskStackNotifier();
    }

    @Provides
    @SysUISingleton
    static TakeScreenshotHandler provideTakeScreenshotHandler(Context context) {
        return new TakeScreenshotHandler(context);
    }

    @Provides
    @SysUISingleton
    static GoBackHandler provideGoBackHandler() {
        return new GoBackHandler();
    }

    @Provides
    @SysUISingleton
    static NgaUiController provideNgaUiController(Context context, TimeoutManager timeoutManager, AssistantPresenceHandler assistantPresenceHandler, TouchInsideHandler touchInsideHandler, ColorChangeHandler colorChangeHandler, OverlayUiHost overlayUiHost, EdgeLightsController edgeLightsController, GlowController glowController, ScrimController scrimController, TranscriptionController transcriptionController, IconController iconController, LightnessProvider lightnessProvider, StatusBarStateController statusBarStateController, Lazy<AssistManager> lazy, FlingVelocityWrapper flingVelocityWrapper, AssistantWarmer assistantWarmer, NavBarFader navBarFader, AssistLogger assistLogger) {
        return new NgaUiController(context, timeoutManager, assistantPresenceHandler, touchInsideHandler, colorChangeHandler, overlayUiHost, edgeLightsController, glowController, scrimController, transcriptionController, iconController, lightnessProvider, statusBarStateController, lazy, flingVelocityWrapper, assistantWarmer, navBarFader, assistLogger);
    }

    @Provides
    @SysUISingleton
    static GlowController provideGlowController(Context context, @Named(OVERLAY_UI_HOST_PARENT_VIEW_GROUP) ViewGroup viewGroup, TouchInsideHandler touchInsideHandler) {
        return new GlowController(context, viewGroup, touchInsideHandler);
    }

    @Provides
    @SysUISingleton
    static GoogleDefaultUiController provideGoogleDefaultUiController(Context context, GoogleAssistLogger googleAssistLogger) {
        return new GoogleDefaultUiController(context, googleAssistLogger);
    }

    @Provides
    @SysUISingleton
    static NgaMessageHandler provideNgaMessageHandler(NgaUiController ngaUiController, AssistantPresenceHandler assistantPresenceHandler, NavigationModeController navigationModeController, Set<NgaMessageHandler.KeepAliveListener> set, Set<NgaMessageHandler.AudioInfoListener> setB, Set<NgaMessageHandler.CardInfoListener> setC, Set<NgaMessageHandler.ConfigInfoListener> setD, Set<NgaMessageHandler.EdgeLightsInfoListener> setE, Set<NgaMessageHandler.TranscriptionInfoListener> setF, Set<NgaMessageHandler.GreetingInfoListener> setG, Set<NgaMessageHandler.ChipsInfoListener> setH, Set<NgaMessageHandler.ClearListener> setI, Set<NgaMessageHandler.StartActivityInfoListener> setJ, Set<NgaMessageHandler.KeyboardInfoListener> setK, Set<NgaMessageHandler.ZerostateInfoListener> set1B, Set<NgaMessageHandler.GoBackListener> set1C, Set<NgaMessageHandler.TakeScreenshotListener> set1D, Set<NgaMessageHandler.WarmingListener> set1E, Set<NgaMessageHandler.NavBarVisibilityListener> set1F, Handler handler) {
        return new NgaMessageHandler(ngaUiController, assistantPresenceHandler, navigationModeController, set, setB, setC, setD, setE, setF, setG, setH, setI, setJ, setK, set1B, set1C, set1D, set1E, set1F, handler);
    }

    @Provides
    @SysUISingleton
    static ScrimController provideScrimController(@Named(OVERLAY_UI_HOST_PARENT_VIEW_GROUP) ViewGroup viewGroup, OverlappedElementController overlappedElementController, LightnessProvider lightnessProvider, TouchInsideHandler touchInsideHandler) {
        return new ScrimController(viewGroup, overlappedElementController, lightnessProvider, touchInsideHandler);
    }

    @Provides
    @SysUISingleton
    static TimeoutManager provideTimeoutManager(Lazy<AssistManager> lazy) {
        return new TimeoutManager(lazy);
    }

    @Provides
    @SysUISingleton
    static OverlayUiHost provideOverlayUiHost(Context context, TouchOutsideHandler touchOutsideHandler) {
        return new OverlayUiHost(context, touchOutsideHandler);
    }

    @Provides
    @SysUISingleton
    static LightnessProvider provideLightnessProvider() {
        return new LightnessProvider();
    }

    @Provides
    @SysUISingleton
    static EdgeLightsController provideEdgeLightsController(Context context, @Named(OVERLAY_UI_HOST_PARENT_VIEW_GROUP) ViewGroup viewGroup, AssistLogger assistLogger) {
        return new EdgeLightsController(context, viewGroup, assistLogger);
    }

    @Provides
    @SysUISingleton
    static NgaInputHandler provideNgaInputHandler(TouchInsideHandler touchInsideHandler, Set<TouchActionRegion> set, Set<TouchInsideRegion> setB) {
        return new NgaInputHandler(touchInsideHandler, set, setB);
    }

    @Provides
    @ElementsIntoSet
    static Set<NgaMessageHandler.AudioInfoListener> provideAudioInfoListeners(EdgeLightsController edgeLightsController, GlowController glowController) {
        return new HashSet(Arrays.asList(edgeLightsController, glowController));
    }

    @Provides
    @ElementsIntoSet
    static Set<NgaMessageHandler.CardInfoListener> provideCardInfoListeners(GlowController glowController, ScrimController scrimController, TranscriptionController transcriptionController, LightnessProvider lightnessProvider) {
        return new HashSet(Arrays.asList(glowController, scrimController, transcriptionController, lightnessProvider));
    }

    @Provides
    @ElementsIntoSet
    static Set<NgaMessageHandler.TranscriptionInfoListener> provideTranscriptionInfoListener(TranscriptionController transcriptionController) {
        return new HashSet(Arrays.asList(transcriptionController));
    }

    @Provides
    @ElementsIntoSet
    static Set<NgaMessageHandler.GreetingInfoListener> provideGreetingInfoListener(TranscriptionController transcriptionController) {
        return new HashSet(Arrays.asList(transcriptionController));
    }

    @Provides
    @ElementsIntoSet
    static Set<NgaMessageHandler.ChipsInfoListener> provideChipsInfoListener(TranscriptionController transcriptionController) {
        return new HashSet(Arrays.asList(transcriptionController));
    }

    @Provides
    @ElementsIntoSet
    static Set<NgaMessageHandler.ClearListener> provideClearListener(TranscriptionController transcriptionController) {
        return new HashSet(Arrays.asList(transcriptionController));
    }

    @Provides
    @ElementsIntoSet
    static Set<NgaMessageHandler.KeyboardInfoListener> provideKeyboardInfoListener(IconController iconController) {
        return new HashSet(Arrays.asList(iconController));
    }

    @Provides
    @ElementsIntoSet
    static Set<NgaMessageHandler.ZerostateInfoListener> provideZerostateInfoListener(IconController iconController) {
        return new HashSet(Arrays.asList(iconController));
    }

    @Provides
    @ElementsIntoSet
    static Set<NgaMessageHandler.GoBackListener> provideGoBackListener(GoBackHandler goBackHandler) {
        return new HashSet(Arrays.asList(goBackHandler));
    }

    @Provides
    @ElementsIntoSet
    static Set<NgaMessageHandler.TakeScreenshotListener> provideTakeScreenshotListener(TakeScreenshotHandler takeScreenshotHandler) {
        return new HashSet(Arrays.asList(takeScreenshotHandler));
    }

    @Provides
    @ElementsIntoSet
    static Set<NgaMessageHandler.WarmingListener> provideWarmingListener(AssistantWarmer assistantWarmer) {
        return new HashSet(Arrays.asList(assistantWarmer));
    }

    @Provides
    @ElementsIntoSet
    static Set<NgaMessageHandler.NavBarVisibilityListener> provideNavBarVisibilityListener(NavBarFader navBarFader) {
        return new HashSet(Arrays.asList(navBarFader));
    }

    @Provides
    @ElementsIntoSet
    static Set<NgaMessageHandler.ConfigInfoListener> provideConfigInfoListeners(AssistantPresenceHandler assistantPresenceHandler, TouchInsideHandler touchInsideHandler, TouchOutsideHandler touchOutsideHandler, TaskStackNotifier taskStackNotifier, KeyboardMonitor keyboardMonitor, ColorChangeHandler colorChangeHandler, ConfigurationHandler configurationHandler) {
        return new HashSet(Arrays.asList(assistantPresenceHandler, touchInsideHandler, touchOutsideHandler, taskStackNotifier, keyboardMonitor, colorChangeHandler, configurationHandler));
    }

    @Provides
    @ElementsIntoSet
    static Set<NgaMessageHandler.EdgeLightsInfoListener> provideEdgeLightsInfoListeners(EdgeLightsController edgeLightsController, NgaInputHandler ngaInputHandler) {
        return new HashSet(Arrays.asList(edgeLightsController, ngaInputHandler));
    }

    @Provides
    @ElementsIntoSet
    static Set<NgaMessageHandler.KeepAliveListener> provideKeepAliveListener(TimeoutManager timeoutManager) {
        return new HashSet(Arrays.asList(timeoutManager));
    }

    @Provides
    @ElementsIntoSet
    static Set<NgaMessageHandler.StartActivityInfoListener> provideActivityStarter(final Lazy<StatusBar> lazy) {
        return new HashSet(Collections.singletonList((NgaMessageHandler.StartActivityInfoListener) (intent, z) -> {
            if (intent == null) {
                Log.e("ActivityStarter", "Null intent; cannot start activity");
            } else {
                lazy.get().startActivity(intent, z);
            }
        }));
    }

    @Provides
    @ElementsIntoSet
    static Set<TouchActionRegion> provideTouchActionRegions(IconController iconController, TranscriptionController transcriptionController) {
        return new HashSet(Arrays.asList(iconController, transcriptionController));
    }

    @Provides
    @ElementsIntoSet
    static Set<TouchInsideRegion> provideTouchInsideRegions(GlowController glowController, ScrimController scrimController, TranscriptionController transcriptionController) {
        return new HashSet(Arrays.asList(glowController, scrimController, transcriptionController));
    }

    @Provides
    @SysUISingleton
    @Named(OVERLAY_UI_HOST_PARENT_VIEW_GROUP)
    static ViewGroup provideParentViewGroup(OverlayUiHost overlayUiHost) {
        return overlayUiHost.getParent();
    }
}
