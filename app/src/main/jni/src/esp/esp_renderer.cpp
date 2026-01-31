#include "esp_renderer.h"
#include <cmath>
#include <sstream>
#include <iomanip>

ESPRenderer::ESPRenderer() 
    : renderer_(NativeRenderer::getInstance()), localTeam_(0) {
}

void ESPRenderer::renderESP(const std::vector<Player>& players, const Matrix& viewMatrix, 
                           const Vec3& localPos, uint8_t localTeam) {
    localPosition_ = localPos;
    localTeam_ = localTeam;
    
    RenderSettings& settings = renderer_.getSettings();
    
    renderer_.beginFrame();
    
    if (settings.aimbotIndicator) {
        renderAimbotIndicator();
    }
    
    for (const Player& player : players) {
        if (!player.IsValid()) continue;
        
        uint8_t team = player.GetTeam();
        
        if (!settings.showEnemies && team != localTeam) continue;
        if (!settings.showFriendlies && team == localTeam) continue;
        if (team == localTeam && !settings.wallhack) continue;
        
        renderPlayerESP(player, viewMatrix);
    }
    
    renderer_.endFrame();
}

void ESPRenderer::renderPlayerESP(const Player& player, const Matrix& viewMatrix) {
    Vec3 pos = player.GetPosition();
    if (pos.x == 0 && pos.y == 0 && pos.z == 0) return;
    
    float distance = sqrt(
        pow(pos.x - localPosition_.x, 2) + 
        pow(pos.y - localPosition_.y, 2) + 
        pow(pos.z - localPosition_.z, 2)
    );
    
    RenderSettings& settings = renderer_.getSettings();
    if (distance > settings.maxDistance) return;
    
    Vec3 head = pos + Vec3(0, 1.7f, 0);
    Vec3 feet = pos;
    
    Vec2 screenHead, screenFeet;
    if (!renderer_.worldToScreen(head, viewMatrix, screenHead)) return;
    if (!renderer_.worldToScreen(feet, viewMatrix, screenFeet)) return;
    
    float boxHeight = screenFeet.y - screenHead.y;
    float boxWidth = boxHeight * 0.5f;
    
    Vec2 topLeft(screenHead.x - boxWidth / 2, screenHead.y);
    Vec2 bottomRight(screenHead.x + boxWidth / 2, screenFeet.y);
    
    uint8_t health = (uint8_t)player.GetHealth();
    
    if (settings.espLines) {
        Vec2 screenCenter(renderer_.getScreenWidth() / 2.0f, renderer_.getScreenHeight());
        renderLines(screenCenter, screenFeet);
    }
    
    if (settings.espBox) {
        renderBox(topLeft, bottomRight, health);
    }
    
    if (settings.espHealthBars) {
        renderHealthBar(topLeft, bottomRight, health);
    }
    
    if (settings.espSkeleton) {
        renderSkeleton(player, viewMatrix);
    }
    
    if (settings.espNames) {
        std::string name = player.GetName();
        if (!name.empty()) {
            renderName(Vec2(screenHead.x, screenHead.y - 20), name);
        }
    }
    
    if (settings.espDistance) {
        renderDistance(Vec2(screenFeet.x, screenFeet.y + 5), distance);
    }
}

void ESPRenderer::renderLines(const Vec2& screenPos, const Vec2& feetPos) {
    RenderSettings& settings = renderer_.getSettings();
    renderer_.drawLine(screenPos, feetPos, settings.lineColor, settings.lineThickness);
}

void ESPRenderer::renderBox(const Vec2& topLeft, const Vec2& bottomRight, uint8_t health) {
    RenderSettings& settings = renderer_.getSettings();
    
    Color boxColor = settings.boxColor;
    if (health < 30) {
        boxColor = Color(1.0f, 0.0f, 0.0f, 1.0f);
    } else if (health < 60) {
        boxColor = Color(1.0f, 0.5f, 0.0f, 1.0f);
    }
    
    renderer_.drawRect(topLeft, bottomRight, boxColor, settings.boxThickness);
    
    float cornerSize = 10.0f;
    
    renderer_.drawLine(
        Vec2(topLeft.x, topLeft.y), 
        Vec2(topLeft.x + cornerSize, topLeft.y), 
        Color(0.0f, 1.0f, 1.0f, 1.0f), 
        settings.boxThickness + 1
    );
    renderer_.drawLine(
        Vec2(topLeft.x, topLeft.y), 
        Vec2(topLeft.x, topLeft.y + cornerSize), 
        Color(0.0f, 1.0f, 1.0f, 1.0f), 
        settings.boxThickness + 1
    );
    
    renderer_.drawLine(
        Vec2(bottomRight.x, topLeft.y), 
        Vec2(bottomRight.x - cornerSize, topLeft.y), 
        Color(0.0f, 1.0f, 1.0f, 1.0f), 
        settings.boxThickness + 1
    );
    renderer_.drawLine(
        Vec2(bottomRight.x, topLeft.y), 
        Vec2(bottomRight.x, topLeft.y + cornerSize), 
        Color(0.0f, 1.0f, 1.0f, 1.0f), 
        settings.boxThickness + 1
    );
    
    renderer_.drawLine(
        Vec2(topLeft.x, bottomRight.y), 
        Vec2(topLeft.x + cornerSize, bottomRight.y), 
        Color(0.0f, 1.0f, 1.0f, 1.0f), 
        settings.boxThickness + 1
    );
    renderer_.drawLine(
        Vec2(topLeft.x, bottomRight.y), 
        Vec2(topLeft.x, bottomRight.y - cornerSize), 
        Color(0.0f, 1.0f, 1.0f, 1.0f), 
        settings.boxThickness + 1
    );
    
    renderer_.drawLine(
        Vec2(bottomRight.x, bottomRight.y), 
        Vec2(bottomRight.x - cornerSize, bottomRight.y), 
        Color(0.0f, 1.0f, 1.0f, 1.0f), 
        settings.boxThickness + 1
    );
    renderer_.drawLine(
        Vec2(bottomRight.x, bottomRight.y), 
        Vec2(bottomRight.x, bottomRight.y - cornerSize), 
        Color(0.0f, 1.0f, 1.0f, 1.0f), 
        settings.boxThickness + 1
    );
}

void ESPRenderer::renderHealthBar(const Vec2& topLeft, const Vec2& bottomRight, uint8_t health) {
    RenderSettings& settings = renderer_.getSettings();
    
    float barWidth = 4.0f;
    float barX = topLeft.x - barWidth - 3.0f;
    float barHeight = bottomRight.y - topLeft.y;
    float healthPercent = health / 100.0f;
    
    renderer_.drawFilledRect(
        Vec2(barX, topLeft.y),
        Vec2(barX + barWidth, bottomRight.y),
        Color(0.2f, 0.2f, 0.2f, 0.8f)
    );
    
    Color healthColor;
    if (health > 60) {
        healthColor = Color(0.0f, 1.0f, 0.0f, 1.0f);
    } else if (health > 30) {
        healthColor = Color(1.0f, 0.5f, 0.0f, 1.0f);
    } else {
        healthColor = Color(1.0f, 0.0f, 0.0f, 1.0f);
    }
    
    float healthBarHeight = barHeight * healthPercent;
    renderer_.drawFilledRect(
        Vec2(barX, bottomRight.y - healthBarHeight),
        Vec2(barX + barWidth, bottomRight.y),
        healthColor
    );
}

void ESPRenderer::renderSkeleton(const Player& player, const Matrix& viewMatrix) {
    RenderSettings& settings = renderer_.getSettings();
    
    Vec3 pos = player.GetPosition();
    Vec3 head = pos + Vec3(0, 1.7f, 0);
    Vec3 neck = pos + Vec3(0, 1.5f, 0);
    Vec3 chest = pos + Vec3(0, 1.0f, 0);
    Vec3 waist = pos + Vec3(0, 0.6f, 0);
    Vec3 leftFoot = pos + Vec3(-0.2f, 0, 0);
    Vec3 rightFoot = pos + Vec3(0.2f, 0, 0);
    Vec3 leftShoulder = neck + Vec3(-0.3f, -0.1f, 0);
    Vec3 rightShoulder = neck + Vec3(0.3f, -0.1f, 0);
    Vec3 leftHand = leftShoulder + Vec3(-0.3f, -0.5f, 0);
    Vec3 rightHand = rightShoulder + Vec3(0.3f, -0.5f, 0);
    
    Vec2 screenPoints[10];
    Vec3 worldPoints[] = {head, neck, chest, waist, leftShoulder, rightShoulder, 
                          leftHand, rightHand, leftFoot, rightFoot};
    
    for (int i = 0; i < 10; i++) {
        if (!renderer_.worldToScreen(worldPoints[i], viewMatrix, screenPoints[i])) {
            return;
        }
    }
    
    renderer_.drawLine(screenPoints[0], screenPoints[1], settings.skeletonColor, settings.lineThickness);
    renderer_.drawLine(screenPoints[1], screenPoints[2], settings.skeletonColor, settings.lineThickness);
    renderer_.drawLine(screenPoints[2], screenPoints[3], settings.skeletonColor, settings.lineThickness);
    
    renderer_.drawLine(screenPoints[1], screenPoints[4], settings.skeletonColor, settings.lineThickness);
    renderer_.drawLine(screenPoints[1], screenPoints[5], settings.skeletonColor, settings.lineThickness);
    
    renderer_.drawLine(screenPoints[4], screenPoints[6], settings.skeletonColor, settings.lineThickness);
    renderer_.drawLine(screenPoints[5], screenPoints[7], settings.skeletonColor, settings.lineThickness);
    
    renderer_.drawLine(screenPoints[3], screenPoints[8], settings.skeletonColor, settings.lineThickness);
    renderer_.drawLine(screenPoints[3], screenPoints[9], settings.skeletonColor, settings.lineThickness);
}

void ESPRenderer::renderName(const Vec2& pos, const std::string& name) {
    RenderSettings& settings = renderer_.getSettings();
    // Text rendering would go here
    // For now, it's a placeholder since we don't have font rendering yet
}

void ESPRenderer::renderDistance(const Vec2& pos, float distance) {
    RenderSettings& settings = renderer_.getSettings();
    // Text rendering would go here
    std::stringstream ss;
    ss << std::fixed << std::setprecision(1) << distance << "m";
}

void ESPRenderer::renderAimbotIndicator() {
    RenderSettings& settings = renderer_.getSettings();
    
    float centerX = renderer_.getScreenWidth() / 2.0f;
    float centerY = renderer_.getScreenHeight() / 2.0f;
    float size = 20.0f;
    
    renderer_.drawCircle(Vec2(centerX, centerY), size, settings.aimbotColor, 2.0f);
    
    renderer_.drawLine(
        Vec2(centerX - size - 5, centerY),
        Vec2(centerX - size + 5, centerY),
        settings.aimbotColor, 2.0f
    );
    renderer_.drawLine(
        Vec2(centerX + size - 5, centerY),
        Vec2(centerX + size + 5, centerY),
        settings.aimbotColor, 2.0f
    );
    renderer_.drawLine(
        Vec2(centerX, centerY - size - 5),
        Vec2(centerX, centerY - size + 5),
        settings.aimbotColor, 2.0f
    );
    renderer_.drawLine(
        Vec2(centerX, centerY + size - 5),
        Vec2(centerX, centerY + size + 5),
        settings.aimbotColor, 2.0f
    );
}

void ESPRenderer::renderMenu() {
    // Native menu rendering would go here
    // This would implement a full ImGui-style menu
    // For now, we'll keep menu control in Java for simplicity
}

void ESPRenderer::setSettings(const RenderSettings& settings) {
    renderer_.setSettings(settings);
}
