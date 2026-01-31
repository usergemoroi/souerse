package com.example.espapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

public class EspSettings {
    private static final String PREFS_NAME = "ESP_SETTINGS";
    private static EspSettings instance;
    
    private SharedPreferences prefs;
    
    public boolean espLinesEnabled = true;
    public boolean espBoxEnabled = true;
    public boolean espHealthEnabled = true;
    public boolean espSkeletonEnabled = false;
    public boolean espNamesEnabled = true;
    public boolean espDistanceEnabled = true;
    public boolean espAimbotIndicatorEnabled = false;
    public boolean wallhackEnabled = false;
    
    public int espLineColor = Color.rgb(0, 255, 255);
    public int espBoxColor = Color.rgb(255, 0, 255);
    public int espHealthBarColor = Color.rgb(0, 255, 0);
    public int espSkeletonColor = Color.rgb(255, 255, 0);
    public int espNameColor = Color.rgb(255, 255, 255);
    public int espDistanceColor = Color.rgb(200, 200, 200);
    public int espAimbotColor = Color.rgb(255, 0, 0);
    
    public float espLineThickness = 2.5f;
    public float espBoxThickness = 2.5f;
    public float espHealthBarThickness = 3.0f;
    public float espSkeletonThickness = 2.0f;
    
    public int espTextSize = 14;
    public float espOpacity = 0.9f;
    
    public boolean showFriendlyPlayers = false;
    public boolean showEnemyPlayers = true;
    
    public float maxRenderDistance = 500.0f;
    
    private EspSettings(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        loadSettings();
    }
    
    public static synchronized EspSettings getInstance(Context context) {
        if (instance == null) {
            instance = new EspSettings(context.getApplicationContext());
        }
        return instance;
    }
    
    public void loadSettings() {
        espLinesEnabled = prefs.getBoolean("espLinesEnabled", true);
        espBoxEnabled = prefs.getBoolean("espBoxEnabled", true);
        espHealthEnabled = prefs.getBoolean("espHealthEnabled", true);
        espSkeletonEnabled = prefs.getBoolean("espSkeletonEnabled", false);
        espNamesEnabled = prefs.getBoolean("espNamesEnabled", true);
        espDistanceEnabled = prefs.getBoolean("espDistanceEnabled", true);
        espAimbotIndicatorEnabled = prefs.getBoolean("espAimbotIndicatorEnabled", false);
        wallhackEnabled = prefs.getBoolean("wallhackEnabled", false);
        
        espLineColor = prefs.getInt("espLineColor", Color.rgb(0, 255, 255));
        espBoxColor = prefs.getInt("espBoxColor", Color.rgb(255, 0, 255));
        espHealthBarColor = prefs.getInt("espHealthBarColor", Color.rgb(0, 255, 0));
        espSkeletonColor = prefs.getInt("espSkeletonColor", Color.rgb(255, 255, 0));
        espNameColor = prefs.getInt("espNameColor", Color.rgb(255, 255, 255));
        espDistanceColor = prefs.getInt("espDistanceColor", Color.rgb(200, 200, 200));
        espAimbotColor = prefs.getInt("espAimbotColor", Color.rgb(255, 0, 0));
        
        espLineThickness = prefs.getFloat("espLineThickness", 2.5f);
        espBoxThickness = prefs.getFloat("espBoxThickness", 2.5f);
        espHealthBarThickness = prefs.getFloat("espHealthBarThickness", 3.0f);
        espSkeletonThickness = prefs.getFloat("espSkeletonThickness", 2.0f);
        
        espTextSize = prefs.getInt("espTextSize", 14);
        espOpacity = prefs.getFloat("espOpacity", 0.9f);
        
        showFriendlyPlayers = prefs.getBoolean("showFriendlyPlayers", false);
        showEnemyPlayers = prefs.getBoolean("showEnemyPlayers", true);
        
        maxRenderDistance = prefs.getFloat("maxRenderDistance", 500.0f);
    }
    
    public void saveSettings() {
        SharedPreferences.Editor editor = prefs.edit();
        
        editor.putBoolean("espLinesEnabled", espLinesEnabled);
        editor.putBoolean("espBoxEnabled", espBoxEnabled);
        editor.putBoolean("espHealthEnabled", espHealthEnabled);
        editor.putBoolean("espSkeletonEnabled", espSkeletonEnabled);
        editor.putBoolean("espNamesEnabled", espNamesEnabled);
        editor.putBoolean("espDistanceEnabled", espDistanceEnabled);
        editor.putBoolean("espAimbotIndicatorEnabled", espAimbotIndicatorEnabled);
        editor.putBoolean("wallhackEnabled", wallhackEnabled);
        
        editor.putInt("espLineColor", espLineColor);
        editor.putInt("espBoxColor", espBoxColor);
        editor.putInt("espHealthBarColor", espHealthBarColor);
        editor.putInt("espSkeletonColor", espSkeletonColor);
        editor.putInt("espNameColor", espNameColor);
        editor.putInt("espDistanceColor", espDistanceColor);
        editor.putInt("espAimbotColor", espAimbotColor);
        
        editor.putFloat("espLineThickness", espLineThickness);
        editor.putFloat("espBoxThickness", espBoxThickness);
        editor.putFloat("espHealthBarThickness", espHealthBarThickness);
        editor.putFloat("espSkeletonThickness", espSkeletonThickness);
        
        editor.putInt("espTextSize", espTextSize);
        editor.putFloat("espOpacity", espOpacity);
        
        editor.putBoolean("showFriendlyPlayers", showFriendlyPlayers);
        editor.putBoolean("showEnemyPlayers", showEnemyPlayers);
        
        editor.putFloat("maxRenderDistance", maxRenderDistance);
        
        editor.apply();
    }
    
    public void resetToDefaults() {
        espLinesEnabled = true;
        espBoxEnabled = true;
        espHealthEnabled = true;
        espSkeletonEnabled = false;
        espNamesEnabled = true;
        espDistanceEnabled = true;
        espAimbotIndicatorEnabled = false;
        wallhackEnabled = false;
        
        espLineColor = Color.rgb(0, 255, 255);
        espBoxColor = Color.rgb(255, 0, 255);
        espHealthBarColor = Color.rgb(0, 255, 0);
        espSkeletonColor = Color.rgb(255, 255, 0);
        espNameColor = Color.rgb(255, 255, 255);
        espDistanceColor = Color.rgb(200, 200, 200);
        espAimbotColor = Color.rgb(255, 0, 0);
        
        espLineThickness = 2.5f;
        espBoxThickness = 2.5f;
        espHealthBarThickness = 3.0f;
        espSkeletonThickness = 2.0f;
        
        espTextSize = 14;
        espOpacity = 0.9f;
        
        showFriendlyPlayers = false;
        showEnemyPlayers = true;
        
        maxRenderDistance = 500.0f;
        
        saveSettings();
    }
}
