package com.example.espapp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

public class LogBuffer {
    private static final int MAX_LINES = 50;
    private final LinkedList<String> logs = new LinkedList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

    public synchronized void addLog(String message) {
        String timestamp = dateFormat.format(new Date());
        String logLine = String.format("[%s] %s", timestamp, message);
        
        logs.addLast(logLine);
        
        if (logs.size() > MAX_LINES) {
            logs.removeFirst();
        }
    }

    public synchronized String getAllLogs() {
        StringBuilder sb = new StringBuilder();
        for (String log : logs) {
            sb.append(log).append("\n");
        }
        return sb.toString();
    }

    public synchronized void clear() {
        logs.clear();
    }
}
