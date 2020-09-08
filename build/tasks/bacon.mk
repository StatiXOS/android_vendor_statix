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
# StatiX OTA update package

STATIX_TARGET_PACKAGE := $(PRODUCT_OUT)/$(STATIX_VERSION).zip

.PHONY: bacon
bacon: $(INTERNAL_OTA_PACKAGE_TARGET)
	$(hide) ln -f $(INTERNAL_OTA_PACKAGE_TARGET) $(STATIX_TARGET_PACKAGE)
#	$(hide) $(MD5SUM) $(STATIX_TARGET_PACKAGE) | sed "s|$(PRODUCT_OUT)/||" > $(STATIX_TARGET_PACKAGE).md5sum
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
