# Include librsjni explicitly to workaround GMS issue
PRODUCT_PACKAGES += \
    librsjni

# Packages
# Turbo
#PRODUCT_PACKAGES += \
#    Turbo \
#    turbo.xml \
#    privapp-permissions-turbo.xml \

# StatiX Packages
PRODUCT_PACKAGES += \
     ThemePicker \
     WeatherProvider \
     CustomDoze

PRODUCT_PACKAGES += \
     StatixOverlayStub

# Charger images
PRODUCT_PACKAGES += \
    charger_res_images \
    product_charger_res_images

# WeatherProvider
PRODUCT_COPY_FILES += \
    vendor/statix/prebuilt/common/etc/permissions/com.android.providers.weather.xml:system/etc/permissions/com.android.providers.weather.xml \
    vendor/statix/prebuilt/common/etc/default-permissions/com.android.providers.weather.xml:system/etc/default-permissions/com.android.providers.weather.xml

# Fonts
PRODUCT_PACKAGES += \
    FontArbutusSourceOverlay \
    FontArvoLatoOverlay \
    FontRubikRubikOverlay \
    FontGoogleSansOverlay \

-include vendor/statix/config/overlay.mk
