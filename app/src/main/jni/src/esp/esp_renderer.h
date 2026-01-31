#pragma once
#include "../types.h"
#include "../render/native_renderer.h"
#include "../sdk/player/player.h"
#include <vector>

class ESPRenderer {
public:
    ESPRenderer();
    
    void renderESP(const std::vector<Player>& players, const Matrix& viewMatrix, 
                   const Vec3& localPos, uint8_t localTeam);
    
    void renderMenu();
    
    void setSettings(const RenderSettings& settings);
    RenderSettings& getSettings() { return renderer_.getSettings(); }
    
private:
    void renderPlayerESP(const Player& player, const Matrix& viewMatrix);
    void renderLines(const Vec2& screenPos, const Vec2& headPos);
    void renderBox(const Vec2& topLeft, const Vec2& bottomRight, uint8_t health);
    void renderHealthBar(const Vec2& topLeft, const Vec2& bottomRight, uint8_t health);
    void renderSkeleton(const Player& player, const Matrix& viewMatrix);
    void renderName(const Vec2& pos, const std::string& name);
    void renderDistance(const Vec2& pos, float distance);
    void renderAimbotIndicator();
    
    NativeRenderer& renderer_;
    Vec3 localPosition_;
    uint8_t localTeam_;
};
