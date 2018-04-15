# Set all versions
DATE := $(shell date -u +%Y%m%d)
TIME := $(shell date -u +%H%M)
STATIX_VERSION_MAJOR := 0.1-TEST
STATIX_VERSION := $(TARGET_PRODUCT)-$(DATE)-8.1.0-$(STATIX_VERSION_MAJOR)-$(TIME)

STATIX_BUILD_TYPE := UNOFFICIAL
ifeq ($(filter-out OFFICIAL,$(STATIX_BUILD_TYPE)),)
    PRODUCT_DEFAULT_DEV_CERTIFICATE := ../.keys/releasekey
endif


PRODUCT_PROPERTY_OVERRIDES += \
    BUILD_DISPLAY_ID=$(BUILD_ID) \
    ro.statix.version=$(STATIX_VERSION) \
    ro.mod.version=$(BUILD_ID)-$(DATE)-$(STATIX_VERSION_MAJOR)
