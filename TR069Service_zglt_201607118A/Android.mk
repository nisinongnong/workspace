
LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_STATIC_JAVA_LIBRARIES := libdownload

LOCAL_PACKAGE_NAME := tr069Service_std

ALL_DEFAULT_INSTALLED_MODULES += $(LOCAL_PACKAGE_NAME)

LOCAL_CERTIFICATE := platform

LOCAL_DEX_PREOPT := false
LOCAL_PROGUARD_ENABLED := disabled

include $(BUILD_PACKAGE)
include $(CLEAR_VARS)
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := libdownload:libs/JzDownloadUtil.jar
include $(BUILD_MULTI_PREBUILT)
ALL_DEFAULT_INSTALLED_MODULES += $(LOCAL_PACKAGE_NAME)

include $(call all-makefiles-under,$(LOCAL_PATH))