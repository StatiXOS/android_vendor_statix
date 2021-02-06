# Set date and time
BUILD_DATE := $(shell date +%Y%m%d)
BUILD_TIME := $(shell date +%H%M)

## Versioning System
# Set all versions
STATIX_BASE_VERSION := v4.2
STATIX_PLATFORM_VERSION := 11

# Use signing keys and don't print date & time in the final zip for official builds
ifeq ($(STATIX_BUILD_TYPE),OFFICIAL)
    PRODUCT_DEFAULT_DEV_CERTIFICATE := ./.keys/releasekey
    STATIX_VERSION := $(TARGET_PRODUCT)-$(BUILD_DATE)-$(STATIX_PLATFORM_VERSION)-$(STATIX_BASE_VERSION)-$(STATIX_BUILD_TYPE)
else
    STATIX_VERSION := $(TARGET_PRODUCT)-$(BUILD_DATE)-$(BUILD_TIME)-$(STATIX_PLATFORM_VERSION)-$(STATIX_BASE_VERSION)-$(STATIX_BUILD_TYPE)
endif

ifndef STATIX_BUILD_TYPE
    STATIX_BUILD_TYPE := UNOFFICIAL
endif

# Fingerprint
ROM_FINGERPRINT := StatiXOS/$(PLATFORM_VERSION)/$(STATIX_BUILD_TYPE)/$(BUILD_DATE)$(BUILD_TIME)
# Declare it's a StatiX build
STATIX_BUILD := true