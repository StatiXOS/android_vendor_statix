# Copyright (C) 2021 The LineageOS Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

$(call inherit-product, build/target/product/sdk_phone_x86.mk)
$(call inherit-product, vendor/statix/config/common.mk)
$(call inherit-product, vendor/statix/config/gsm.mk)

PRODUCT_COPY_FILES += \
    device/generic/goldfish/data/etc/permissions/privapp-permissions-goldfish.xml:$(TARGET_COPY_OUT_SYSTEM)/etc/permissions/privapp-permissions-goldfish.xml \

# Allow building otatools
TARGET_FORCE_OTA_PACKAGE := true

# Don't include GApps
TARGET_DOES_NOT_USE_GAPPS := true

# SDK addon
PRODUCT_SDK_ADDON_NAME := statix
PRODUCT_SDK_ADDON_SYS_IMG_SOURCE_PROP := $(LOCAL_PATH)/source.properties

# Don't build kernel
TARGET_NO_KERNEL_OVERRIDE := true

# Overrides
PRODUCT_NAME := statix_sdk_phone_x86
PRODUCT_MODEL := StatiXOS Android SDK built for x86
