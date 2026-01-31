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
    private static final String TAG = "ESP_APP_V7";
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
        
        // Disable buttons initially
        startButton.setEnabled(false);
        stopButton.setEnabled(false);
        
        startButton.setOnClickListener(v -> startEspService());
        stopButton.setOnClickListener(v -> {
            stopButton.setEnabled(false);
            stopEspService();
            uiHandler.postDelayed(() -> stopButton.setEnabled(false), 1000);
        });
        
        updateStatus("Initializing...");
        addLog("═══════════════════════════════");
        addLog("ESP V8 RELEASE");
        addLog("═══════════════════════════════");
        addLog("Application started");
        
        // Register broadcast receiver for logs from service
        LocalBroadcastManager.getInstance(this).registerReceiver(
            logReceiver,
            new IntentFilter("ESP_LOG_UPDATE")
        );
        
        // Check root access FIRST - this is critical
        requestRootAccessDialog();
        
        // Check overlay permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                addLog("Overlay permission not granted");
                requestOverlayPermission();
            } else {
                addLog("✓ Overlay permission already granted");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(logReceiver);
    }

    private void requestRootAccessDialog() {
        addLog("═══════════════════════════════");
        addLog("▶ Root Access Required");
        addLog("═══════════════════════════════");
        
        new AlertDialog.Builder(this)
            .setTitle("Требуются рут права")
            .setMessage("Приложение требует рут доступ для работы.\n\n" +
                       "Когда появится диалог SuperUser/Magisk - нажмите РАЗРЕШИТЬ")
            .setPositiveButton("Запросить", (d, w) -> {
                addLog("Requesting root access...");
                performRootRequest();
            })
            .setNegativeButton("Отмена", (d, w) -> {
                addLog("✗ Root access request cancelled");
                updateStatus("Error: Root access required");
            })
            .setCancelable(false)
            .show();
    }
    
    private void performRootRequest() {
        new Thread(() -> {
            try {
                addLog("Executing 'su' command...");
                Process process = Runtime.getRuntime().exec("su");
                process.getOutputStream().write("exit\n".getBytes());
                process.getOutputStream().flush();
                process.getOutputStream().close();
                int exitValue = process.waitFor();
                
                uiHandler.post(() -> {
                    if (exitValue == 0) {
                        addLog("✓ Root access granted!");
                        addLog("═══════════════════════════════");
                        hasRootAccess = true;
                        startButton.setEnabled(true);
                        updateStatus("Ready (Root Access: YES)");
                    } else {
                        addLog("✗ Root access denied");
                        addLog("═══════════════════════════════");
                        hasRootAccess = false;
                        updateStatus("Error: Root access denied");
                        showRootWarningDialog();
                    }
                });
            } catch (Exception e) {
                uiHandler.post(() -> {
                    addLog("✗ Error requesting root: " + e.getMessage());
                    addLog("═══════════════════════════════");
                    hasRootAccess = false;
                    updateStatus("Error: Root check failed");
                    showRootWarningDialog();
                });
            }
        }).start();
    }
    
    private void checkRootAccess() {
        addLog("Checking root access...");
        
        new Thread(() -> {
            hasRootAccess = checkRootAccessInternal();
            
            uiHandler.post(() -> {
                if (hasRootAccess) {
                    addLog("✓ Root access confirmed");
                    startButton.setEnabled(true);
                    updateStatus("Ready (Root Access: YES)");
                } else {
                    addLog("✗ Root access not available");
                    addLog("Warning: ESP requires root access");
                    startButton.setEnabled(false);
                    updateStatus("Ready (Root Access: NO)");
                    
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
            addLog("❌ Error: Root access required");
            showRootWarningDialog();
            return;
        }
        
        addLog("═══════════════════════════════");
        addLog("▶ Starting ESP Service...");
        addLog("═══════════════════════════════");
        updateStatus("Initializing...");
        
        // Check Standoff 2 first
        if (!isStandoff2Installed()) {
            addLog("❌ Standoff 2 не установлен");
            addLog("═══════════════════════════════");
            updateStatus("Error: Standoff 2 not found");
            
            new AlertDialog.Builder(this)
                .setTitle("Standoff 2 Not Found")
                .setMessage("Standoff 2 is not installed on this device. " +
                           "Please install it first.")
                .setPositiveButton("OK", null)
                .show();
            return;
        }
        addLog("✓ Standoff 2 detected");
        
        // Start ESP service
        try {
            Intent espIntent = new Intent(this, EspService.class);
            startService(espIntent);
            addLog("✓ ESP Service started");
        } catch (Exception e) {
            addLog("❌ Failed to start ESP Service: " + e.getMessage());
            Log.e(TAG, "ESP Service error", e);
            return;
        }
        
        // Start Overlay service (Cheat Menu)
        try {
            Intent overlayIntent = new Intent(this, OverlayService.class);
            startService(overlayIntent);
            addLog("✓ Overlay Service started");
        } catch (Exception e) {
            addLog("⚠ Warning: Overlay Service failed: " + e.getMessage());
            Log.e(TAG, "Overlay Service error", e);
        }
        
        isEspRunning = true;
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        
        addLog("✓ Services initialized");
        updateStatus("ESP Running - Waiting for injection...");
        
        // Launch Standoff 2 after a short delay
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                uiHandler.post(() -> {
                    addLog("═══════════════════════════════");
                    addLog("▶ Launching Standoff 2...");
                    addLog("═══════════════════════════════");
                    launchStandoff2();
                });
            } catch (Exception e) {
                uiHandler.post(() -> {
                    addLog("❌ Error: " + e.getMessage());
                    Log.e(TAG, "Launch delay error", e);
                });
            }
        }).start();
        
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
        
        addLog("═══════════════════════════════");
        addLog("⏹ Stopping ESP Service...");
        addLog("═══════════════════════════════");
        
        // Run in separate thread to prevent UI freeze
        new Thread(() -> {
            try {
                // Stop services with timeout protection
                Intent espIntent = new Intent(MainActivity.this, EspService.class);
                stopService(espIntent);
                uiHandler.post(() -> addLog("✓ ESP Service stopped"));
                
                Thread.sleep(200);
                
                Intent overlayIntent = new Intent(MainActivity.this, OverlayService.class);
                stopService(overlayIntent);
                uiHandler.post(() -> addLog("✓ Overlay Service stopped"));
                
                isEspRunning = false;
                
                uiHandler.post(() -> {
                    updateStatus("ESP Service Stopped");
                    addLog("═══════════════════════════════");
                    addLog("✓ INJECTION DETACHED");
                    addLog("═══════════════════════════════");
                    startButton.setEnabled(hasRootAccess);
                    stopButton.setEnabled(false);
                });
                
                Log.d(TAG, "ESP Service stopped");
                
            } catch (Exception e) {
                uiHandler.post(() -> {
                    addLog("✗ Error stopping: " + e.getMessage());
                    Log.e(TAG, "Stop error", e);
                    updateStatus("Error: " + e.getMessage());
                    stopButton.setEnabled(true);
                });
            }
        }).start();
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
