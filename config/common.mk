include vendor/statix/build/core/vendor/*.mk

ifeq ($(PRODUCT_USES_QCOM_HARDWARE), true)
include vendor/statix/build/core/ProductConfigQcom.mk
endif

PRODUCT_BUILD_PROP_OVERRIDES += BUILD_UTC_DATE=0

ifeq ($(PRODUCT_GMS_CLIENTID_BASE),)
PRODUCT_SYSTEM_DEFAULT_PROPERTIES += \
    ro.com.google.clientidbase=android-google
else
PRODUCT_SYSTEM_DEFAULT_PROPERTIES += \
    ro.com.google.clientidbase=$(PRODUCT_GMS_CLIENTID_BASE)
endif

PRODUCT_SYSTEM_DEFAULT_PROPERTIES += \
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

#ifeq ($(AB_OTA_UPDATER),true)
#PRODUCT_COPY_FILES += \
#    vendor/statix/build/tools/backuptool_ab.sh:system/bin/backuptool_ab.sh \
#    vendor/statix/build/tools/backuptool_ab.functions:system/bin/backuptool_ab.functions \
#    vendor/statix/build/tools/backuptool_postinstall.sh:system/bin/backuptool_postinstall.sh
#endif

# Fix Google dialer
PRODUCT_COPY_FILES += \
    vendor/statix/prebuilt/common/etc/dialer_experience.xml:system/etc/sysconfig/dialer_experience.xml

# copy privapp permissions
PRODUCT_COPY_FILES += \
    vendor/statix/prebuilt/common/etc/permissions/privapp-permissions-statix.xml:$(TARGET_COPY_OUT_PRODUCT)/etc/permissions/privapp-permissions-statix.xml

# Backup Tool
#PRODUCT_COPY_FILES += \
#    vendor/statix/build/tools/backuptool.sh:install/bin/backuptool.sh \
#    vendor/statix/build/tools/backuptool.functions:install/bin/backuptool.functions \
#    vendor/statix/build/tools/50-statix.sh:system/addon.d/50-statix.sh

# Statix-specific init file
PRODUCT_COPY_FILES += \
    vendor/statix/prebuilt/common/etc/init.statix.rc:system/etc/init/init.statix.rc

# Packages
include vendor/statix/config/packages.mk

# Branding
include vendor/statix/config/branding.mk

# Sounds
include vendor/statix/config/pixel2-audio_prebuilt.mk

# Bootanimation
include vendor/statix/config/statix-bootanimation.mk

# Overlays
PRODUCT_ENFORCE_RRO_EXCLUDED_OVERLAYS += vendor/statix/overlay
DEVICE_PACKAGE_OVERLAYS += vendor/statix/overlay/common

# Fonts
include vendor/statix/config/fonts.mk
