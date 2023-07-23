# Copyright (C) 2017 Unlegacy-Android
# Copyright (C) 2017 The LineageOS Project
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
# StatiX fastboot update package

STATIX_TARGET_UPDATEPACKAGE := $(PRODUCT_OUT)/$(STATIX_VERSION)-img.zip

.PHONY: updatepackage
updatepackage: $(INTERNAL_UPDATE_PACKAGE_TARGET)
	$(hide) ln -f $(INTERNAL_UPDATE_PACKAGE_TARGET) $(STATIX_TARGET_UPDATEPACKAGE)
#	$(hide) $(MD5SUM) $(STATIX_TARGET_UPDATEPACKAGE) | sed "s|$(PRODUCT_OUT)/||" > $(STATIX_TARGET_UPDATEPACKAGE).md5sum
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
	@echo "Package Complete: $(STATIX_TARGET_UPDATEPACKAGE)" >&2
	@echo "Package size: `du -h $(STATIX_TARGET_UPDATEPACKAGE) | cut -f 1`"
