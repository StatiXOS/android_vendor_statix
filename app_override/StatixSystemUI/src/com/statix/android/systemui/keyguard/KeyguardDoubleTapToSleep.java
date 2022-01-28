/**
 * Copyright (C) 2021 StatiX
 * SPDX-License-Identifer: Apache-2.0
 */

package com.statix.android.systemui.keyguard;

import com.android.systemui.statusbar.phone;
import android.content.Context;
import android.view.GestureDetector;

public class KeygaurdDoubleTapToSleep extends PanelViewController {
    
    private GestureDetector mDoubleTapGestureListener;

    mDoubleTapGestureListener = new GestureDetector(mView.getContext(),
            new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDoubleTap(MotionEvent event) {
            final PowerManager pm = (PowerManager) mView.getContext().getSystemService(
                    Context.POWER_SERVICE);
            pm.goToSleep(event.getEventTime());
            return true;
        }
    });

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mBarState == StatusBarState.KEYGUARD) {
                mDoubleTapGestureListener.onTouchEvent(event);
        }
    }
}