#
# Copyright (C) 2023 StatiXOS
# SPDX-License-Identifier: Apache-2.0
#

#
# Statix Release Package (Builds OTA and Fastboot packages)
#

# Package names
STATIX_TARGET_UPDATEPACKAGE := $(PRODUCT_OUT)/$(STATIX_VERSION)-img.zip
STATIX_TARGET_PACKAGE := $(PRODUCT_OUT)/$(STATIX_VERSION).zip

.PHONY: stxrelease
stxrelease: $(INTERNAL_UPDATE_PACKAGE_TARGET) $(INTERNAL_OTA_PACKAGE_TARGET)
	$(hide) ln -f $(INTERNAL_UPDATE_PACKAGE_TARGET) $(STATIX_TARGET_UPDATEPACKAGE)
	$(hide) ln -f $(INTERNAL_OTA_PACKAGE_TARGET) $(STATIX_TARGET_PACKAGE)
	@echo ""
	@echo ""
	@echo "     _______..___________.    ___   .___________. __  ___   ___   ______        _______."
	@echo "    /       ||           |   /   \  |           ||  | \  \ /  /  /  __  \      /       |"
	@echo "   |   (----``---|  |----`  /  ^  \ `---|  |----`|  |  \  V  /  |  |  |  |    |   (----`"
	@echo "    \   \        |  |      /  /_\  \    |  |     |  |   >   <   |  |  |  |     \   \    "
	@echo ".----)   |       |  |     /  _____  \   |  |     |  |  /  .  \  |  `--'  | .----)   |   "
	@echo "|_______/        |__|    /__/     \__\  |__|     |__| /__/ \__\  \______/  |_______/    "
	@echo "                                                                                        "
	@echo ""
	@echo "============================================================================"
	@echo "Fastboot: $(STATIX_TARGET_UPDATEPACKAGE)" >&2
	@echo "Size: `du -h $(STATIX_TARGET_UPDATEPACKAGE) | cut -f 1`"
	@echo "============================================================================"
	@echo "OTA: $(STATIX_TARGET_PACKAGE)" >&2
	@echo "Size: `du -h $(STATIX_TARGET_PACKAGE) | cut -f 1`"
	@echo "============================================================================"
