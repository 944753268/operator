LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := InstallProcess
LOCAL_SRC_FILES := InstallProcess.cpp

#���ɹ������ӿ�
#include $(BUILD_SHARED_LIBRARY)
#���ɿ�ִ���ļ�
include $(BUILD_EXECUTABLE)

