LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_PACKAGE_NAME := IconShapeTaperedRectOverlay
LOCAL_PRODUCT_MODULE := true
LOCAL_RRO_THEME := IconShapeTaperedRect
LOCAL_SDK_VERSION := current
include $(BUILD_RRO_PACKAGE)
