package com.statix.android.settings.overlay;

import com.android.settings.overlay.FeatureFactoryImpl;
import com.android.settings.applications.GameSettingsFeatureProviderImpl;
import com.google.android.settings.accounts.AccountFeatureProviderGoogleImpl;
import com.google.android.settings.fuelgauge.PowerUsageFeatureProviderGoogleImpl;

public final class FeatureFactoryImplStatix extends FeatureFactoryImpl {
    @Override
    public PowerUsageFeatureProvider getPowerUsageFeatureProvider(Context context) {
        if (mPowerUsageFeatureProvider == null) {
            mPowerUsageFeatureProvider = new PowerUsageFeatureProviderGoogleImpl(
                    context.getApplicationContext());
        }
        return mPowerUsageFeatureProvider;
    }

    @Override
    public AccountFeatureProvider getAccountFeatureProvider() {
        if (mAccountFeatureProvider == null) {
            mAccountFeatureProvider = new AccountFeatureProviderGoogleImpl();
        }
        return mAccountFeatureProvider;
    }

    @Override
    public GameSettingsFeatureProvider getGameSettingsFeatureProvider() {
        if (mGameSettingsFeatureProvider == null) {
            mGameSettingsFeatureProvider = new GameSettingsFeatureProviderImpl();
        }
        return mGameSettingsFeatureProvider;
    }
}