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
	@echo " "
	@echo " "
	@echo "                                                              :             "
	@echo "          .                                                  t#,           ."
	@echo "         ;W                               t                 ;##W.         ;W"
	@echo "        f#E GEEEEEEEL         .. GEEEEEEELEj               :#L:WE        f#E"
	@echo "      .E#f  ,;;L#K;;.        ;W, ,;;L#K;;.E#, :KW,      L .KG  ,#D     .E#f "
	@echo "     iWW;      t#E          j##,    t#E   E#t  ,#W:   ,KG EE    ;#f   iWW;  "
	@echo "    L##Lffi    t#E         G###,    t#E   E#t   ;#W. jWi f#.     t#i L##Lffi"
	@echo "   tLLG##L     t#E       :E####,    t#E   E#t    i#KED.  :#G     GK tLLG##L "
	@echo "     ,W#i      t#E      ;W#DG##,    t#E   E#t     L#W.    ;#L   LW.   ,W#i  "
	@echo "    j#E.       t#E     j###DW##,    t#E   E#t   .GKj#K.    t#f f#:   j#E.   "
	@echo "  .D#j         t#E    G##,,,,##,    t#E   E#t  iWf  i#K.    f#D#;  .D#j     "
	@echo " ,WK,          t#E  :K#K:   L##,    t#E   E#t LK:    t#E     G#t  ,WK,      "
	@echo " EG.            fE ;##D.    L##,     fE   E#t i       tDj     t   EG.       "
	@echo " ,.              : ###,      L#,      #:   ,;.                     ,        "
	@echo " "
	@echo " "
	@echo "Package Complete: $(STATIX_TARGET_PACKAGE)" >&2
	@echo "Package size: `du -h $(STATIX_TARGET_PACKAGE) | cut -f 1`"

endif
