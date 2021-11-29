package com.statix.android.systemui.keyguard;

import android.app.PendingIntent;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Trace;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.SliceAction;

import com.android.systemui.R;
import com.android.systemui.keyguard.KeyguardSliceProvider;

import com.google.android.systemui.smartspace.SmartSpaceCard;
import com.google.android.systemui.smartspace.SmartSpaceController;
import com.google.android.systemui.smartspace.SmartSpaceData;
import com.google.android.systemui.smartspace.SmartSpaceUpdateListener;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

public class KeyguardSliceProviderStatix extends KeyguardSliceProvider implements SmartSpaceUpdateListener {
    private static final boolean DEBUG = Log.isLoggable("KeyguardSliceProvider", 3);
    private final Uri mCalendarUri = Uri.parse("content://com.android.systemui.keyguard/smartSpace/calendar");
    private boolean mHideSensitiveContent;
    private boolean mHideWorkContent = true;
    @Inject
    public SmartSpaceController mSmartSpaceController;
    private SmartSpaceData mSmartSpaceData;
    private final Uri mWeatherUri = Uri.parse("content://com.android.systemui.keyguard/smartSpace/weather");

    private static class AddShadowTask extends AsyncTask<Bitmap, Void, Bitmap> {
        private final float mBlurRadius;
        private final WeakReference<KeyguardSliceProviderStatix> mProviderReference;
        private final SmartSpaceCard mWeatherCard;

        AddShadowTask(KeyguardSliceProviderStatix keyguardSliceProviderStatix, SmartSpaceCard smartSpaceCard) {
            mProviderReference = new WeakReference<>(keyguardSliceProviderStatix);
            mWeatherCard = smartSpaceCard;
            mBlurRadius = keyguardSliceProviderStatix.getContext().getResources().getDimension(R.dimen.smartspace_icon_shadow);
        }

        private Bitmap applyShadow(Bitmap bitmap) {
            BlurMaskFilter blurMaskFilter = new BlurMaskFilter(mBlurRadius, BlurMaskFilter.Blur.NORMAL);
            Paint paint = new Paint();
            paint.setMaskFilter(blurMaskFilter);
            int[] iArr = new int[2];
            Bitmap extractAlpha = bitmap.extractAlpha(paint, iArr);
            Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(createBitmap);
            Paint paint2 = new Paint();
            paint2.setAlpha(70);
            canvas.drawBitmap(extractAlpha, (float) iArr[0], ((float) iArr[1]) + (mBlurRadius / 2.0f), paint2);
            extractAlpha.recycle();
            paint2.setAlpha(255);
            canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint2);
            return createBitmap;
        }

        @Override
        protected Bitmap doInBackground(Bitmap... bitmapArr) {
            return applyShadow(bitmapArr[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            KeyguardSliceProviderStatix keyguardSliceProviderStatix;
            synchronized (this) {
                mWeatherCard.setIcon(bitmap);
                keyguardSliceProviderStatix = mProviderReference.get();
            }
            if (keyguardSliceProviderStatix != null) {
                keyguardSliceProviderStatix.notifyChange();
            }
        }
    }

    private void addWeather(ListBuilder listBuilder) {
        SmartSpaceCard weatherCard = mSmartSpaceData.getWeatherCard();
        if (weatherCard != null && !weatherCard.isExpired()) {
            ListBuilder.RowBuilder title = new ListBuilder.RowBuilder(mWeatherUri).setTitle(weatherCard.getTitle());
            Bitmap icon = weatherCard.getIcon();
            if (icon != null) {
                IconCompat createWithBitmap = IconCompat.createWithBitmap(icon);
                createWithBitmap.setTintMode(PorterDuff.Mode.DST);
                title.addEndItem(createWithBitmap, 1);
            }
            listBuilder.addRow(title);
        }
    }

    public Slice onBindSlice(Uri uri) {
        Slice build;
        SliceAction sliceAction = null;
        boolean z = false;
        Trace.beginSection("KeyguardSliceProviderStatix#onBindSlice");
        ListBuilder listBuilder = new ListBuilder(getContext(), mSliceUri, -1);
        synchronized (this) {
            SmartSpaceCard currentCard = mSmartSpaceData.getCurrentCard();
            if (currentCard != null && !currentCard.isExpired() && !TextUtils.isEmpty(currentCard.getTitle())) {
                boolean isSensitive = currentCard.isSensitive();
                boolean z2 = isSensitive && !mHideSensitiveContent && !currentCard.isWorkProfile();
                boolean z3 = isSensitive && !mHideWorkContent && currentCard.isWorkProfile();
                if (!isSensitive || z2 || z3) {
                    z = true;
                }
            }
            if (z) {
                Bitmap icon = currentCard.getIcon();
                IconCompat createWithBitmap = icon == null ? null : IconCompat.createWithBitmap(icon);
                PendingIntent pendingIntent = currentCard.getPendingIntent();
                if (!(createWithBitmap == null || pendingIntent == null)) {
                    sliceAction = SliceAction.create(pendingIntent, createWithBitmap, 1, currentCard.getTitle());
                }
                ListBuilder.HeaderBuilder title = new ListBuilder.HeaderBuilder(mHeaderUri).setTitle(currentCard.getFormattedTitle());
                if (sliceAction != null) {
                    title.setPrimaryAction(sliceAction);
                }
                listBuilder.setHeader(title);
                String subtitle = currentCard.getSubtitle();
                if (subtitle != null) {
                    ListBuilder.RowBuilder title2 = new ListBuilder.RowBuilder(mCalendarUri).setTitle(subtitle);
                    if (createWithBitmap != null) {
                        title2.addEndItem(createWithBitmap, 1);
                    }
                    if (sliceAction != null) {
                        title2.setPrimaryAction(sliceAction);
                    }
                    listBuilder.addRow(title2);
                }
                addZenModeLocked(listBuilder);
                addPrimaryActionLocked(listBuilder);
                Trace.endSection();
                build = listBuilder.build();
            } else {
                if (needsMediaLocked()) {
                    addMediaLocked(listBuilder);
                } else {
                    listBuilder.addRow(new ListBuilder.RowBuilder(mDateUri).setTitle(getFormattedDateLocked()));
                }
                addWeather(listBuilder);
                addNextAlarmLocked(listBuilder);
                addZenModeLocked(listBuilder);
                addPrimaryActionLocked(listBuilder);
                build = listBuilder.build();
                if (DEBUG) {
                    Log.d("KeyguardSliceProvider", "Binding slice: " + build);
                }
                Trace.endSection();
            }
        }
        return build;
    }

    public boolean onCreateSliceProvider() {
        boolean onCreateSliceProvider = super.onCreateSliceProvider();
        mSmartSpaceData = new SmartSpaceData();
        mSmartSpaceController.addListener(this);
        return onCreateSliceProvider;
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        mSmartSpaceController.removeListener(this);
    }

    @Override // com.google.android.systemui.smartspace.SmartSpaceUpdateListener
    public void onSensitiveModeChanged(boolean z, boolean z2) {
        boolean z3;
        boolean z4 = true;
        synchronized (this) {
            if (mHideSensitiveContent != z) {
                mHideSensitiveContent = z;
                if (DEBUG) {
                    Log.d("KeyguardSliceProvider", "Public mode changed, hide data: " + z);
                }
                z3 = true;
            } else {
                z3 = false;
            }
            if (mHideWorkContent != z2) {
                mHideWorkContent = z2;
                if (DEBUG) {
                    Log.d("KeyguardSliceProvider", "Public work mode changed, hide data: " + z2);
                }
            } else {
                z4 = z3;
            }
        }
        if (z4) {
            notifyChange();
        }
    }

    @Override // com.google.android.systemui.smartspace.SmartSpaceUpdateListener
    public void onSmartSpaceUpdated(SmartSpaceData smartSpaceData) {
        synchronized (this) {
            mSmartSpaceData = smartSpaceData;
        }
        SmartSpaceCard weatherCard = smartSpaceData.getWeatherCard();
        if (weatherCard == null || weatherCard.getIcon() == null || weatherCard.isIconProcessed()) {
            notifyChange();
            return;
        }
        weatherCard.setIconProcessed(true);
        new AddShadowTask(this, weatherCard).execute(weatherCard.getIcon());
    }

    /* access modifiers changed from: protected */
    public void updateClockLocked() {
        notifyChange();
    }
}
