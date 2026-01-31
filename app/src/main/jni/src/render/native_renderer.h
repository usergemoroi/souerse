#pragma once
#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>
#include <cstdint>
#include <vector>
#include <string>
#include "../types.h"

struct Color {
    float r, g, b, a;
    
    Color() : r(1.0f), g(1.0f), b(1.0f), a(1.0f) {}
    Color(float r, float g, float b, float a = 1.0f) : r(r), g(g), b(b), a(a) {}
    Color(uint32_t argb) {
        a = ((argb >> 24) & 0xFF) / 255.0f;
        r = ((argb >> 16) & 0xFF) / 255.0f;
        g = ((argb >> 8) & 0xFF) / 255.0f;
        b = (argb & 0xFF) / 255.0f;
    }
};

struct Vec2 {
    float x, y;
    Vec2() : x(0), y(0) {}
    Vec2(float x, float y) : x(x), y(y) {}
};

struct RenderSettings {
    bool espLines = true;
    bool espBox = true;
    bool espHealthBars = true;
    bool espSkeleton = true;
    bool espNames = true;
    bool espDistance = true;
    bool aimbotIndicator = true;
    bool wallhack = true;
    bool showEnemies = true;
    bool showFriendlies = false;
    
    float lineThickness = 2.5f;
    float boxThickness = 2.5f;
    float textSize = 14.0f;
    float opacity = 0.9f;
    float maxDistance = 500.0f;
    
    Color lineColor = Color(0xFF00FFFF);
    Color boxColor = Color(0xFFFF00FF);
    Color healthBarColor = Color(0xFF00FF00);
    Color skeletonColor = Color(0xFFFFFF00);
    Color nameColor = Color(0xFFFFFFFF);
    Color distanceColor = Color(0xFFCCCCCC);
    Color aimbotColor = Color(0xFFFF0000);
};

class NativeRenderer {
public:
    static NativeRenderer& getInstance();
    
    void init(int screenWidth, int screenHeight);
    void beginFrame();
    void endFrame();
    
    void drawLine(const Vec2& start, const Vec2& end, const Color& color, float thickness);
    void drawRect(const Vec2& topLeft, const Vec2& bottomRight, const Color& color, float thickness);
    void drawFilledRect(const Vec2& topLeft, const Vec2& bottomRight, const Color& color);
    void drawText(const Vec2& pos, const std::string& text, const Color& color, float size);
    void drawCircle(const Vec2& center, float radius, const Color& color, float thickness);
    
    bool worldToScreen(const Vec3& worldPos, const Matrix& viewMatrix, Vec2& screenPos);
    
    void setSettings(const RenderSettings& settings);
    RenderSettings& getSettings() { return settings_; }
    
    int getScreenWidth() const { return screenWidth_; }
    int getScreenHeight() const { return screenHeight_; }
    
private:
    NativeRenderer();
    ~NativeRenderer();
    
    void setupOpenGL();
    void createShaders();
    
    int screenWidth_;
    int screenHeight_;
    RenderSettings settings_;
    
    GLuint shaderProgram_;
    GLint positionLoc_;
    GLint colorLoc_;
    GLint mvpLoc_;
    
    bool initialized_;
};
