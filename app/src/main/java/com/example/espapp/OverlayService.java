package com.example.espapp;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

/**
 * OverlayService V7 - Pure Native Renderer Mode
 * 
 * NO OVERLAY - All UI rendering happens natively via OpenGL inside the game
 * This service only manages settings synchronization with the native renderer
 */
public class OverlayService extends Service {
    private static final String TAG = "OVERLAY_SERVICE_V7";
    
    private CheatMenuV7 cheatMenu;
    private EspSettings settings;
    
    private int playerCount = 0;
    private int fps = 0;
    private Handler uiHandler;

    private final BroadcastReceiver overlayReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("playerCount")) {
                playerCount = intent.getIntExtra("playerCount", 0);
                updateEspData();
            }
            if (intent.hasExtra("fps")) {
                fps = intent.getIntExtra("fps", 0);
            }
            if (intent.hasExtra("espData")) {
                // Handle ESP data from native code
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "V7 Overlay service created - PURE NATIVE RENDERING MODE");
        Log.d(TAG, "NO OVERLAY WINDOWS - Menu rendered inside game via OpenGL");
        
        uiHandler = new Handler(Looper.getMainLooper());
        settings = EspSettings.getInstance(this);
        
        // Initialize native renderer only - NO OVERLAY
        initializeNativeRenderer();
        
        // Initialize settings manager (no UI)
        createCheatMenu();
        
        LocalBroadcastManager.getInstance(this).registerReceiver(
            overlayReceiver,
            new IntentFilter("ESP_OVERLAY_UPDATE")
        );
        
        startEspUpdateLoop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Overlay service destroyed");
        
        LocalBroadcastManager.getInstance(this).unregisterReceiver(overlayReceiver);
        
        if (cheatMenu != null) {
            cheatMenu.destroy();
            cheatMenu = null;
        }
    }

    /**
     * Initialize native OpenGL renderer
     * Menu will be rendered inside the game via native code
     */
    private void initializeNativeRenderer() {
        Log.d(TAG, "V7: Initializing native OpenGL menu renderer (INJECT MODE)");
        
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        
        EspService espService = EspService.getInstance();
        if (espService != null) {
            espService.initNativeRenderer(screenWidth, screenHeight);
            espService.updateNativeSettings(settings);
            Log.d(TAG, "Native renderer initialized: " + screenWidth + "x" + screenHeight);
            Log.d(TAG, "Native menu will render INSIDE the game - NO overlay window");
        }
    }
    
    /**
     * Create settings manager (no UI - CheatMenuV7 only manages settings sync)
     */
    private void createCheatMenu() {
        cheatMenu = new CheatMenuV7(this);
        Log.d(TAG, "CheatMenu V7 created (native rendering only, no overlay)");
    }

    /**
     * Settings update loop - syncs to native renderer
     */
    private void startEspUpdateLoop() {
        uiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateNativeSettings();
                uiHandler.postDelayed(this, 1000);
            }
        }, 1000);
    }

    private void updateEspData() {
        // Native code handles all ESP data
    }
    
    /**
     * Synchronize settings to native renderer
     * The native menu reads and updates these settings directly
     */
    private void updateNativeSettings() {
        EspService espService = EspService.getInstance();
        if (espService != null) {
            espService.updateNativeSettings(settings);
        }
        
        // Sync settings state
        if (cheatMenu != null) {
            cheatMenu.syncSettings();
        }
    }
}
