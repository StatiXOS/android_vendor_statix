#
# Copyright (C) 2022 StatiX
#
# SPDX-License-Identifer: Apache-2.0
#

$(warning Cleaning kernel objects.)
$(call add-clean-step, rm -rf $(OUT_DIR)/kernel boot.img)
