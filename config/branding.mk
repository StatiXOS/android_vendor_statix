# Set all versions
DATE := $(shell date -u +%Y%m%d)
AOSP_VERSION := $(TARGET_PRODUCT)-$(DATE)-$(shell date -u +%H%M)

PRODUCT_PROPERTY_OVERRIDES += \
    BUILD_DISPLAY_ID=$(BUILD_ID) \
    ro.aosp.version=$(AOSP_VERSION) \
    ro.mod.version=$(BUILD_ID)-$(DATE)