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
    vendor/statix/prebuilt/etc/dialer_experience.xml:system/etc/sysconfig/dialer_experience.xml

# Backup Tool
#PRODUCT_COPY_FILES += \
#    vendor/statix/tools/backuptool.sh:install/bin/backuptool.sh \
#    vendor/statix/tools/backuptool.functions:install/bin/backuptool.functions \
#    vendor/statix/tools/50-statix.sh:system/addon.d/50-statix.sh

# Overlays
PRODUCT_PACKAGE_OVERLAYS += vendor/statix/overlay/common

# Packages
include vendor/statix/config/packages.mk

# Branding
include vendor/statix/config/branding.mk

# Fonts
PRODUCT_COPY_FILES += \
    vendor/statix/fonts/GoogleSans-Regular.ttf:system/fonts/GoogleSans-Regular.ttf \
    vendor/statix/fonts/GoogleSans-Medium.ttf:system/fonts/GoogleSans-Medium.ttf \
    vendor/statix/fonts/GoogleSans-MediumItalic.ttf:system/fonts/GoogleSans-MediumItalic.ttf \
    vendor/statix/fonts/GoogleSans-Italic.ttf:system/fonts/GoogleSans-Italic.ttf \
    vendor/statix/fonts/GoogleSans-Bold.ttf:system/fonts/GoogleSans-Bold.ttf \
    vendor/statix/fonts/GoogleSans-BoldItalic.ttf:system/fonts/GoogleSans-BoldItalic.ttf

# Volume steps
PRODUCT_PROPERTY_OVERRIDES += \
    ro.config.media_vol_steps=24 \
    ro.config.vc_call_vol_steps=8

# Inherit from 64-bit config
ifeq ($(filter arm64 ,$(TARGET_ARCH)),)
    $(call inherit-product, $(SRC_TARGET_DIR)/product/core_64_bit.mk)
    $(call inherit-product, $(SRC_TARGET_DIR)/product/aosp_base_telephony.mk)
endif
