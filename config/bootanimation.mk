#
# Copyright (C) 2018 The Dirty Unicorns Project
# Copyright (C) 2022 StatiXOS
#
# SPDX-License-Identifer: Apache-2.0

# Boot Animation
TARGET_SCREEN_WIDTH ?= 1080
TARGET_SCREEN_HEIGHT ?= 1920

PRODUCT_PACKAGES += \
    bootanimation.zip

PRODUCT_ARTIFACT_PATH_REQUIREMENT_ALLOWED_LIST += \
    system/media/bootanimation.zip
