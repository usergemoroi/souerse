package com.example.espapp;

public interface EspCallback {
    void onLog(String message);
    void onPlayerCountChanged(int count);
    void onFpsUpdate(int fps);
}
