# Set all versions
DATE := $(shell date -u +%Y%m%d)
TIME := $(shell date -u +%H%M)
ifndef STATIX_BUILD_TYPE
       STATIX_BUILD_TYPE := UNOFFICIAL
endif
STATIX_BASE_VERSION := v2.0
STATIX_VERSION := $(TARGET_PRODUCT)-$(DATE)-$(TIME)-9-$(STATIX_BASE_VERSION)-$(STATIX_BUILD_TYPE)

ifeq ($(filter-out OFFICIAL,$(STATIX_BUILD_TYPE)),)
    PRODUCT_DEFAULT_DEV_CERTIFICATE := ../.keys/releasekey
endif


PRODUCT_PROPERTY_OVERRIDES += \
    BUILD_DISPLAY_ID=$(BUILD_ID) \
    ro.statix.version=v2.0-$(BUILD_ID)-$(STATIX_BUILD_TYPE) \
    ro.mod.version=$(BUILD_ID)-$(DATE)-$(STATIX_BASE_VERSION)
