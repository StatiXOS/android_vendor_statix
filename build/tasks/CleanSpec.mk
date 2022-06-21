# Copyright (C) 2022 StatiX
#
# SPDX-License-Identifer: Apache-2.0
#

$(warning "Cleaning DTBO objects.")
$(call add-clean-step, rm -rf $(TARGET_OUT_INTERMEDIATES)/dtbs)
