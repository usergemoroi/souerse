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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class OverlayService extends Service {
    private static final String TAG = "OVERLAY_SERVICE";
    
    private WindowManager windowManager;
    private View overlayView;
    private TextView statusText;
    private TextView playersText;
    private TextView fpsText;
    private Button stopButton;
    private Button hideButton;
    
    private int playerCount = 0;
    private int fps = 0;
    private long lastFpsUpdate = 0;
    private int frameCount = 0;
    
    private Handler uiHandler;
    
    private float initialX;
    private float initialY;
    private float initialTouchX;
    private float initialTouchY;

    private final BroadcastReceiver overlayReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("playerCount")) {
                playerCount = intent.getIntExtra("playerCount", 0);
                updateOverlayInfo();
            }
            if (intent.hasExtra("fps")) {
                fps = intent.getIntExtra("fps", 0);
                updateOverlayInfo();
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
        
        createOverlayView();
        
        // Register broadcast receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(
            overlayReceiver,
            new IntentFilter("ESP_OVERLAY_UPDATE")
        );
        
        // Start FPS counter
        startFpsCounter();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Overlay service destroyed");
        
        LocalBroadcastManager.getInstance(this).unregisterReceiver(overlayReceiver);
        
        if (overlayView != null && windowManager != null) {
            windowManager.removeView(overlayView);
            overlayView = null;
        }
    }

    private void createOverlayView() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        overlayView = inflater.inflate(R.layout.overlay_layout, null);
        
        // Get views
        statusText = overlayView.findViewById(R.id.overlay_status);
        playersText = overlayView.findViewById(R.id.overlay_players);
        fpsText = overlayView.findViewById(R.id.overlay_fps);
        stopButton = overlayView.findViewById(R.id.overlay_stop_button);
        hideButton = overlayView.findViewById(R.id.overlay_hide_button);
        
        // Set button listeners
        stopButton.setOnClickListener(v -> {
            Log.d(TAG, "Stop button clicked");
            stopEspService();
        });
        
        hideButton.setOnClickListener(v -> {
            Log.d(TAG, "Hide button clicked");
            hideOverlay();
        });
        
        // Setup window parameters
        int layoutFlag;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutFlag = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutFlag = WindowManager.LayoutParams.TYPE_PHONE;
        }
        
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        );
        
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 100;
        params.y = 100;
        
        // Add touch listener for dragging
        overlayView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                        
                    case MotionEvent.ACTION_MOVE:
                        params.x = (int) (initialX + (event.getRawX() - initialTouchX));
                        params.y = (int) (initialY + (event.getRawY() - initialTouchY));
                        windowManager.updateViewLayout(overlayView, params);
                        return true;
                }
                return false;
            }
        });
        
        // Add view to window
        windowManager.addView(overlayView, params);
        Log.d(TAG, "Overlay view added to window");
        
        updateOverlayInfo();
    }

    private void updateOverlayInfo() {
        if (overlayView == null) return;
        
        uiHandler.post(() -> {
            statusText.setText("Status: Running");
            playersText.setText("Players: " + playerCount);
            fpsText.setText("FPS: " + fps);
        });
    }

    private void startFpsCounter() {
        uiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (overlayView != null) {
                    frameCount++;
                    long currentTime = System.currentTimeMillis();
                    
                    if (currentTime - lastFpsUpdate >= 1000) {
                        fps = frameCount;
                        frameCount = 0;
                        lastFpsUpdate = currentTime;
                        updateOverlayInfo();
                    }
                    
                    uiHandler.postDelayed(this, 16); // ~60 FPS
                }
            }
        }, 16);
    }

    private void stopEspService() {
        // Stop ESP service
        Intent espIntent = new Intent(this, EspService.class);
        stopService(espIntent);
        
        // Stop self
        stopSelf();
        
        Log.d(TAG, "ESP and overlay services stopped");
    }

    private void hideOverlay() {
        if (overlayView != null) {
            overlayView.setVisibility(View.GONE);
            
            // Show again after 5 seconds
            uiHandler.postDelayed(() -> {
                if (overlayView != null) {
                    overlayView.setVisibility(View.VISIBLE);
                }
            }, 5000);
        }
    }
}
