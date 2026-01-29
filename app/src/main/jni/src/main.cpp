#include "memory/memory.h"
#include "network/socket_server.h"
#include "esp/esp.h"
#include <unistd.h>
#include <cstdio>
#include <pthread.h>
#include <jni.h>
#include <android/log.h>

#define LOG_TAG "ESP_NATIVE"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

static bool g_running = false;
static pthread_t g_esp_thread;
static JavaVM* g_jvm = nullptr;
static jclass g_espServiceClass = nullptr;
static jmethodID g_onLogMethod = nullptr;
static jmethodID g_onPlayerCountMethod = nullptr;
static jmethodID g_onFpsMethod = nullptr;

// Helper function to send log to Java
void sendLogToJava(const char* message)
{
    if (!g_jvm || !g_espServiceClass || !g_onLogMethod) return;
    
    JNIEnv* env;
    bool attached = false;
    
    int getEnvStat = g_jvm->GetEnv((void**)&env, JNI_VERSION_1_6);
    if (getEnvStat == JNI_EDETACHED) {
        if (g_jvm->AttachCurrentThread(&env, nullptr) == 0) {
            attached = true;
        } else {
            return;
        }
    }
    
    if (env) {
        jstring jmsg = env->NewStringUTF(message);
        env->CallStaticVoidMethod(g_espServiceClass, g_onLogMethod, jmsg);
        env->DeleteLocalRef(jmsg);
        
        if (attached) {
            g_jvm->DetachCurrentThread();
        }
    }
}

// Helper function to send player count to Java
void sendPlayerCountToJava(int count)
{
    if (!g_jvm || !g_espServiceClass || !g_onPlayerCountMethod) return;
    
    JNIEnv* env;
    bool attached = false;
    
    int getEnvStat = g_jvm->GetEnv((void**)&env, JNI_VERSION_1_6);
    if (getEnvStat == JNI_EDETACHED) {
        if (g_jvm->AttachCurrentThread(&env, nullptr) == 0) {
            attached = true;
        } else {
            return;
        }
    }
    
    if (env) {
        env->CallStaticVoidMethod(g_espServiceClass, g_onPlayerCountMethod, count);
        
        if (attached) {
            g_jvm->DetachCurrentThread();
        }
    }
}

// Helper function to send FPS to Java
void sendFpsToJava(int fps)
{
    if (!g_jvm || !g_espServiceClass || !g_onFpsMethod) return;
    
    JNIEnv* env;
    bool attached = false;
    
    int getEnvStat = g_jvm->GetEnv((void**)&env, JNI_VERSION_1_6);
    if (getEnvStat == JNI_EDETACHED) {
        if (g_jvm->AttachCurrentThread(&env, nullptr) == 0) {
            attached = true;
        } else {
            return;
        }
    }
    
    if (env) {
        env->CallStaticVoidMethod(g_espServiceClass, g_onFpsMethod, fps);
        
        if (attached) {
            g_jvm->DetachCurrentThread();
        }
    }
}

void* espMainLoop(void* arg)
{
    LOGD("ESP main loop started");
    sendLogToJava("ESP main loop started");
    setbuf(stdout, NULL);

    g_socket_server.start();
    LOGD("Socket server started on port 9557");
    sendLogToJava("Socket server started on port 9557");

    pid_t processID = memory_utils::findProcess("com.axlebolt.standoff2");
    while (!processID && g_running)
    {
        LOGD("Waiting for Standoff 2 process...");
        sendLogToJava("Waiting for Standoff 2 process...");
        usleep(1000000);
        processID = memory_utils::findProcess("com.axlebolt.standoff2");
    }

    if (!g_running)
    {
        LOGD("ESP stopped before finding process");
        sendLogToJava("ESP stopped before finding process");
        g_socket_server.stop();
        return nullptr;
    }

    char pidMsg[256];
    snprintf(pidMsg, sizeof(pidMsg), "✓ Found Standoff 2 process with PID: %d", processID);
    LOGD("%s", pidMsg);
    sendLogToJava(pidMsg);
    memory_utils::initialize(processID);

    uint32_t libUnityBase = 0;
    while (!libUnityBase && g_running)
    {
        LOGD("Searching for libunity.so...");
        sendLogToJava("Searching for libunity.so...");
        usleep(1000000);
        libUnityBase = memory_utils::findModule(processID, "libunity.so");
    }

    if (!g_running)
    {
        LOGD("ESP stopped before finding libunity.so");
        sendLogToJava("ESP stopped before finding libunity.so");
        g_socket_server.stop();
        return nullptr;
    }

    char baseMsg[256];
    snprintf(baseMsg, sizeof(baseMsg), "✓ Found libunity.so at base: 0x%X", libUnityBase);
    LOGD("%s", baseMsg);
    sendLogToJava(baseMsg);
    sendLogToJava("ESP is now running");
    
    ESP esp(libUnityBase);

    int frameCount = 0;
    long lastFpsTime = 0;
    int currentFps = 0;
    int lastPlayerCount = -1;

    while (g_running)
    {
        esp.Render();
        
        // Calculate FPS
        frameCount++;
        long currentTime = time(nullptr);
        if (currentTime != lastFpsTime) {
            currentFps = frameCount;
            frameCount = 0;
            lastFpsTime = currentTime;
            sendFpsToJava(currentFps);
        }
        
        // Send player count when it changes
        int currentPlayerCount = esp.GetLastPlayerCount();
        if (currentPlayerCount != lastPlayerCount) {
            lastPlayerCount = currentPlayerCount;
            sendPlayerCountToJava(currentPlayerCount);
        }
        
        usleep(10000);
    }

    LOGD("ESP main loop stopped");
    sendLogToJava("ESP main loop stopped");
    g_socket_server.stop();
    return nullptr;
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_espapp_EspService_startNativeEspServer(JNIEnv* env, jobject thiz)
{
    LOGD("startNativeEspServer called");
    
    if (g_running)
    {
        LOGD("ESP server already running");
        return;
    }

    // Cache JavaVM and method IDs
    if (!g_jvm) {
        env->GetJavaVM(&g_jvm);
    }
    
    if (!g_espServiceClass) {
        jclass localClass = env->FindClass("com/example/espapp/EspService");
        if (localClass) {
            g_espServiceClass = (jclass)env->NewGlobalRef(localClass);
            env->DeleteLocalRef(localClass);
            
            g_onLogMethod = env->GetStaticMethodID(g_espServiceClass, "onNativeLog", "(Ljava/lang/String;)V");
            g_onPlayerCountMethod = env->GetStaticMethodID(g_espServiceClass, "onPlayerCountChanged", "(I)V");
            g_onFpsMethod = env->GetStaticMethodID(g_espServiceClass, "onFpsUpdate", "(I)V");
        }
    }

    g_running = true;
    
    int result = pthread_create(&g_esp_thread, nullptr, espMainLoop, nullptr);
    if (result != 0)
    {
        LOGE("Failed to create ESP thread: %d", result);
        sendLogToJava("✗ Failed to create ESP thread");
        g_running = false;
        return;
    }

    LOGD("ESP thread created successfully");
    sendLogToJava("✓ ESP thread created successfully");
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_espapp_EspService_stopNativeEspServer(JNIEnv* env, jobject thiz)
{
    LOGD("stopNativeEspServer called");
    
    if (!g_running)
    {
        LOGD("ESP server not running");
        return;
    }

    g_running = false;
    
    pthread_join(g_esp_thread, nullptr);
    
    LOGD("ESP thread stopped");
    sendLogToJava("ESP thread stopped");
    
    // Clean up global references
    if (g_espServiceClass && env) {
        env->DeleteGlobalRef(g_espServiceClass);
        g_espServiceClass = nullptr;
    }
}
