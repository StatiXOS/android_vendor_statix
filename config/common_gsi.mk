PRODUCT_PROPERTY_OVERRIDES += \
    keyguard.no_require_sim=true \
    dalvik.vm.debug.alloc=0 \
    ro.url.legal=http://www.google.com/intl/%s/mobile/android/basic/phone-legal.html \
    ro.url.legal.android_privacy=http://www.google.com/intl/%s/mobile/android/basic/privacy.html \
    ro.error.receiver.system.apps=com.google.android.gms \
    ro.setupwizard.enterprise_mode=1 \
    ro.com.android.dataroaming=false \
    ro.atrace.core.services=com.google.android.gms,com.google.android.gms.ui,com.google.android.gms.persistent \
    ro.com.android.dateformat=MM-dd-yyyy \
    ro.boot.vendor.overlay.theme=com.potato.overlay.accent.Red \
    ro.build.selinux=1

# Fix Google dialer
PRODUCT_COPY_FILES += \
    vendor/statix/prebuilt/common/etc/dialer_experience.xml:system/etc/sysconfig/dialer_experience.xml

# Overlays
PRODUCT_PACKAGE_OVERLAYS += vendor/statix/overlay/common

# Packages
include vendor/statix/config/packages.mk

# Branding
include vendor/statix/config/branding.mk

# Volume steps
PRODUCT_PROPERTY_OVERRIDES += \
    ro.config.media_vol_steps=24 \
    ro.config.vc_call_vol_steps=8

