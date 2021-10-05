package com.statix.android.systemui;

import android.content.Context;

import com.statix.android.systemui.dagger.DaggerGlobalRootComponentStatix;
import com.statix.android.systemui.dagger.GlobalRootComponentStatix;

import com.android.systemui.SystemUIFactory;
import com.android.systemui.dagger.GlobalRootComponent;

public class SystemUIStatixFactory extends SystemUIFactory {
    @Override
    protected GlobalRootComponent buildGlobalRootComponent(Context context) {
        return DaggerGlobalRootComponentStatix.builder()
                .context(context)
                .build();
    }
}
