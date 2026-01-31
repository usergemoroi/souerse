package com.example.espapp;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.FrameLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class OverlayService extends Service {
    private static final String TAG = "OVERLAY_SERVICE";
    
    private CheatMenu cheatMenu;
    
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
                // This would be expanded when native code sends player data
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
        Log.d(TAG, "Overlay service created");
        
        uiHandler = new Handler(Looper.getMainLooper());
        settings = EspSettings.getInstance(this);
        
        createOverlayRenderer();
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

    private void createOverlayRenderer() {
        // V6: Native rendering - no overlay for ESP, only menu
        // ESP is now rendered natively in the game by C++ code
        Log.d(TAG, "V6: Native ESP rendering enabled");
        
        // Initialize native renderer with screen dimensions
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        
        EspService espService = EspService.getInstance();
        if (espService != null) {
            espService.initNativeRenderer(screenWidth, screenHeight);
            espService.updateNativeSettings(settings);
            Log.d(TAG, "Native renderer initialized: " + screenWidth + "x" + screenHeight);
        }
    }
    
    private void createCheatMenu() {
        cheatMenu = new CheatMenu(this);
        Log.d(TAG, "Cheat menu created");
    }

    private void startEspUpdateLoop() {
        // V6: Settings update loop for native renderer
        uiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateNativeSettings();
                uiHandler.postDelayed(this, 1000);
            }
        }, 1000);
    }

    private void updateEspData() {
        // V6: No longer needed - native code handles ESP data
    }
    
    private void updateNativeSettings() {
        // Send updated settings to native renderer
        EspService espService = EspService.getInstance();
        if (espService != null) {
            espService.updateNativeSettings(settings);
        }
    }
}
