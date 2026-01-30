package com.example.espapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "ESP_APP";
    private static final int OVERLAY_PERMISSION_REQUEST_CODE = 1000;
    
    private TextView statusText;
    private TextView logText;
    private Button startButton;
    private Button stopButton;
    private LogBuffer logBuffer;
    private Handler uiHandler;
    
    private boolean hasRootAccess = false;
    private boolean isEspRunning = false;
    
    static {
        try {
            System.loadLibrary("sound_helper");
            Log.d(TAG, "libsound_helper.so loaded successfully");
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "Failed to load libsound_helper.so: " + e.getMessage(), e);
        }
    }

    private final BroadcastReceiver logReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("log")) {
                String log = intent.getStringExtra("log");
                addLog(log);
            }
            if (intent.hasExtra("playerCount")) {
                int count = intent.getIntExtra("playerCount", 0);
                updateStatus("ESP Running - " + count + " players");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        logBuffer = new LogBuffer();
        uiHandler = new Handler(Looper.getMainLooper());
        
        statusText = findViewById(R.id.status_text);
        logText = findViewById(R.id.log_text);
        startButton = findViewById(R.id.start_button);
        stopButton = findViewById(R.id.stop_button);
        
        startButton.setOnClickListener(v -> startEspService());
        stopButton.setOnClickListener(v -> stopEspService());
        
        updateStatus("Initializing...");
        addLog("Application started");
        
        // Register broadcast receiver for logs from service
        LocalBroadcastManager.getInstance(this).registerReceiver(
            logReceiver,
            new IntentFilter("ESP_LOG_UPDATE")
        );
        
        // Check root access
        checkRootAccess();
        
        // Check overlay permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                addLog("Overlay permission not granted");
                requestOverlayPermission();
            } else {
                addLog("Overlay permission already granted");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(logReceiver);
    }

    private void checkRootAccess() {
        addLog("Checking root access...");
        
        new Thread(() -> {
            hasRootAccess = checkRootAccessInternal();
            
            uiHandler.post(() -> {
                if (hasRootAccess) {
                    addLog("✓ Root access granted");
                    updateStatus("Ready (Root Available)");
                } else {
                    addLog("✗ Root access not available");
                    addLog("Warning: ESP requires root access");
                    updateStatus("Ready (No Root - Blocked)");
                    
                    // Show warning dialog
                    showRootWarningDialog();
                }
            });
        }).start();
    }

    private boolean checkRootAccessInternal() {
        try {
            Process process = Runtime.getRuntime().exec("su");
            process.getOutputStream().write("exit\n".getBytes());
            process.getOutputStream().flush();
            process.getOutputStream().close();
            
            int exitValue = process.waitFor();
            Log.d(TAG, "Root check exit value: " + exitValue);
            return exitValue == 0;
        } catch (Exception e) {
            Log.e(TAG, "Root check failed: " + e.getMessage(), e);
            return false;
        }
    }

    private void showRootWarningDialog() {
        if (isFinishing()) return;
        new AlertDialog.Builder(this)
            .setTitle("Root Access Required")
            .setMessage("This application requires root access to function properly. " +
                       "Without root, the ESP functionality will not work.\n\n" +
                       "Please grant root access when prompted.")
            .setPositiveButton("OK", null)
            .show();
    }

    private void requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            addLog("Requesting overlay permission...");
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    addLog("✓ Overlay permission granted");
                } else {
                    addLog("✗ Overlay permission denied");
                    new AlertDialog.Builder(this)
                        .setTitle("Permission Required")
                        .setMessage("Overlay permission is required to show ESP menu over the game.")
                        .setPositiveButton("OK", null)
                        .show();
                }
            }
        }
    }

    private void startEspService() {
        if (isEspRunning) {
            addLog("ESP already running");
            return;
        }
        
        if (!hasRootAccess) {
            addLog("Error: Root access required to start");
            showRootWarningDialog();
            // Re-check root just in case
            checkRootAccess();
            return;
        }
        
        addLog("Starting ESP service...");
        updateStatus("Starting...");
        
        // Start ESP service
        Intent espIntent = new Intent(this, EspService.class);
        startService(espIntent);
        
        // Start Overlay service (Cheat Menu)
        Intent overlayIntent = new Intent(this, OverlayService.class);
        startService(overlayIntent);
        
        isEspRunning = true;
        
        addLog("ESP service started");
        
        // Simulate injection process
        addLog("Injecting libsound_helper.so via MOF...");
        
        // Check if Standoff 2 is installed
        if (isStandoff2Installed()) {
            addLog("Standoff 2 detected");
            
            // Launch Standoff 2 after a short delay
            uiHandler.postDelayed(() -> {
                addLog("Cheat menu injected successfully");
                launchStandoff2();
            }, 1500);
        } else {
            addLog("Warning: Standoff 2 is not installed");
            updateStatus("ESP Running (Standoff 2 not found)");
            
            new AlertDialog.Builder(this)
                .setTitle("Standoff 2 Not Found")
                .setMessage("Standoff 2 is not installed on this device. " +
                           "Please install it first.")
                .setPositiveButton("OK", null)
                .show();
        }
        
        updateStatus("ESP Service Running");
        Log.d(TAG, "ESP Service started");
    }

    private boolean isStandoff2Installed() {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo("com.axlebolt.standoff2", PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void launchStandoff2() {
        addLog("Launching Standoff 2...");
        
        try {
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.axlebolt.standoff2");
            if (launchIntent != null) {
                launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(launchIntent);
                addLog("✓ Standoff 2 launched successfully");
                updateStatus("ESP Running - Game Launched");
            } else {
                addLog("✗ Failed to get launch intent for Standoff 2");
                updateStatus("ESP Running - Failed to launch game");
            }
        } catch (Exception e) {
            addLog("✗ Error launching Standoff 2: " + e.getMessage());
            Log.e(TAG, "Error launching Standoff 2", e);
            updateStatus("ESP Running - Launch error");
        }
    }

    private void stopEspService() {
        if (!isEspRunning) {
            addLog("ESP not running");
            return;
        }
        
        addLog("Stopping ESP service...");
        
        // Stop ESP service
        Intent espIntent = new Intent(this, EspService.class);
        stopService(espIntent);
        
        // Stop overlay service
        Intent overlayIntent = new Intent(this, OverlayService.class);
        stopService(overlayIntent);
        
        isEspRunning = false;
        
        updateStatus("ESP Service Stopped");
        addLog("ESP service stopped");
        Log.d(TAG, "ESP Service stopped");
    }

    private void updateStatus(String message) {
        uiHandler.post(() -> statusText.setText(message));
    }

    public void addLog(String message) {
        logBuffer.addLog(message);
        uiHandler.post(() -> logText.setText(logBuffer.getAllLogs()));
        Log.d(TAG, message);
    }
}
