LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := InstallProcess
LOCAL_SRC_FILES := InstallProcess.cpp

#生成共享链接库
#include $(BUILD_SHARED_LIBRARY)
#生成可执行文件
include $(BUILD_EXECUTABLE)

