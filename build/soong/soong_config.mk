add_json_str_omitempty = $(if $(strip $(2)),$(call add_json_str, $(1), $(2)))

$(eval _contents := $$(_contents)"Statix": {$$(newline))

$(call add_json_bool,	Has_legacy_camera_hal1,						$(filter true,$(if $(filter true,$(TARGET_HAS_LEGACY_CAMERA_HAL1)),true,false)))
$(call add_json_bool,	Needs_legacy_camera_hal1_dyn_native_handle,	$(filter true,$(if $(filter true,$(TARGET_NEEDS_LEGACY_CAMERA_HAL1_DYN_NATIVE_HANDLE)),true,false)))
$(call add_json_bool,	Uses_media_extensions,						$(filter true,$(if $(filter true,$(TARGET_USES_MEDIA_EXTENSIONS)),true,false)))
$(call add_json_bool,	Uses_generic_camera_parameter_library		$(filter true,$(if $(TARGET_SPECIFIC_CAMERA_PARAMETER_LIBRARY),false,true)))
$(call add_json_bool,	Needs_text_relocations,						$(filter true,$(if $(filter true,$(TARGET_NEEDS_PLATFORM_TEXT_RELOCATIONS)),true,false)))
$(call add_json_bool,	Mtk_hardware,								$(filter true,$(if $(filter true,$(BOARD_USES_MTK_HARDWARE)),true,false)))
$(call add_json_bool,	BoardUsesQTIHardware,						$(filter true,$(if $(BOARD_USES_QTI_HARDWARE),true,false)))
$(call add_json_bool,	Cant_reallocate_omx_buffers,				$(filter true,$(if $(filter omap4,$(TARGET_BOARD_PLATFORM)),true,false)))
$(call add_json_bool,	Qcom_bsp_legacy,							$(filter true,$(if $(TARGET_USES_QCOM_BSP_LEGACY),true,false)))
$(call add_json_bool,	Qti_flac_decoder,							$(filter true,$(if $(strip $(AUDIO_FEATURE_ENABLED_EXTN_FLAC_DECODER)),true,false)))
$(call add_json_bool,	Target_uses_qsml,							$(filter true,$(if $(strip $(TARGET_USES_QSML)),true,false)))
$(call add_json_bool,	Target_uses_eigen,							$(filter true,$(if $(strip $(TARGET_USES_QSML)),false,true)))

$(call add_json_str_omitempty,	Additional_gralloc_10_usage_bits, 	$(TARGET_ADDITIONAL_GRALLOC_10_USAGE_BITS))

$(call add_json_str,	Specific_camera_parameter_library,			$(TARGET_SPECIFIC_CAMERA_PARAMETER_LIBRARY))
$(call add_json_str,	Libart_img_base,							$(LIBART_IMG_BASE))
$(call add_json_str,	QTIAudioPath,								$(call project-path-for,qcom-audio))
$(call add_json_str,	QTIDisplayPath,								$(call project-path-for,qcom-display))
$(call add_json_str,	QTIMediaPath,								$(call project-path-for,qcom-media))
$(call add_json_str,	Target_shim_libs,							$(subst $(space),:,$(TARGET_LD_SHIM_LIBS)))

$(eval _contents := $(subst $$(comma)$$(newline)__SV_END,$(newline),$$(_contents)__SV_END},$$(newline)))
