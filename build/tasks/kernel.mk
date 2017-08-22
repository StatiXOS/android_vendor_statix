#Android makefile to build kernel as a part of Android Build

TARGET_AUTO_KDIR := $(shell echo $(TARGET_DEVICE_DIR) | sed -e 's/^device/kernel/g')

## Externally influenced variables
# kernel location - optional, defaults to kernel/<vendor>/<device>
TARGET_KERNEL_SOURCE ?= $(TARGET_AUTO_KDIR)
KERNEL_SRC := $(TARGET_KERNEL_SOURCE)
# kernel configuration - mandatory
KERNEL_DEFCONFIG := $(TARGET_KERNEL_CONFIG)

## Internal variables
KERNEL_OUT := $(TARGET_OUT_INTERMEDIATES)/KERNEL_OBJ
KERNEL_CONFIG := $(KERNEL_OUT)/.config

ifeq ($(BOARD_USES_UBOOT),true)
	TARGET_PREBUILT_INT_KERNEL := $(KERNEL_OUT)/arch/$(TARGET_ARCH)/boot/uImage
	TARGET_PREBUILT_INT_KERNEL_TYPE := uImage
else
	TARGET_PREBUILT_INT_KERNEL := $(KERNEL_OUT)/arch/$(TARGET_ARCH)/boot/zImage
	TARGET_PREBUILT_INT_KERNEL_TYPE := zImage
endif

# by default dont build even if source is present
ifeq (,$(filter true 1,$(BUILD_KERNEL)))
    KERNEL_SRC:=
endif
# if there is no prebuilt kernel we must build from source
ifeq ($(TARGET_PREBUILT_KERNEL),)
    KERNEL_SRC := $(TARGET_KERNEL_SOURCE)
endif

ifeq "$(wildcard $(KERNEL_SRC) )" ""
    ifneq (,$(filter true 1,$(BUILD_KERNEL)))
        $(warning ************************************************)
        $(warning *        ERROR: Can't find kernel source       *)
        $(warning *                                              *)
        $(warning * You asked me to build the kernel but did not *)
        $(warning *              provide the source!             *)
        $(warning *                                              *)
        $(warning * Please run find_deps to sync the kernel repo *)
        $(warning ************************************************)
        $(error "NO SOURCE")
    endif
    ifneq ($(TARGET_PREBUILT_KERNEL),)
        $(warning ************************************************)
        $(warning *         Using prebuilt kernel binary         *)
        $(warning ************************************************)
        FULL_KERNEL_BUILD := false
        KERNEL_BIN := $(TARGET_PREBUILT_KERNEL)
    else
        $(warning *************************************************)
        $(warning *        ERROR: Can't find kernel source        *)
        $(warning *                                               *)
        $(warning * If using a prebuilt kernel you must define    *)
        $(warning * TARGET_PREBUILT_KERNEL in the BoardConfig.mk  *)
        $(warning *************************************************)
        $(error "NO KERNEL")
    endif
else
    ifeq ($(TARGET_KERNEL_CONFIG),)
        $(warning *************************************************)
        $(warning *     ERROR: no kernel configuration found      *)
        $(warning *              You need to define               *)
        $(warning *   TARGET_KERNEL_CONFIG in the BoardConfig.mk  *)
        $(warning *************************************************)
        $(error "NO KERNEL CONFIG")
    else
        FULL_KERNEL_BUILD := true
        ifeq ($(TARGET_USES_UNCOMPRESSED_KERNEL),true)
        $(info Using uncompressed kernel)
            KERNEL_BIN := $(KERNEL_OUT)/piggy
        else
            KERNEL_BIN := $(TARGET_PREBUILT_INT_KERNEL)
        endif
    endif
endif

ifeq ($(FULL_KERNEL_BUILD),true)

KERNEL_HEADERS_INSTALL := $(KERNEL_OUT)/usr
KERNEL_MODULES_INSTALL := system
KERNEL_MODULES_OUT := $(TARGET_OUT)/lib/modules

define mv-modules
    mdpath=`find $(KERNEL_MODULES_OUT) -type f -name modules.order`;\
    if [ "$$mdpath" != "" ];then\
        mpath=`dirname $$mdpath`;\
        ko=`find $$mpath/kernel -type f -name *.ko`;\
        for i in $$ko; do mv $$i $(KERNEL_MODULES_OUT)/; done;\
    fi
endef

define clean-module-folder
    mdpath=`find $(KERNEL_MODULES_OUT) -type f -name modules.order`;\
    if [ "$$mdpath" != "" ];then\
        mpath=`dirname $$mdpath`; rm -rf $$mpath;\
    fi
endef

ifneq ($(KERNEL_JOBS),)
    JOBS := -j$(KERNEL_JOBS)
endif

ifeq ($(TARGET_ARCH),arm)
    ifneq ($(USE_CCACHE),)
      ccache := $(ANDROID_BUILD_TOP)/prebuilt/$(HOST_PREBUILT_TAG)/ccache/ccache
      # Check that the executable is here.
      ccache := $(strip $(wildcard $(ccache)))
    endif
    ARM_CROSS_COMPILE:=CROSS_COMPILE="$(ccache) $(ARM_EABI_TOOLCHAIN)/arm-eabi-"
    ccache = 
endif

ifeq ($(TARGET_KERNEL_MODULES),)
    TARGET_KERNEL_MODULES := no-external-modules
endif

$(KERNEL_OUT):
	mkdir -p $(KERNEL_OUT)
	mkdir -p $(KERNEL_MODULES_OUT)

$(KERNEL_CONFIG): $(KERNEL_OUT)
	$(MAKE) -C $(KERNEL_SRC) O=$(KERNEL_OUT) ARCH=$(TARGET_ARCH) $(ARM_CROSS_COMPILE) $(KERNEL_DEFCONFIG)

$(KERNEL_OUT)/piggy : $(TARGET_PREBUILT_INT_KERNEL)
	$(hide) gunzip -c $(KERNEL_OUT)/arch/$(TARGET_ARCH)/boot/compressed/piggy.gzip > $(KERNEL_OUT)/piggy

TARGET_KERNEL_BINARIES: $(KERNEL_OUT) $(KERNEL_CONFIG) $(KERNEL_HEADERS_INSTALL)
	$(MAKE) $(JOBS) -C $(KERNEL_SRC) O=$(KERNEL_OUT) ARCH=$(TARGET_ARCH) $(ARM_CROSS_COMPILE) $(TARGET_PREBUILT_INT_KERNEL_TYPE)
	$(MAKE) $(JOBS) -C $(KERNEL_SRC) O=$(KERNEL_OUT) ARCH=$(TARGET_ARCH) $(ARM_CROSS_COMPILE) modules
	$(MAKE) $(JOBS) -C $(KERNEL_SRC) O=$(KERNEL_OUT) INSTALL_MOD_PATH=../../$(KERNEL_MODULES_INSTALL) ARCH=$(TARGET_ARCH) $(ARM_CROSS_COMPILE) modules_install
	$(mv-modules)
	$(clean-module-folder)

$(TARGET_KERNEL_MODULES): TARGET_KERNEL_BINARIES

$(TARGET_PREBUILT_INT_KERNEL): $(TARGET_KERNEL_MODULES)
	$(mv-modules)
	$(clean-module-folder)

$(KERNEL_HEADERS_INSTALL): $(KERNEL_OUT) $(KERNEL_CONFIG)
	$(MAKE) -C $(KERNEL_SRC) O=$(KERNEL_OUT) ARCH=$(TARGET_ARCH) $(ARM_CROSS_COMPILE) headers_install

endif # FULL_KERNEL_BUILD

## Install it

file := $(INSTALLED_KERNEL_TARGET)
ALL_PREBUILT += $(file)
$(file) : $(KERNEL_BIN) | $(ACP)
	$(transform-prebuilt-to-target)

ALL_PREBUILT += $(INSTALLED_KERNEL_TARGET)

