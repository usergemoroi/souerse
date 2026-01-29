package com.example.espapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class EspService extends Service {
    private static final String TAG = "ESP_SERVICE";
    private boolean isRunning = false;
    private Thread espThread;
    private static EspService instance;
    
    public EspService() {
        instance = this;
    }

    public static EspService getInstance() {
        return instance;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service onStartCommand called");
        sendLog("ESP Service starting...");
        
        if (!isRunning) {
            isRunning = true;
            startEspThread();
        }
        
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Service destroyed");
        sendLog("ESP Service stopping...");
        stopNativeEspServer();
        isRunning = false;
        
        if (espThread != null) {
            espThread.interrupt();
            espThread = null;
        }
        
        instance = null;
        super.onDestroy();
    }

    private void startEspThread() {
        espThread = new Thread(() -> {
            try {
                Log.d(TAG, "Starting native ESP server...");
                sendLog("Starting native ESP server on port 9557...");
                startNativeEspServer();
            } catch (Exception e) {
                Log.e(TAG, "Error in ESP thread: " + e.getMessage(), e);
                sendLog("Error in ESP thread: " + e.getMessage());
            }
        });
        espThread.start();
    }

    // Called from native code via JNI
    public static void onNativeLog(String message) {
        if (instance != null) {
            instance.sendLog(message);
        }
    }

    // Called from native code via JNI
    public static void onPlayerCountChanged(int count) {
        if (instance != null) {
            instance.sendPlayerCount(count);
        }
    }

    // Called from native code via JNI
    public static void onFpsUpdate(int fps) {
        if (instance != null) {
            instance.sendFps(fps);
        }
    }

    private void sendLog(String message) {
        Intent intent = new Intent("ESP_LOG_UPDATE");
        intent.putExtra("log", message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendPlayerCount(int count) {
        Intent intent = new Intent("ESP_LOG_UPDATE");
        intent.putExtra("playerCount", count);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        
        // Also send to overlay
        Intent overlayIntent = new Intent("ESP_OVERLAY_UPDATE");
        overlayIntent.putExtra("playerCount", count);
        LocalBroadcastManager.getInstance(this).sendBroadcast(overlayIntent);
    }

    private void sendFps(int fps) {
        Intent intent = new Intent("ESP_OVERLAY_UPDATE");
        intent.putExtra("fps", fps);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    // Native methods
    private native void startNativeEspServer();
    private native void stopNativeEspServer();
}
