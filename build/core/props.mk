ADDITIONAL_BUILD_PROPERTIES += \
    ro.statix.version=$(STATIX_BASE_VERSION)-$(STATIX_BUILD_TYPE)-$(BUILD_DATE)-$(BUILD_TIME) \
    ro.mod.version=$(BUILD_ID)-$(BUILD_DATE)-$(STATIX_BASE_VERSION) \
    ro.statix.fingerprint=$(ROM_FINGERPRINT)
