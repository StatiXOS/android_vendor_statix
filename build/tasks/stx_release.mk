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
	@echo "============================================================================"
	@echo "Fastboot: $(STATIX_TARGET_UPDATEPACKAGE)" >&2
	@echo "Size: `du -h $(STATIX_TARGET_UPDATEPACKAGE) | cut -f 1`"
	@echo "============================================================================"
	@echo "OTA: $(STATIX_TARGET_PACKAGE)" >&2
	@echo "Size: `du -h $(STATIX_TARGET_PACKAGE) | cut -f 1`"
	@echo "============================================================================"
