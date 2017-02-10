package com.statix.launcher;

import android.content.Context;
import android.os.PowerManager;
import android.view.MotionEvent;

import com.android.launcher3.Launcher;
import com.android.launcher3.Workspace;
import com.android.launcher3.touch.WorkspaceTouchListener;

public class StatixWorkspaceListener extends WorkspaceTouchListener {

    private final Workspace mWorkspace;
    private final PowerManager mPm;

    @Override
    public WorkspaceTouchListener(Launcher launcher, Workspace workspace) {
        mWorkspace = workspace;
        mPm = (PowerManager) workspace.getContext().getSystemService(Context.POWER_SERVICE);
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        mPm.goToSleep(event.getEventTime());
        return true;
    }
}
