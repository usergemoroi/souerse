package com.example.espapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ScrollView;
import android.util.Log;

public class CheatMenu {
    private static final String TAG = "CHEAT_MENU";
    
    private Context context;
    private WindowManager windowManager;
    private View menuView;
    private LinearLayout menuContainer;
    private LinearLayout menuContent;
    private TextView menuTitle;
    private Button minimizeButton;
    private Button closeButton;
    private ScrollView scrollView;
    
    private EspSettings settings;
    
    private float initialX;
    private float initialY;
    private float initialTouchX;
    private float initialTouchY;
    
    private boolean isMinimized = false;
    private boolean isVisible = true;
    
    private WindowManager.LayoutParams params;
    
    public CheatMenu(Context context) {
        this.context = context;
        this.windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        this.settings = EspSettings.getInstance(context);
        createMenu();
    }
    
    private void createMenu() {
        menuView = createMenuLayout();
        
        int layoutFlag;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutFlag = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutFlag = WindowManager.LayoutParams.TYPE_PHONE;
        }
        
        params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        );
        
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 50;
        params.y = 50;
        
        setupTouchListener();
        
        try {
            windowManager.addView(menuView, params);
            Log.d(TAG, "Cheat menu created successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error creating cheat menu: " + e.getMessage(), e);
        }
    }
    
    private View createMenuLayout() {
        menuContainer = new LinearLayout(context);
        menuContainer.setOrientation(LinearLayout.VERTICAL);
        menuContainer.setBackgroundColor(0xE0121212);
        menuContainer.setPadding(20, 15, 20, 15);
        
        LinearLayout titleBar = new LinearLayout(context);
        titleBar.setOrientation(LinearLayout.HORIZONTAL);
        titleBar.setGravity(Gravity.CENTER_VERTICAL);
        
        menuTitle = new TextView(context);
        menuTitle.setText("⚡ CHEAT MENU V6 ⚡");
        menuTitle.setTextColor(0xFF00FFFF);
        menuTitle.setTextSize(18);
        menuTitle.setPadding(0, 0, 20, 0);
        menuTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f
        );
        menuTitle.setLayoutParams(titleParams);
        
        minimizeButton = new Button(context);
        minimizeButton.setText("_");
        minimizeButton.setTextColor(0xFFFFFF00);
        minimizeButton.setBackgroundColor(0x80333333);
        minimizeButton.setPadding(20, 5, 20, 5);
        minimizeButton.setOnClickListener(v -> toggleMinimize());
        
        closeButton = new Button(context);
        closeButton.setText("X");
        closeButton.setTextColor(0xFFFF0000);
        closeButton.setBackgroundColor(0x80333333);
        closeButton.setPadding(20, 5, 20, 5);
        closeButton.setOnClickListener(v -> hide());
        
        titleBar.addView(menuTitle);
        titleBar.addView(minimizeButton);
        titleBar.addView(closeButton);
        
        scrollView = new ScrollView(context);
        menuContent = new LinearLayout(context);
        menuContent.setOrientation(LinearLayout.VERTICAL);
        menuContent.setPadding(0, 15, 0, 0);
        
        addEspSection();
        addSettingsSection();
        
        scrollView.addView(menuContent);
        
        menuContainer.addView(titleBar);
        menuContainer.addView(scrollView);
        
        return menuContainer;
    }
    
    private void addEspSection() {
        addSectionTitle("ESP FEATURES");
        
        addToggleSwitch("ESP Lines", settings.espLinesEnabled, (buttonView, isChecked) -> {
            settings.espLinesEnabled = isChecked;
            settings.saveSettings();
        });
        
        addToggleSwitch("ESP Box", settings.espBoxEnabled, (buttonView, isChecked) -> {
            settings.espBoxEnabled = isChecked;
            settings.saveSettings();
        });
        
        addToggleSwitch("ESP Health Bars", settings.espHealthEnabled, (buttonView, isChecked) -> {
            settings.espHealthEnabled = isChecked;
            settings.saveSettings();
        });
        
        addToggleSwitch("ESP Skeleton", settings.espSkeletonEnabled, (buttonView, isChecked) -> {
            settings.espSkeletonEnabled = isChecked;
            settings.saveSettings();
        });
        
        addToggleSwitch("ESP Names", settings.espNamesEnabled, (buttonView, isChecked) -> {
            settings.espNamesEnabled = isChecked;
            settings.saveSettings();
        });
        
        addToggleSwitch("ESP Distance", settings.espDistanceEnabled, (buttonView, isChecked) -> {
            settings.espDistanceEnabled = isChecked;
            settings.saveSettings();
        });
        
        addToggleSwitch("Aimbot Indicator", settings.espAimbotIndicatorEnabled, (buttonView, isChecked) -> {
            settings.espAimbotIndicatorEnabled = isChecked;
            settings.saveSettings();
        });
        
        addToggleSwitch("Wallhack Mode", settings.wallhackEnabled, (buttonView, isChecked) -> {
            settings.wallhackEnabled = isChecked;
            settings.saveSettings();
        });
    }
    
    private void addSettingsSection() {
        addSectionTitle("SETTINGS");
        
        addSlider("Line Thickness", 1, 10, (int) (settings.espLineThickness * 2), progress -> {
            settings.espLineThickness = progress / 2.0f;
            settings.saveSettings();
        });
        
        addSlider("Box Thickness", 1, 10, (int) (settings.espBoxThickness * 2), progress -> {
            settings.espBoxThickness = progress / 2.0f;
            settings.saveSettings();
        });
        
        addSlider("Text Size", 8, 24, settings.espTextSize, progress -> {
            settings.espTextSize = progress;
            settings.saveSettings();
        });
        
        addSlider("Opacity", 10, 100, (int) (settings.espOpacity * 100), progress -> {
            settings.espOpacity = progress / 100.0f;
            settings.saveSettings();
        });
        
        addSlider("Max Distance", 50, 1000, (int) settings.maxRenderDistance, progress -> {
            settings.maxRenderDistance = (float) progress;
            settings.saveSettings();
        });
        
        addSectionTitle("FILTERS");
        
        addToggleSwitch("Show Enemies", settings.showEnemyPlayers, (buttonView, isChecked) -> {
            settings.showEnemyPlayers = isChecked;
            settings.saveSettings();
        });
        
        addToggleSwitch("Show Friendlies", settings.showFriendlyPlayers, (buttonView, isChecked) -> {
            settings.showFriendlyPlayers = isChecked;
            settings.saveSettings();
        });
        
        addSectionTitle("ACTIONS");
        
        Button resetButton = new Button(context);
        resetButton.setText("Reset to Defaults");
        resetButton.setTextColor(0xFFFFFFFF);
        resetButton.setBackgroundColor(0x80FF4444);
        resetButton.setPadding(20, 10, 20, 10);
        resetButton.setOnClickListener(v -> {
            settings.resetToDefaults();
            recreateMenu();
        });
        
        menuContent.addView(resetButton);
    }
    
    private void addSectionTitle(String title) {
        TextView sectionTitle = new TextView(context);
        sectionTitle.setText(title);
        sectionTitle.setTextColor(0xFF00FF00);
        sectionTitle.setTextSize(16);
        sectionTitle.setPadding(0, 15, 0, 10);
        sectionTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        menuContent.addView(sectionTitle);
        
        View divider = new View(context);
        divider.setBackgroundColor(0xFF00FF00);
        divider.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 2
        ));
        menuContent.addView(divider);
    }
    
    private void addToggleSwitch(String label, boolean initialState, CompoundButton.OnCheckedChangeListener listener) {
        LinearLayout row = new LinearLayout(context);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, 8, 0, 8);
        row.setGravity(Gravity.CENTER_VERTICAL);
        
        TextView textView = new TextView(context);
        textView.setText(label);
        textView.setTextColor(0xFFCCCCCC);
        textView.setTextSize(14);
        
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f
        );
        textView.setLayoutParams(textParams);
        
        Switch switchView = new Switch(context);
        switchView.setChecked(initialState);
        switchView.setOnCheckedChangeListener(listener);
        
        row.addView(textView);
        row.addView(switchView);
        
        menuContent.addView(row);
    }
    
    private void addSlider(String label, int min, int max, int initialValue, SliderChangeListener listener) {
        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(0, 8, 0, 8);
        
        LinearLayout labelRow = new LinearLayout(context);
        labelRow.setOrientation(LinearLayout.HORIZONTAL);
        
        TextView labelText = new TextView(context);
        labelText.setText(label);
        labelText.setTextColor(0xFFCCCCCC);
        labelText.setTextSize(14);
        
        LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f
        );
        labelText.setLayoutParams(labelParams);
        
        TextView valueText = new TextView(context);
        valueText.setText(String.valueOf(initialValue));
        valueText.setTextColor(0xFF00FFFF);
        valueText.setTextSize(14);
        
        labelRow.addView(labelText);
        labelRow.addView(valueText);
        
        SeekBar seekBar = new SeekBar(context);
        seekBar.setMax(max - min);
        seekBar.setProgress(initialValue - min);
        
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int value = progress + min;
                valueText.setText(String.valueOf(value));
                listener.onProgressChanged(value);
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        
        container.addView(labelRow);
        container.addView(seekBar);
        
        menuContent.addView(container);
    }
    
    private void setupTouchListener() {
        menuContainer.setOnTouchListener(new View.OnTouchListener() {
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
                        if (!isMinimized) {
                            float touchY = event.getY();
                            if (touchY < 100) {
                                params.x = (int) (initialX + (event.getRawX() - initialTouchX));
                                params.y = (int) (initialY + (event.getRawY() - initialTouchY));
                                windowManager.updateViewLayout(menuView, params);
                            }
                        } else {
                            params.x = (int) (initialX + (event.getRawX() - initialTouchX));
                            params.y = (int) (initialY + (event.getRawY() - initialTouchY));
                            windowManager.updateViewLayout(menuView, params);
                        }
                        return true;
                }
                return false;
            }
        });
    }
    
    private void toggleMinimize() {
        isMinimized = !isMinimized;
        if (isMinimized) {
            scrollView.setVisibility(View.GONE);
            minimizeButton.setText("□");
        } else {
            scrollView.setVisibility(View.VISIBLE);
            minimizeButton.setText("_");
        }
    }
    
    public void show() {
        if (menuView != null && !isVisible) {
            menuView.setVisibility(View.VISIBLE);
            isVisible = true;
        }
    }
    
    public void hide() {
        if (menuView != null && isVisible) {
            menuView.setVisibility(View.GONE);
            isVisible = false;
        }
    }
    
    public void destroy() {
        if (menuView != null && windowManager != null) {
            try {
                windowManager.removeView(menuView);
                menuView = null;
            } catch (Exception e) {
                Log.e(TAG, "Error destroying menu: " + e.getMessage(), e);
            }
        }
    }
    
    private void recreateMenu() {
        destroy();
        createMenu();
    }
    
    public boolean isVisible() {
        return isVisible;
    }
    
    private interface SliderChangeListener {
        void onProgressChanged(int progress);
    }
}
