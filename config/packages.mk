# Include librsjni explicitly to workaround GMS issue
PRODUCT_PACKAGES += \
    librsjni

# StatiX Packages
PRODUCT_PACKAGES += \
    CustomDoze \
    ThemePicker \
    SimpleDeviceConfig \
    StatiXOSWalls \
    QuickAccessWallet

# Local Updater
ifneq ($(AB_OTA_PARTITIONS),)
PRODUCT_PACKAGES += \
    LocalUpdater
endif

PRODUCT_PACKAGES += \
     StatixOverlayStub

# Charger images
PRODUCT_PACKAGES += \
    charger_res_images

-include vendor/statix/config/overlay.mk
