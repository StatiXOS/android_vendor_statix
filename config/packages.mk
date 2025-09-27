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
    StatiXOSWalls

ifneq ($(STATIX_MINIMAL), true)
PRODUCT_PACKAGES += \
    QuickAccessWallet
endif

# APEX
DISABLE_DEXPREOPT_CHECK := true

PRODUCT_PACKAGES += \
    com.google.android.permission

# App overrides
PRODUCT_PACKAGES += \
    StatixLauncher \
    StatixSystemUI \
    StatixSettings

ifneq ($(STATIX_MINIMAL), true)
PRODUCT_PACKAGES += \
    WallpaperPickerGoogleRelease
endif

# BtHelper
PRODUCT_PACKAGES += \
    BtHelper

# Camera
PRODUCT_PACKAGES += \
    Aperture

# Preopt StatixSystemUI
PRODUCT_DEXPREOPT_SPEED_APPS += \
    StatixSystemUI

# Google Pixel Launcher
ifneq ($(STATIX_MINIMAL), true)
ifeq ($(INCLUDE_PIXEL_LAUNCHER),true)
PRODUCT_PACKAGES += \
    NexusLauncherRelease
endif
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
