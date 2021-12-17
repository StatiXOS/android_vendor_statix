package com.statix.android.systemui.elmyra;

import android.content.Context;

import com.google.android.systemui.elmyra.ServiceConfigurationGoogle;
import com.google.android.systemui.elmyra.actions.CameraAction;
import com.google.android.systemui.elmyra.actions.SetupWizardAction;
import com.google.android.systemui.elmyra.feedback.OpaHomeButton;
import com.google.android.systemui.elmyra.feedback.OpaLockscreen;
import com.google.android.systemui.elmyra.feedback.SquishyNavigationButtons;

import com.statix.android.systemui.assist.AssistManagerGoogle;

import com.android.systemui.dagger.SysUISingleton;
import com.android.systemui.keyguard.KeyguardViewMediator;
import com.android.systemui.navigationbar.NavigationModeController;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.telephony.TelephonyListenerManager;

import dagger.Module;
import dagger.Provides;

import java.util.Optional;

@Module
public class ElmyraModule {
    @Provides
    @SysUISingleton
    static ServiceConfigurationGoogle provideServiceConfigurationGoogle(Context context, com.google.android.systemui.elmyra.feedback.AssistInvocationEffect assistInvocationEffect, com.google.android.systemui.elmyra.actions.LaunchOpa.Builder builder, com.google.android.systemui.elmyra.actions.SettingsAction.Builder builderB, CameraAction.Builder builderC, SetupWizardAction.Builder builderD, SquishyNavigationButtons squishyNavigationButtons, com.google.android.systemui.elmyra.actions.UnpinNotifications unpinNotifications, com.google.android.systemui.elmyra.actions.SilenceCall silenceCall, com.google.android.systemui.elmyra.gates.TelephonyActivity telephonyActivity) {
        return new ServiceConfigurationGoogle(context, assistInvocationEffect, builder, builderB, builderC, builderD, squishyNavigationButtons, unpinNotifications, silenceCall, telephonyActivity);
    }

    @Provides
    @SysUISingleton
    static com.google.android.systemui.elmyra.feedback.AssistInvocationEffect provideAssistInvocationEffectElmyra(AssistManagerGoogle assistManagerGoogle, OpaHomeButton opaHomeButton, OpaLockscreen opaLockscreen) {
        return new com.google.android.systemui.elmyra.feedback.AssistInvocationEffect(assistManagerGoogle, opaHomeButton, opaLockscreen);
    }

    @Provides
    @SysUISingleton
    static OpaHomeButton provideOpaHomeButton(KeyguardViewMediator keyguardViewMediator, StatusBar statusBar, NavigationModeController navigationModeController) {
        return new OpaHomeButton(keyguardViewMediator, statusBar, navigationModeController);
    }

    @Provides
    @SysUISingleton
    static OpaLockscreen provideOpaLockscreen(StatusBar statusBar, KeyguardStateController keyguardStateController) {
        return new OpaLockscreen(statusBar, keyguardStateController);
    }

    @Provides
    @SysUISingleton
    static SquishyNavigationButtons provideSquishyNavigationButtons(Context context, KeyguardViewMediator keyguardViewMediator, StatusBar statusBar, NavigationModeController navigationModeController) {
        return new SquishyNavigationButtons(context, keyguardViewMediator, statusBar, navigationModeController);
    }

    @Provides
    @SysUISingleton
    static com.google.android.systemui.elmyra.gates.TelephonyActivity provideTelephonyActivityElmyra(Context context, TelephonyListenerManager telephonyListenerManager) {
        return new com.google.android.systemui.elmyra.gates.TelephonyActivity(context, telephonyListenerManager);
    }

    @Provides
    @SysUISingleton
    static SetupWizardAction.Builder provideSetupWizardAction(Context context, StatusBar statusBar) {
        return new SetupWizardAction.Builder(context, statusBar);
    }

    @Provides
    @SysUISingleton
    static com.google.android.systemui.elmyra.actions.UnpinNotifications provideUnpinNotificationsElmyra(Context context, Optional<HeadsUpManager> optional) {
        return new com.google.android.systemui.elmyra.actions.UnpinNotifications(context, optional);
    }

    @Provides
    @SysUISingleton
    static com.google.android.systemui.elmyra.actions.LaunchOpa.Builder provideLaunchOpaElmyra(Context context, StatusBar statusBar) {
        return new com.google.android.systemui.elmyra.actions.LaunchOpa.Builder(context, statusBar);
    }

    @Provides
    @SysUISingleton
    static com.google.android.systemui.elmyra.actions.SilenceCall provideSilenceCallElmyra(Context context, TelephonyListenerManager telephonyListenerManager) {
        return new com.google.android.systemui.elmyra.actions.SilenceCall(context, telephonyListenerManager);
    }

    @Provides
    @SysUISingleton
    static com.google.android.systemui.elmyra.actions.SettingsAction.Builder provideSettingsActionElmyra(Context context, StatusBar statusBar) {
        return new com.google.android.systemui.elmyra.actions.SettingsAction.Builder(context, statusBar);
    }

    @Provides
    @SysUISingleton
    static CameraAction.Builder provideCameraAction(Context context, StatusBar statusBar) {
        return new CameraAction.Builder(context, statusBar);
    }
}
