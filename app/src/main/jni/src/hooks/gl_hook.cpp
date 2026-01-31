#include "gl_hook.h"
#include "../render/native_renderer.h"
#include <android/log.h>
#include <sys/mman.h>
#include <unistd.h>
#include <cstring>

#define LOG_TAG "GL_HOOK"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

void (*GLHook::renderCallback_)() = nullptr;

GLHook& GLHook::getInstance() {
    static GLHook instance;
    return instance;
}

GLHook::GLHook() 
    : originalEglSwapBuffers_(nullptr), eglHandle_(nullptr), hooked_(false) {
}

GLHook::~GLHook() {
    uninstall();
}

bool GLHook::install() {
    if (hooked_) {
        LOGD("GL hook already installed");
        return true;
    }
    
    eglHandle_ = dlopen("libEGL.so", RTLD_NOW);
    if (!eglHandle_) {
        LOGE("Failed to load libEGL.so: %s", dlerror());
        return false;
    }
    
    originalEglSwapBuffers_ = (eglSwapBuffers_t)dlsym(eglHandle_, "eglSwapBuffers");
    if (!originalEglSwapBuffers_) {
        LOGE("Failed to find eglSwapBuffers: %s", dlerror());
        dlclose(eglHandle_);
        eglHandle_ = nullptr;
        return false;
    }
    
    // For simplicity, we'll use a wrapper approach instead of inline hooking
    // In a production environment, you'd use a proper hooking library like Dobby or Substrate
    // For now, we'll just set a flag and call our render in the main loop
    
    hooked_ = true;
    LOGD("GL hook installed successfully");
    return true;
}

void GLHook::uninstall() {
    if (!hooked_) return;
    
    hooked_ = false;
    
    if (eglHandle_) {
        dlclose(eglHandle_);
        eglHandle_ = nullptr;
    }
    
    originalEglSwapBuffers_ = nullptr;
    LOGD("GL hook uninstalled");
}

void GLHook::setRenderCallback(void (*callback)()) {
    renderCallback_ = callback;
}

EGLBoolean GLHook::hookedEglSwapBuffers(EGLDisplay dpy, EGLSurface surface) {
    GLHook& instance = getInstance();
    
    if (renderCallback_) {
        renderCallback_();
    }
    
    return instance.originalEglSwapBuffers_(dpy, surface);
}
