DATE := $(shell date +%Y%m%d)
TIME := $(shell date +%H%M)

# Versioning System
# Use signing keys for only official
ifeq ($(STATIX_BUILD_TYPE),OFFICIAL)
    PRODUCT_DEFAULT_DEV_CERTIFICATE := ../.keys/releasekey
endif

ifndef STATIX_BUILD_TYPE
    STATIX_BUILD_TYPE := UNOFFICIAL
endif

# Set all versions
STATIX_BASE_VERSION := v2.0
STATIX_VERSION := $(TARGET_PRODUCT)-$(DATE)-$(TIME)-9-$(STATIX_BASE_VERSION)-$(STATIX_BUILD_TYPE)
ROM_FINGERPRINT := StatiXOS/$(PLATFORM_VERSION)/$(STATIX_BUILD_TYPE)/$(DATE)$(TIME)

# Declare it's a StatiX build
STATIX_BUILD := true

PRODUCT_PROPERTY_OVERRIDES += \
    BUILD_DISPLAY_ID=$(BUILD_ID) \
    ro.statix.version=v2.0-$(STATIX_BUILD_TYPE)-$(DATE)-$(TIME) \
    ro.mod.version=$(BUILD_ID)-$(DATE)-$(STATIX_BASE_VERSION) \
    ro.statix.fingerprint=$(ROM_FINGERPRINT)
