# Include librsjni explicitly to workaround GMS issue
PRODUCT_PACKAGES += \
    librsjni

# StatiX Packages
PRODUCT_PACKAGES += \
    CustomDoze \
    StitchImage \
    ThemePicker

# Local Updater
ifneq ($(AB_OTA_PARTITIONS),)
PRODUCT_PACKAGES += \
    LocalUpdater
endif

# Charger images
PRODUCT_PACKAGES += \
    charger_res_images

# Overlays
include vendor/statix/config/overlay.mk
