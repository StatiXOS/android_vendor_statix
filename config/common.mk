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
    persist.sys.disable_rescue=true \
    ro.build.selinux=1

# Fix Google dialer
PRODUCT_COPY_FILES += \
    vendor/aosp/prebuilt/etc/dialer_experience.xml:system/etc/sysconfig/dialer_experience.xml

# Backup Tool
PRODUCT_COPY_FILES += \
    vendor/aosp/tools/backuptool.sh:install/bin/backuptool.sh \
    vendor/aosp/tools/backuptool.functions:install/bin/backuptool.functions \
    vendor/aosp/tools/50-aosp.sh:system/addon.d/50-aosp.sh

# Packages
include vendor/aosp/config/packages.mk

# Branding
include vendor/aosp/config/branding.mk

#Inherit from 64-bit config
ifeq ($(filter angler marlin taimen sailfish walleye ,$(TARGET_PRODUCT)),)
    $(call inherit-product, $(SRC_TARGET_DIR)/product/core_64_bit.mk)
    $(call inherit-product, $(SRC_TARGET_DIR)/product/aosp_base_telephony.mk)
