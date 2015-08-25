LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_STATIC_JAVA_LIBRARIES := \
    android-support-v13 \
    android-support-v4 
LOCAL_RESOURCE_DIR = \
    $(LOCAL_PATH)/res  

#LOCAL_JAVA_LIBRARIES += mediatek-framework

#LOCAL_REQUIRED_MODULES := libvariablespeed 

LOCAL_PACKAGE_NAME := MyCompass 
#LOCAL_CERTIFICATE := shared 

LOCAL_PROGUARD_FLAG_FILES := proguard.flags

LOCAL_ASSET_DIR := $(LOCAL_PATH)/assets

include $(BUILD_PACKAGE)

#Use the folloing include to make our test apk.
include $(call all-makefiles-under,$(LOCAL_PATH))
