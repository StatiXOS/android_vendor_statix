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
# AOSP OTA update package

AOSP_TARGET_PACKAGE := $(PRODUCT_OUT)/$(AOSP_VERSION).zip

.PHONY: bacon
bacon: $(INTERNAL_OTA_PACKAGE_TARGET)
	$(hide) ln -f $(INTERNAL_OTA_PACKAGE_TARGET) $(AOSP_TARGET_PACKAGE)
	$(hide) $(MD5SUM) $(AOSP_TARGET_PACKAGE) | sed "s|$(PRODUCT_OUT)/||" > $(AOSP_TARGET_PACKAGE).md5sum
	@echo "Package Complete: $(AOSP_TARGET_PACKAGE)" >&2

