include vendor/statix/config/BoardConfigKernel.mk

ifeq ($(BOARD_USES_QCOM_HARDWARE),true)
include vendor/statix/build/core/BoardConfigQcom.mk
endif

include vendor/statix/config/BoardConfigSoong.mk
