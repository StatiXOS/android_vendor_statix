#
# Copyright (C) 2022 StatiX
#
# SPDX-License-Identifer: Apache-2.0
#

$(warning Cleaning kernel and vendor_dlkm objects.)
$(call add-clean-step, rm -rf $(PRODUCT_OUT)/boot.img)
$(call add-clean-step, rm -rf $(PRODUCT_OUT)/kernel)
$(call add-clean-step, rm -rf $(PRODUCT_OUT)/vendor_dlkm)
