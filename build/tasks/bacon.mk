# Copyright (C) 2017 Unlegacy-Android
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
# STATIX OTA update package

STATIX_TARGET_PACKAGE := $(PRODUCT_OUT)/$(STATIX_VERSION).zip

.PHONY: bacon
bacon: $(INTERNAL_OTA_PACKAGE_TARGET)
	$(hide) ln -f $(INTERNAL_OTA_PACKAGE_TARGET) $(STATIX_TARGET_PACKAGE)
	$(hide) $(MD5SUM) $(STATIX_TARGET_PACKAGE) | sed "s|$(PRODUCT_OUT)/||" > $(STATIX_TARGET_PACKAGE).md5sum
	@echo " "
	@echo " ________  _________  ________  _________  ___     ___    ___ "
	@echo "|\   ____\|\___   ___\\   __  \|\___   ___\\  \   |\  \  /  /| "
	@echo "\ \  \___|\|___ \  \_\ \  \|\  \|___ \  \_\ \  \  \ \  \/  / / "
	@echo " \ \_____  \   \ \  \ \ \   __  \   \ \  \ \ \  \  \ \    / / "
	@echo "  \|____|\  \   \ \  \ \ \  \ \  \   \ \  \ \ \  \  /     \/  "
	@echo "    ____\_\  \   \ \__\ \ \__\ \__\   \ \__\ \ \__\/  /\   \  "
	@echo "   |\_________\   \|__|  \|__|\|__|    \|__|  \|__/__/ /\ __\ "
	@echo "   \|_________|                                   |__|/ \|__|  "                                            
	@echo " "                                                      
	@echo "Package Complete: $(STATIX_TARGET_PACKAGE)" >&2
	@echo "Package size: `du -h $(STATIX_TARGET_PACKAGE) | cut -f 1`"

