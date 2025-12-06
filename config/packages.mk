#
# Copyright (C) 2018-2022 StatiXOS
#
# SPDX-License-Identifier: Apache-2.0
#

# Include librsjni explicitly to workaround GMS issue
PRODUCT_PACKAGES += \
    librsjni

# StatiX Packages
PRODUCT_PACKAGES += \
    StatiXOSWalls \
    QuickAccessWallet

# APEX
DISABLE_DEXPREOPT_CHECK := true

PRODUCT_MAINLINE_BLUETOOTH_SEPOLICY_DEV_CERTIFICATES=vendor/statix/build/target/product/security

PRODUCT_MAINLINE_SEPOLICY_DEV_CERTIFICATES=vendor/statix-prebuilts/apex/certificates

PRODUCT_PACKAGES += \
    com.google.android.cellbroadcast \
    com.google.android.permission \
    com.google.android.tethering \
    com.google.android.wifi

# App overrides
PRODUCT_PACKAGES += \
    StatixLauncher \
    StatixSystemUI \
    StatixSettings \
    WallpaperPickerGoogleRelease

# BtHelper
PRODUCT_PACKAGES += \
    BtHelper

# Camera
PRODUCT_PACKAGES += \
    Aperture

# Google Pixel Launcher
ifeq ($(INCLUDE_PIXEL_LAUNCHER),true)
PRODUCT_PACKAGES += \
    NexusLauncherRelease
endif

# Updaters
ifeq ($(STATIX_BUILD_TYPE),OFFICIAL)
PRODUCT_PACKAGES += \
    Updater
endif

# Some useful shell based utilities for Android
PRODUCT_PACKAGES += \
    htop \
    nano \
    vim

# Charger images
PRODUCT_PACKAGES += \
    charger_res_images \
    charger_res_images_vendor_pixel

-include vendor/statix/config/overlay.mk
