# Bring in Qualcomm helper macros
include vendor/statix/build/core/utils.mk

UM_4_4_FAMILY := msm8998 sdm660
UM_4_9_FAMILY := msm8917 msm8937 msm8952 msm8953 msm8996 sdm845
UM_4_14_FAMILY := $(MSMNILE) $(MSMSTEPPE) $(TRINKET) $(ATOLL)
UM_4_19_FAMILY := $(KONA) $(LITO) $(BENGAL)
UM_5_4_FAMILY := $(LAHAINA) $(HOLI)
UM_5_10_FAMILY := $(TARO) $(PARROT)

UM_PLATFORMS := $(UM_4_4_FAMILY) $(UM_4_9_FAMILY) $(UM_4_14_FAMILY) $(UM_4_19_FAMILY) $(UM_5_4_FAMILY) $(UM_5_10_FAMILY)
QSSI_SUPPORTED_PLATFORMS := $(UM_4_4_FAMILY) $(UM_4_9_FAMILY) $(UM_4_14_FAMILY) $(UM_4_19_FAMILY) $(UM_5_4_FAMILY) $(UM_5_10_FAMILY)
UM_NO_GKI_PLATFORMS := $(UM_4_4_FAMILY) $(UM_4_9_FAMILY) $(UM_4_14_FAMILY) $(UM_4_19_FAMILY) $(UM_5_4_FAMILY)

BOARD_USES_ADRENO := true

# Add qtidisplay to soong config namespaces
SOONG_CONFIG_NAMESPACES += qtidisplay

# Add supported variables to qtidisplay config
SOONG_CONFIG_qtidisplay += \
    drmpp \
    headless \
    llvmsa \
    gralloc4 \
    displayconfig_enabled \
    default \
    gralloc_handle_has_reserved_size \
    gralloc_handle_has_custom_content_md_reserved_size \
    var1 \
    var2 \
    var3

# Set default values for qtidisplay config
SOONG_CONFIG_qtidisplay_drmpp ?= false
SOONG_CONFIG_qtidisplay_headless ?= false
SOONG_CONFIG_qtidisplay_llvmsa ?= false
SOONG_CONFIG_qtidisplay_gralloc4 ?= false
SOONG_CONFIG_qtidisplay_displayconfig_enabled ?= false
SOONG_CONFIG_qtidisplay_default ?= true
SOONG_CONFIG_qtidisplay_gralloc_handle_has_reserved_size ?= false
SOONG_CONFIG_qtidisplay_gralloc_handle_has_custom_content_md_reserved_size ?= false
SOONG_CONFIG_qtidisplay_var1 ?= false
SOONG_CONFIG_qtidisplay_var2 ?= false
SOONG_CONFIG_qtidisplay_var3 ?= false

# UM platforms no longer need this set on O+
ifneq ($(call is-board-platform-in-list, $(UM_PLATFORMS)),true)
    TARGET_USES_QCOM_BSP := true
endif

# Tell HALs that we're compiling an AOSP build with an in-line kernel
TARGET_COMPILE_WITH_MSM_KERNEL := true

# Allow building audio encoders
TARGET_USES_QCOM_MM_AUDIO := true

# Enable color metadata for all UM targets
ifeq ($(call is-board-platform-in-list, $(UM_PLATFORMS)),true)
    TARGET_USES_COLOR_METADATA := true
endif

# Enable DRM PP driver on UM platforms that support it
ifeq ($(call is-board-platform-in-list, $(UM_4_9_FAMILY) $(UM_4_14_FAMILY) $(UM_4_19_FAMILY) $(UM_5_4_FAMILY) $(UM_5_10_FAMILY)),true)
    SOONG_CONFIG_qtidisplay_drmpp := true
    TARGET_USES_DRM_PP := true
endif

# Enable displayconfig
ifeq ($(call is-board-platform-in-list, $(UM_5_10_FAMILY)),true)
    SOONG_CONFIG_qtidisplay_displayconfig_enabled := true
endif

# Enable gralloc handle support on 5.10
ifeq ($(call is-board-platform-in-list, $(UM_5_10_FAMILY)),true)
    SOONG_CONFIG_qtidisplay_gralloc_handle_has_reserved_size := true
endif

# Enable Gralloc4 on UM platforms that support it
ifneq ($(filter $(UM_5_4_FAMILY) $(UM_5_10_FAMILY),$(PRODUCT_BOARD_PLATFORM)),)
    SOONG_CONFIG_qtidisplay_gralloc4 := true
endif

# List of targets that use master side content protection
MASTER_SIDE_CP_TARGET_LIST := msm8996 $(UM_4_4_FAMILY) $(UM_4_9_FAMILY) $(UM_4_14_FAMILY) $(UM_4_19_FAMILY) $(UM_5_4_FAMILY) $(UM_5_10_FAMILY)

# Every qcom platform is considered a vidc target
MSM_VIDC_TARGET_LIST := $(PRODUCT_BOARD_PLATFORM)

ifeq ($(call is-board-platform-in-list, $(UM_4_4_FAMILY)),true)
    QCOM_HARDWARE_VARIANT := msm8998
else ifeq ($(call is-board-platform-in-list, $(UM_4_9_FAMILY)),true)
    QCOM_HARDWARE_VARIANT := sdm845
else ifeq ($(call is-board-platform-in-list, $(UM_4_14_FAMILY)),true)
    QCOM_HARDWARE_VARIANT := sm8150
else ifeq ($(call is-board-platform-in-list, $(UM_4_19_FAMILY)),true)
    QCOM_HARDWARE_VARIANT := sm8250
else ifeq ($(call is-board-platform-in-list, $(UM_5_4_FAMILY)),true)
    QCOM_HARDWARE_VARIANT := sm8350
else ifeq ($(call is-board-platform-in-list, $(UM_5_10_FAMILY)),true)
    QCOM_HARDWARE_VARIANT := sm8450
else
    QCOM_HARDWARE_VARIANT := $(PRODUCT_BOARD_PLATFORM)
endif

# Allow a device to manually override which HALs it wants to use
ifneq ($(OVERRIDE_QCOM_HARDWARE_VARIANT),)
QCOM_HARDWARE_VARIANT := $(OVERRIDE_QCOM_HARDWARE_VARIANT)
endif

ifeq ($(call is-board-platform-in-list, $(UM_4_4_FAMILY)),true)
    TARGET_KERNEL_VERSION := 4.4
else ifeq ($(call is-board-platform-in-list, $(UM_4_9_FAMILY)),true)
    TARGET_KERNEL_VERSION := 4.9
else ifeq ($(call is-board-platform-in-list, $(UM_4_14_FAMILY)),true)
    TARGET_KERNEL_VERSION := 4.14
else ifeq ($(call is-board-platform-in-list, $(UM_4_19_FAMILY)),true)
    TARGET_KERNEL_VERSION := 4.19
else ifeq ($(call is-board-platform-in-list, $(UM_5_4_FAMILY)),true)
    TARGET_KERNEL_VERSION := 5.4
else ifeq ($(call is-board-platform-in-list, $(UM_5_10_FAMILY)),true)
    TARGET_KERNEL_VERSION := 5.10
endif

# Required for frameworks/native
ifeq ($(call is-board-platform-in-list, $(UM_PLATFORMS)),true)
    TARGET_USES_QCOM_UM_FAMILY := true
endif

# Allow a device to opt-out hardset of PRODUCT_SOONG_NAMESPACES
QCOM_SOONG_NAMESPACE ?= hardware/qcom-caf/$(QCOM_HARDWARE_VARIANT)
PRODUCT_SOONG_NAMESPACES += $(QCOM_SOONG_NAMESPACE)

# Define kernel headers location
PRODUCT_VENDOR_KERNEL_HEADERS += hardware/qcom-caf/$(QCOM_HARDWARE_VARIANT)/kernel-headers

# Add display-commonsys-intf to PRODUCT_SOONG_NAMESPACES for QSSI supported platforms
ifeq ($(call is-board-platform-in-list, $(QSSI_SUPPORTED_PLATFORMS)),true)
PRODUCT_SOONG_NAMESPACES += \
    vendor/qcom/opensource/commonsys-intf/display
endif

# Add display-commonsys and display for non-GKI platforms
ifneq ($(filter $(UM_NO_GKI_PLATFORMS),$(PRODUCT_BOARD_PLATFORM)),)
PRODUCT_SOONG_NAMESPACES += \
    vendor/qcom/opensource/commonsys/display
endif

# Add data-ipa-cfg-mgr to PRODUCT_SOONG_NAMESPACES if needed
ifneq ($(USE_DEVICE_SPECIFIC_DATA_IPA_CFG_MGR),true)
ifneq ($(filter $(UM_NO_GKI_PLATFORMS),$(PRODUCT_BOARD_PLATFORM)),)
    PRODUCT_SOONG_NAMESPACES += vendor/qcom/opensource/data-ipa-cfg-mgr-nogki
else
    PRODUCT_SOONG_NAMESPACES += vendor/qcom/opensource/data-ipa-cfg-mgr
endif
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
