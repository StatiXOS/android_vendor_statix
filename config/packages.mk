# Include librsjni explicitly to workaround GMS issue
PRODUCT_PACKAGES += \
    librsjni

# StatiX Packages
PRODUCT_PACKAGES += \
    CustomDoze \
    StitchImage \
    ThemePicker \
    WeatherProvider

PRODUCT_PACKAGES += \
     StatixOverlayStub

# Charger images
PRODUCT_PACKAGES += \
    charger_res_images

# WeatherProvider
PRODUCT_COPY_FILES += \
    vendor/statix/prebuilt/common/etc/permissions/com.android.providers.weather.xml:system/etc/permissions/com.android.providers.weather.xml \
    vendor/statix/prebuilt/common/etc/default-permissions/com.android.providers.weather.xml:system/etc/default-permissions/com.android.providers.weather.xml

-include vendor/statix/config/overlay.mk
