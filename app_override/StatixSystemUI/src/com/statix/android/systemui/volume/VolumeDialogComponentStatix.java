/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.statix.android.systemui.volume;

import android.content.Context;
import com.android.systemui.dagger.SysUISingleton;
import com.android.systemui.demomode.DemoModeController;
import com.android.systemui.keyguard.KeyguardViewMediator;
import com.android.systemui.plugins.VolumeDialog;
import com.android.systemui.volume.VolumeDialogComponent;
import com.android.systemui.volume.VolumeDialogControllerImpl;

import com.statix.android.systemui.tristate.TriStateUiController;
import com.statix.android.systemui.tristate.TriStateUiControllerImpl;

import javax.inject.Inject;

@SysUISingleton
public class VolumeDialogComponentStatix extends VolumeDialogComponent
        implements TriStateUiController.UserActivityListener  {

    private TriStateUiControllerImpl mTriStateController;

    @Inject
    public VolumeDialogComponentStatix(Context context, KeyguardViewMediator keyguardViewMediator,
            VolumeDialogControllerImpl volumeDialogController,
            DemoModeController demoModeController) {
        super(context, keyguardViewMediator, volumeDialogController, demoModeController);
        boolean hasAlertSlider = mContext.getResources().
                getBoolean(com.android.internal.R.bool.config_hasAlertSlider);
        Dependency.get(ExtensionController.class).newExtension(VolumeDialog.class)
                .withPlugin(VolumeDialog.class)
                .withDefault(this::createDefault)
                .withCallback(dialog -> {
                    if (mDialog != null) {
                        mDialog.destroy();
                    }
                    mDialog = dialog;
                    mDialog.init(LayoutParams.TYPE_VOLUME_OVERLAY, mVolumeDialogCallback);
                    if (hasAlertSlider) {
                        if (mTriStateController != null) {
                            mTriStateController.destroy();
                        }
                        mTriStateController = new TriStateUiControllerImpl(mContext);
                        mTriStateController.init(LayoutParams.TYPE_VOLUME_OVERLAY, this);
                    }
                }).build();
    }

    @Override
    public void onTriStateUserActivity() {
        onUserActivity();
    }

    /** This method is called while calling the super constructor. */
    @Override
    protected VolumeDialog createDefault() {
        mCarVolumeDialog = new CarVolumeDialogImpl(mContext);
        return mCarVolumeDialog;
    }
}
