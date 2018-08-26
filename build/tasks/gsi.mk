# Copyright (C) 2018 StatiXOS
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
# StatiX Generic System Image

STATIX_TARGET_SYSIMG := $(PRODUCT_OUT)/$(STATIX_VERSION)-system.img

.PHONY: gsi
gsi: $(INSTALLED_SYSTEMIMAGE)
	$(hide) ln -f $(INSTALLED_SYSTEMIMAGE) $(STATIX_TARGET_SYSIMG)
	$(hide) $(MD5SUM) $(STATIX_TARGET_SYSIMG) | sed "s|$(PRODUCT_OUT)/||" > $(STATIX_TARGET_SYSIMG).md5sum
	@echo " "
	@echo " "
	@echo "                                                                                                            "
	@echo "                                                                                                            "
	@echo "                          tttt                                    tttt            iiii                      "
	@echo "                       ttt:::t                                 ttt:::t           i::::i                     "
	@echo "                       t:::::t                                 t:::::t            iiii                      "
	@echo "                       t:::::t                                 t:::::t                                      "
	@echo "    ssssssssss   ttttttt:::::ttttttt      aaaaaaaaaaaaa  ttttttt:::::ttttttt    iiiiiii xxxxxxx      xxxxxxx"
	@echo "  ss::::::::::s  t:::::::::::::::::t      a::::::::::::a t:::::::::::::::::t    i:::::i  x:::::x    x:::::x "
	@echo "ss:::::::::::::s t:::::::::::::::::t      aaaaaaaaa:::::at:::::::::::::::::t     i::::i   x:::::x  x:::::x  "
	@echo "s::::::ssss:::::stttttt:::::::tttttt               a::::atttttt:::::::tttttt     i::::i    x:::::xx:::::x   "
	@echo " s:::::s  ssssss       t:::::t              aaaaaaa:::::a      t:::::t           i::::i     x::::::::::x    "
	@echo "   s::::::s            t:::::t            aa::::::::::::a      t:::::t           i::::i      x::::::::x     "
	@echo "      s::::::s         t:::::t           a::::aaaa::::::a      t:::::t           i::::i      x::::::::x     "
	@echo "ssssss   s:::::s       t:::::t    tttttta::::a    a:::::a      t:::::t    tttttt i::::i     x::::::::::x    "
	@echo "s:::::ssss::::::s      t::::::tttt:::::ta::::a    a:::::a      t::::::tttt:::::ti::::::i   x:::::xx:::::x   "
	@echo "s::::::::::::::s       tt::::::::::::::ta:::::aaaa::::::a      tt::::::::::::::ti::::::i  x:::::x  x:::::x  "
	@echo " s:::::::::::ss          tt:::::::::::tt a::::::::::aa:::a       tt:::::::::::tti::::::i x:::::x    x:::::x "
	@echo "  sssssssssss              ttttttttttt    aaaaaaaaaa  aaaa         ttttttttttt  iiiiiiiixxxxxxx      xxxxxxx"
	@echo " " 
	@echo " "
	@echo "System Image Complete: $(STATIX_TARGET_SYSIMG)" >&2
	@echo "System Image size: `du -h $(STATIX_TARGET_SYSIMG) | cut -f 1`"

