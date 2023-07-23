# Copyright (C) 2021 StatiXOS
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# -----------------------------------------------------------------
# StatiX DSU package

GSI_TARGETS := statix_arm64 statix_arm statix_x86 statix_x86_64

ifneq ($(filter $(TARGET_PRODUCT),$(GSI_TARGETS)),)

STATIX_TARGET_PACKAGE := $(PRODUCT_OUT)/$(STATIX_VERSION)-dsu.zip

.PHONY: dsu_package
dsu_package: $(INSTALLED_SYSTEMIMAGE_TARGET) $(INSTALLED_VBMETAIMAGE_TARGET) $(SOONG_ZIP)
	$(hide) cd $(PRODUCT_OUT) && $(SOONG_ZIP) -o $(STATIX_TARGET_PACKAGE) -f system.img -f vbmeta.img
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
	@echo ""
	@echo "Package Complete: $(STATIX_TARGET_PACKAGE)" >&2
	@echo "Package size: `du -h $(STATIX_TARGET_PACKAGE) | cut -f 1`"

endif
