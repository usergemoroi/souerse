package com.example.espapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.view.View;

public class EspRenderer extends View {
    private static final String TAG = "ESP_RENDERER";
    
    private EspSettings settings;
    private EspData espData;
    
    private Paint linePaint;
    private Paint boxPaint;
    private Paint healthBarPaint;
    private Paint healthBarBgPaint;
    private Paint skeletonPaint;
    private Paint textPaint;
    private Paint distancePaint;
    private Paint aimbotPaint;
    
    private long lastFrameTime;
    private int frameCount;
    private int currentFps;
    
    public EspRenderer(Context context) {
        super(context);
        init(context);
    }
    
    private void init(Context context) {
        settings = EspSettings.getInstance(context);
        espData = new EspData();
        
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        
        boxPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        boxPaint.setStyle(Paint.Style.STROKE);
        boxPaint.setStrokeCap(Paint.Cap.ROUND);
        
        healthBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        healthBarPaint.setStyle(Paint.Style.FILL);
        
        healthBarBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        healthBarBgPaint.setStyle(Paint.Style.FILL);
        healthBarBgPaint.setColor(0x80000000);
        
        skeletonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        skeletonPaint.setStyle(Paint.Style.STROKE);
        skeletonPaint.setStrokeCap(Paint.Cap.ROUND);
        
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        textPaint.setShadowLayer(2, 1, 1, 0xFF000000);
        
        distancePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        distancePaint.setStyle(Paint.Style.FILL);
        distancePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        distancePaint.setShadowLayer(2, 1, 1, 0xFF000000);
        
        aimbotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        aimbotPaint.setStyle(Paint.Style.STROKE);
        aimbotPaint.setStrokeCap(Paint.Cap.ROUND);
        
        lastFrameTime = System.currentTimeMillis();
        frameCount = 0;
        currentFps = 0;
    }
    
    public void updateEspData(EspData data) {
        this.espData = data;
        postInvalidate();
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        if (espData == null || espData.playerCount == 0) {
            return;
        }
        
        updateFps();
        updatePaints();
        
        for (int i = 0; i < espData.playerCount; i++) {
            EspData.PlayerInfo player = espData.players[i];
            
            if (player == null) continue;
            
            if (!settings.showEnemyPlayers && player.isEnemy) continue;
            if (!settings.showFriendlyPlayers && !player.isEnemy) continue;
            
            if (player.distance > settings.maxRenderDistance) continue;
            
            int alpha = (int) (settings.espOpacity * 255);
            
            if (settings.espLinesEnabled) {
                drawEspLine(canvas, player, alpha);
            }
            
            if (settings.espBoxEnabled) {
                drawEspBox(canvas, player, alpha);
            }
            
            if (settings.espHealthEnabled) {
                drawHealthBar(canvas, player, alpha);
            }
            
            if (settings.espSkeletonEnabled) {
                drawSkeleton(canvas, player, alpha);
            }
            
            if (settings.espNamesEnabled) {
                drawName(canvas, player, alpha);
            }
            
            if (settings.espDistanceEnabled) {
                drawDistance(canvas, player, alpha);
            }
            
            if (settings.espAimbotIndicatorEnabled) {
                drawAimbotIndicator(canvas, player, alpha);
            }
        }
    }
    
    private void updateFps() {
        frameCount++;
        long currentTime = System.currentTimeMillis();
        long deltaTime = currentTime - lastFrameTime;
        
        if (deltaTime >= 1000) {
            currentFps = frameCount;
            frameCount = 0;
            lastFrameTime = currentTime;
        }
    }
    
    private void updatePaints() {
        linePaint.setStrokeWidth(settings.espLineThickness);
        linePaint.setColor(settings.espLineColor);
        
        boxPaint.setStrokeWidth(settings.espBoxThickness);
        boxPaint.setColor(settings.espBoxColor);
        
        healthBarPaint.setStrokeWidth(settings.espHealthBarThickness);
        
        skeletonPaint.setStrokeWidth(settings.espSkeletonThickness);
        skeletonPaint.setColor(settings.espSkeletonColor);
        
        textPaint.setTextSize(settings.espTextSize * 1.2f);
        textPaint.setColor(settings.espNameColor);
        
        distancePaint.setTextSize(settings.espTextSize);
        distancePaint.setColor(settings.espDistanceColor);
        
        aimbotPaint.setStrokeWidth(3.0f);
        aimbotPaint.setColor(settings.espAimbotColor);
    }
    
    private void drawEspLine(Canvas canvas, EspData.PlayerInfo player, int alpha) {
        linePaint.setAlpha(alpha);
        canvas.drawLine(espData.centerX, espData.screenHeight, player.footX, player.footY, linePaint);
    }
    
    private void drawEspBox(Canvas canvas, EspData.PlayerInfo player, int alpha) {
        boxPaint.setAlpha(alpha);
        
        float left = player.boxLeft;
        float top = player.boxTop;
        float right = player.boxRight;
        float bottom = player.boxBottom;
        
        canvas.drawRect(left, top, right, bottom, boxPaint);
        
        float cornerLength = (right - left) * 0.2f;
        
        canvas.drawLine(left, top, left + cornerLength, top, boxPaint);
        canvas.drawLine(left, top, left, top + cornerLength, boxPaint);
        
        canvas.drawLine(right, top, right - cornerLength, top, boxPaint);
        canvas.drawLine(right, top, right, top + cornerLength, boxPaint);
        
        canvas.drawLine(left, bottom, left + cornerLength, bottom, boxPaint);
        canvas.drawLine(left, bottom, left, bottom - cornerLength, boxPaint);
        
        canvas.drawLine(right, bottom, right - cornerLength, bottom, boxPaint);
        canvas.drawLine(right, bottom, right, bottom - cornerLength, boxPaint);
    }
    
    private void drawHealthBar(Canvas canvas, EspData.PlayerInfo player, int alpha) {
        float barWidth = 4;
        float barHeight = player.boxBottom - player.boxTop;
        float barX = player.boxLeft - barWidth - 5;
        float barY = player.boxTop;
        
        healthBarBgPaint.setAlpha(alpha);
        canvas.drawRect(barX, barY, barX + barWidth, barY + barHeight, healthBarBgPaint);
        
        float healthPercent = (float) player.health / player.maxHealth;
        float healthHeight = barHeight * healthPercent;
        
        int healthColor;
        if (healthPercent > 0.6f) {
            healthColor = settings.espHealthBarColor;
        } else if (healthPercent > 0.3f) {
            healthColor = 0xFFFFAA00;
        } else {
            healthColor = 0xFFFF0000;
        }
        
        healthBarPaint.setColor(healthColor);
        healthBarPaint.setAlpha(alpha);
        canvas.drawRect(barX, barY + (barHeight - healthHeight), barX + barWidth, barY + barHeight, healthBarPaint);
    }
    
    private void drawSkeleton(Canvas canvas, EspData.PlayerInfo player, int alpha) {
        skeletonPaint.setAlpha(alpha);
        
        float centerX = (player.boxLeft + player.boxRight) / 2;
        float neckY = player.boxTop + (player.boxBottom - player.boxTop) * 0.15f;
        float chestY = player.boxTop + (player.boxBottom - player.boxTop) * 0.35f;
        float waistY = player.boxTop + (player.boxBottom - player.boxTop) * 0.55f;
        
        canvas.drawLine(player.headX, player.headY, centerX, neckY, skeletonPaint);
        
        canvas.drawLine(centerX, neckY, centerX, chestY, skeletonPaint);
        
        float shoulderWidth = (player.boxRight - player.boxLeft) * 0.45f;
        canvas.drawLine(centerX - shoulderWidth, neckY, centerX + shoulderWidth, neckY, skeletonPaint);
        
        canvas.drawLine(centerX, chestY, centerX, waistY, skeletonPaint);
        
        float armEndY = chestY + (waistY - chestY) * 0.8f;
        canvas.drawLine(centerX - shoulderWidth, neckY, centerX - shoulderWidth * 0.9f, armEndY, skeletonPaint);
        canvas.drawLine(centerX + shoulderWidth, neckY, centerX + shoulderWidth * 0.9f, armEndY, skeletonPaint);
        
        float legWidth = (player.boxRight - player.boxLeft) * 0.2f;
        canvas.drawLine(centerX, waistY, centerX - legWidth, player.footY, skeletonPaint);
        canvas.drawLine(centerX, waistY, centerX + legWidth, player.footY, skeletonPaint);
    }
    
    private void drawName(Canvas canvas, EspData.PlayerInfo player, int alpha) {
        textPaint.setAlpha(alpha);
        
        String name = player.name;
        if (name == null || name.isEmpty()) {
            name = "Unknown";
        }
        
        float textWidth = textPaint.measureText(name);
        float textX = player.boxLeft + (player.boxRight - player.boxLeft) / 2 - textWidth / 2;
        float textY = player.boxTop - 10;
        
        canvas.drawText(name, textX, textY, textPaint);
    }
    
    private void drawDistance(Canvas canvas, EspData.PlayerInfo player, int alpha) {
        distancePaint.setAlpha(alpha);
        
        String distText = String.format("%.1fm", player.distance);
        float textWidth = distancePaint.measureText(distText);
        float textX = player.boxLeft + (player.boxRight - player.boxLeft) / 2 - textWidth / 2;
        float textY = player.boxBottom + 20;
        
        canvas.drawText(distText, textX, textY, distancePaint);
    }
    
    private void drawAimbotIndicator(Canvas canvas, EspData.PlayerInfo player, int alpha) {
        aimbotPaint.setAlpha(alpha);
        
        float centerX = (player.boxLeft + player.boxRight) / 2;
        float centerY = (player.boxTop + player.boxBottom) / 2;
        float radius = 20;
        
        canvas.drawCircle(centerX, centerY, radius, aimbotPaint);
        
        canvas.drawLine(centerX - radius - 5, centerY, centerX - radius, centerY, aimbotPaint);
        canvas.drawLine(centerX + radius, centerY, centerX + radius + 5, centerY, aimbotPaint);
        canvas.drawLine(centerX, centerY - radius - 5, centerX, centerY - radius, aimbotPaint);
        canvas.drawLine(centerX, centerY + radius, centerX, centerY + radius + 5, aimbotPaint);
    }
    
    public int getCurrentFps() {
        return currentFps;
    }
}
