# Build fingerprint
ifneq ($(BUILD_FINGERPRINT),)
ADDITIONAL_SYSTEM_PROPERTIES += \
    ro.build.fingerprint=$(BUILD_FINGERPRINT)
endif

ADDITIONAL_SYSTEM_PROPERTIES += \
    ro.statix.version=$(STATIX_BASE_VERSION)-$(STATIX_BUILD_TYPE)-$(BUILD_DATE) \
    ro.statix.base.version=$(STATIX_BASE_VERSION) \
    ro.mod.version=$(BUILD_ID)-$(BUILD_DATE)-$(STATIX_BASE_VERSION) \
    ro.statix.fingerprint=$(ROM_FINGERPRINT) \
    ro.statix.buildtype=$(STATIX_BUILD_TYPE) \
    ro.statix.device=$(TARGET_DEVICE)
