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
    
    private WindowManager windowManager;
    private FrameLayout overlayContainer;
    private EspRenderer espRenderer;
    private CheatMenu cheatMenu;
    
    private EspSettings settings;
    private EspData espData;
    
    private int playerCount = 0;
    private int fps = 0;
    private Handler uiHandler;
    
    private WindowManager.LayoutParams overlayParams;

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
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        settings = EspSettings.getInstance(this);
        espData = new EspData();
        
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
        
        if (overlayContainer != null && windowManager != null) {
            try {
                windowManager.removeView(overlayContainer);
                overlayContainer = null;
            } catch (Exception e) {
                Log.e(TAG, "Error removing overlay: " + e.getMessage(), e);
            }
        }
    }

    private void createOverlayRenderer() {
        overlayContainer = new FrameLayout(this);
        overlayContainer.setBackgroundColor(0x00000000);
        
        espRenderer = new EspRenderer(this);
        overlayContainer.addView(espRenderer);
        
        int layoutFlag;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutFlag = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutFlag = WindowManager.LayoutParams.TYPE_PHONE;
        }
        
        overlayParams = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | 
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        );
        
        overlayParams.gravity = Gravity.TOP | Gravity.START;
        overlayParams.x = 0;
        overlayParams.y = 0;
        
        try {
            windowManager.addView(overlayContainer, overlayParams);
            Log.d(TAG, "ESP overlay renderer created");
        } catch (Exception e) {
            Log.e(TAG, "Error creating overlay renderer: " + e.getMessage(), e);
        }
    }
    
    private void createCheatMenu() {
        cheatMenu = new CheatMenu(this);
        Log.d(TAG, "Cheat menu created");
    }

    private void startEspUpdateLoop() {
        uiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (espRenderer != null) {
                    updateEspData();
                    uiHandler.postDelayed(this, 16);
                }
            }
        }, 16);
    }

    private void updateEspData() {
        if (espData == null || espRenderer == null) return;
        
        espData.playerCount = playerCount;
        espData.screenWidth = getResources().getDisplayMetrics().widthPixels;
        espData.screenHeight = getResources().getDisplayMetrics().heightPixels;
        espData.centerX = espData.screenWidth / 2;
        espData.centerY = espData.screenHeight;
        
        if (playerCount > 0 && espData.players.length < playerCount) {
            espData.players = new EspData.PlayerInfo[playerCount];
            for (int i = 0; i < playerCount; i++) {
                espData.players[i] = createMockPlayerData(i);
            }
        }
        
        espRenderer.updateEspData(espData);
    }
    
    private EspData.PlayerInfo createMockPlayerData(int index) {
        EspData.PlayerInfo player = new EspData.PlayerInfo();
        
        float screenWidth = getResources().getDisplayMetrics().widthPixels;
        float screenHeight = getResources().getDisplayMetrics().heightPixels;
        
        float angle = (float) (index * Math.PI * 2 / Math.max(playerCount, 1));
        float radius = 200 + index * 50;
        
        float centerX = screenWidth / 2;
        float centerY = screenHeight / 2;
        
        player.screenX = centerX + (float) Math.cos(angle) * radius;
        player.screenY = centerY + (float) Math.sin(angle) * radius;
        
        float boxWidth = 60;
        float boxHeight = 120;
        
        player.boxLeft = player.screenX - boxWidth / 2;
        player.boxRight = player.screenX + boxWidth / 2;
        player.boxTop = player.screenY - boxHeight;
        player.boxBottom = player.screenY;
        
        player.headX = player.screenX;
        player.headY = player.boxTop;
        player.footX = player.screenX;
        player.footY = player.boxBottom;
        
        player.distance = radius / 10.0f;
        player.health = 50 + (index * 13) % 100;
        player.maxHealth = 100;
        player.armor = (index * 27) % 100;
        player.team = index % 2;
        player.name = "Player" + (index + 1);
        player.isVisible = true;
        player.isEnemy = player.team != 0;
        
        return player;
    }
}
