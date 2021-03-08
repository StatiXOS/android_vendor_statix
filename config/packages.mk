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

# Updaters
ifeq ($(STATIX_BUILD_TYPE),OFFICIAL)
PRODUCT_PACKAGES += \
    Updater
else
PRODUCT_PACKAGES += \
    LocalUpdater
endif

PRODUCT_PACKAGES += \
     StatixOverlayStub

# Charger images
PRODUCT_PACKAGES += \
    charger_res_images

-include vendor/statix/config/overlay.mk
