# Bring in Qualcomm helper macros
include vendor/statix/build/core/utils.mk

A_FAMILY := msm7x27a msm7x30 msm8660 msm8960
B_FAMILY := msm8226 msm8610 msm8974
B64_FAMILY := msm8992 msm8994
BR_FAMILY := msm8909 msm8916
UM_3_18_FAMILY := msm8917 msm8937 msm8953 msm8996
UM_4_4_FAMILY := msm8998 sdm660
UM_4_9_FAMILY := sdm845
UM_4_14_FAMILY := $(MSMNILE) $(MSMSTEPPE) $(TRINKET) $(ATOLL)
UM_4_19_FAMILY := $(KONA) $(LITO)
UM_5_4_FAMILY := $(LAHAINA)

UM_PLATFORMS := $(UM_3_18_FAMILY) $(UM_4_4_FAMILY) $(UM_4_9_FAMILY) $(UM_4_14_FAMILY) $(UM_4_19_FAMILY) $(UM_5_4_FAMILY)
QSSI_SUPPORTED_PLATFORMS := $(UM_4_4_FAMILY) $(UM_4_9_FAMILY) $(UM_4_14_FAMILY) $(UM_4_19_FAMILY) $(UM_5_4_FAMILY)

BOARD_USES_ADRENO := true

# Add qtidisplay to soong config namespaces
SOONG_CONFIG_NAMESPACES += qtidisplay

# Add supported variables to qtidisplay config
SOONG_CONFIG_qtidisplay += \
    drmpp \
    headless \
    llvmsa \
    gralloc4 \
    default

# Set default values for qtidisplay config
SOONG_CONFIG_qtidisplay_drmpp ?= false
SOONG_CONFIG_qtidisplay_headless ?= false
SOONG_CONFIG_qtidisplay_llvmsa ?= false
SOONG_CONFIG_qtidisplay_gralloc4 ?= false
SOONG_CONFIG_qtidisplay_default ?= true

# UM platforms no longer need this set on O+
ifneq ($(call is-board-platform-in-list, $(UM_PLATFORMS)),true)
    TARGET_USES_QCOM_BSP := true
endif

# Tell HALs that we're compiling an AOSP build with an in-line kernel
TARGET_COMPILE_WITH_MSM_KERNEL := true

ifneq ($(filter msm7x27a msm7x30 msm8660 msm8960,$(PRODUCT_BOARD_PLATFORM)),)
    TARGET_USES_QCOM_BSP_LEGACY := true
    # Enable legacy audio functions
    ifeq ($(BOARD_USES_LEGACY_ALSA_AUDIO),true)
        ifneq ($(filter msm8960,$(PRODUCT_BOARD_PLATFORM)),)
            USE_CUSTOM_AUDIO_POLICY := 1
        endif
    endif
endif

# Allow building audio encoders
TARGET_USES_QCOM_MM_AUDIO := true

# Enable color metadata for all UM targets
ifeq ($(call is-board-platform-in-list, $(UM_PLATFORMS)),true)
    TARGET_USES_COLOR_METADATA := true
endif

# Enable DRM PP driver on UM platforms that support it
ifeq ($(call is-board-platform-in-list, $(UM_4_9_FAMILY) $(UM_4_14_FAMILY) $(UM_4_19_FAMILY) $(UM_5_4_FAMILY)),true)
    SOONG_CONFIG_qtidisplay_drmpp := true
    TARGET_USES_DRM_PP := true
endif

# Enable Gralloc4 on UM platforms that support it
ifneq ($(filter $(UM_5_4_FAMILY),$(PRODUCT_BOARD_PLATFORM)),)
    SOONG_CONFIG_qtidisplay_gralloc4 := true
endif

# List of targets that use master side content protection
MASTER_SIDE_CP_TARGET_LIST := msm8996 $(UM_4_4_FAMILY) $(UM_4_9_FAMILY) $(UM_4_14_FAMILY) $(UM_4_19_FAMILY) $(UM_5_4_FAMILY)

# Every qcom platform is considered a vidc target
MSM_VIDC_TARGET_LIST := $(PRODUCT_BOARD_PLATFORM)

ifeq ($(call is-board-platform-in-list, $(A_FAMILY)),true)
    QCOM_HARDWARE_VARIANT := msm8960
    TARGET_KERNEL_VERSION := 3.4
else ifeq ($(call is-board-platform-in-list, $(B_FAMILY)),true)
    QCOM_HARDWARE_VARIANT := msm8974
    TARGET_KERNEL_VERSION := 3.4
else ifeq ($(call is-board-platform-in-list, $(B64_FAMILY)),true)
    QCOM_HARDWARE_VARIANT := msm8994
    TARGET_KERNEL_VERSION := 3.10
else ifeq ($(call is-board-platform-in-list, $(BR_FAMILY)),true)
    QCOM_HARDWARE_VARIANT := msm8916
else ifeq ($(call is-board-platform-in-list, $(UM_3_18_FAMILY)),true)
    QCOM_HARDWARE_VARIANT := msm8996
    TARGET_USES_QCOM_UM_3_18_FAMILY := true
    TARGET_KERNEL_VERSION := 3.18
else ifeq ($(call is-board-platform-in-list, $(UM_4_4_FAMILY)),true)
    QCOM_HARDWARE_VARIANT := msm8998
    TARGET_USES_QCOM_UM_4_4_FAMILY := true
    TARGET_KERNEL_VERSION := 4.4
else ifeq ($(call is-board-platform-in-list, $(UM_4_9_FAMILY)),true)
    QCOM_HARDWARE_VARIANT := sdm845
    TARGET_USES_QCOM_UM_4_9_FAMILY := true
    TARGET_KERNEL_VERSION := 4.9
else ifeq ($(call is-board-platform-in-list, $(UM_4_14_FAMILY)),true)
    QCOM_HARDWARE_VARIANT := sm8150
    TARGET_USES_QCOM_UM_4_14_FAMILY := true
    TARGET_KERNEL_VERSION := 4.14
else ifeq ($(call is-board-platform-in-list, $(UM_4_19_FAMILY)),true)
    QCOM_HARDWARE_VARIANT := sm8250
    TARGET_USES_QCOM_UM_4_19_FAMILY := true
    TARGET_KERNEL_VERSION := 4.19
else ifeq ($(call is-board-platform-in-list, $(UM_5_4_FAMILY)),true)
    QCOM_HARDWARE_VARIANT := sm8350
    TARGET_KERNEL_VERSION := 5.4
else
    QCOM_HARDWARE_VARIANT := $(PRODUCT_BOARD_PLATFORM)
endif

# Allow a device to manually override which HALs it wants to use
ifneq ($(OVERRIDE_QCOM_HARDWARE_VARIANT),)
QCOM_HARDWARE_VARIANT := $(OVERRIDE_QCOM_HARDWARE_VARIANT)
endif

# Required for frameworks/native
ifeq ($(call is-board-platform-in-list, $(UM_PLATFORMS)),true)
    TARGET_USES_QCOM_UM_FAMILY := true
endif

# Allow a device to opt-out hardset of PRODUCT_SOONG_NAMESPACES
QCOM_SOONG_NAMESPACE ?= hardware/qcom-caf/$(QCOM_HARDWARE_VARIANT)
PRODUCT_SOONG_NAMESPACES += $(QCOM_SOONG_NAMESPACE)

# Add display-commonsys-intf to PRODUCT_SOONG_NAMESPACES for QSSI supported platforms
ifneq ($(filter $(QSSI_SUPPORTED_PLATFORMS),$(PRODUCT_BOARD_PLATFORM)),)
    PRODUCT_SOONG_NAMESPACES += vendor/qcom/opensource/commonsys-intf/display
endif

# Add data-ipa-cfg-mgr to PRODUCT_SOONG_NAMESPACES if needed
ifneq ($(USE_DEVICE_SPECIFIC_DATA_IPA_CFG_MGR),true)
    PRODUCT_SOONG_NAMESPACES += vendor/qcom/opensource/data-ipa-cfg-mgr
endif

# Add dataservices to PRODUCT_SOONG_NAMESPACES if needed
ifneq ($(USE_DEVICE_SPECIFIC_DATASERVICES),true)
    PRODUCT_SOONG_NAMESPACES += vendor/qcom/opensource/dataservices
endif

# Add nxp opensource to PRODUCT_SOONG_NAMESPACES if needed
ifeq ($(USE_NQ_NFC),true)
    PRODUCT_SOONG_NAMESPACES += vendor/nxp/opensource
endif

include vendor/statix/build/core/qcom_target.mk
