package com.statix.android.systemui.dagger;

import com.android.systemui.dagger.DefaultComponentBinder;
import com.android.systemui.dagger.DependencyProvider;
import com.android.systemui.dagger.SysUISingleton;
import com.android.systemui.dagger.SystemUIBinder;
import com.android.systemui.dagger.SysUIComponent;
import com.android.systemui.dagger.SystemUIModule;

import com.statix.android.systemui.gamedashboard.GameDashboardModule;
import com.statix.android.systemui.keyguard.KeyguardSliceProviderStatix;
import com.statix.android.systemui.smartspace.KeyguardSmartspaceController;

import dagger.Subcomponent;

@SysUISingleton
@Subcomponent(modules = {
        DefaultComponentBinder.class,
        DependencyProvider.class,
        GameDashboardModule.class,
        SystemUIModule.class,
        SystemUIStatixBinder.class,
        SystemUIStatixModule.class})
public interface SysUIComponentStatix extends SysUIComponent {
    @SysUISingleton
    @Subcomponent.Builder
    interface Builder extends SysUIComponent.Builder {
        SysUIComponentStatix build();
    }

    /**
     * Member injection into the supplied argument.
     */
    void inject(KeyguardSliceProviderStatix keyguardSliceProviderStatix);

    @SysUISingleton
    KeyguardSmartspaceController createKeyguardSmartspaceController();
}
