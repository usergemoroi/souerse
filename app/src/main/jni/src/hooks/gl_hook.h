#pragma once
#include <EGL/egl.h>
#include <GLES2/gl2.h>
#include <dlfcn.h>

typedef EGLBoolean (*eglSwapBuffers_t)(EGLDisplay dpy, EGLSurface surface);

class GLHook {
public:
    static GLHook& getInstance();
    
    bool install();
    void uninstall();
    
    bool isHooked() const { return hooked_; }
    
    void setRenderCallback(void (*callback)());
    
private:
    GLHook();
    ~GLHook();
    
    static EGLBoolean hookedEglSwapBuffers(EGLDisplay dpy, EGLSurface surface);
    
    eglSwapBuffers_t originalEglSwapBuffers_;
    void* eglHandle_;
    bool hooked_;
    
    static void (*renderCallback_)();
};
