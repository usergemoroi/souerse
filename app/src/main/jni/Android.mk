LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := sound_helper

LOCAL_SRC_FILES := \
    src/main.cpp \
    src/globals.cpp \
    src/memory/memory.cpp \
    src/network/socket_server.cpp \
    src/esp/esp.cpp \
    src/esp/esp_renderer.cpp \
    src/sdk/player/player.cpp \
    src/sdk/game/game.cpp \
    src/render/native_renderer.cpp \
    src/hooks/gl_hook.cpp

LOCAL_C_INCLUDES := src

LOCAL_LDLIBS := -llog -lGLESv2 -lEGL -ldl

LOCAL_CFLAGS += \
    -O2 \
    -fvisibility=hidden \
    -ffunction-sections \
    -fdata-sections

LOCAL_LDFLAGS += \
    -Wl,--gc-sections \
    -Wl,--exclude-libs,ALL

include $(BUILD_SHARED_LIBRARY)
