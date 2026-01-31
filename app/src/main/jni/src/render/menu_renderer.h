#pragma once
#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>
#include <vector>
#include <string>
#include <cstdint>
#include "../types.h"
#include "native_renderer.h"

struct MenuButton {
    Vec2 position;
    Vec2 size;
    std::string label;
    bool* valuePtr;
    bool isToggle;
    bool isPressed;
    float animProgress;
};

struct MenuSlider {
    Vec2 position;
    Vec2 size;
    std::string label;
    float* valuePtr;
    float minValue;
    float maxValue;
    bool isDragging;
    float animProgress;
};

struct MenuSection {
    std::string title;
    Vec2 position;
    std::vector<MenuButton> buttons;
    std::vector<MenuSlider> sliders;
    float scrollOffset;
};

class MenuRenderer {
public:
    MenuRenderer();
    ~MenuRenderer();
    
    void init(int screenWidth, int screenHeight);
    void render();
    void handleTouch(float x, float y, bool isDown);
    void handleMove(float x, float y);
    
    void setVisible(bool visible);
    bool isVisible() const { return visible_; }
    void toggleVisible() { visible_ = !visible_; }
    
    void setSettings(RenderSettings* settings);
    
private:
    void renderBackground();
    void renderTitleBar();
    void renderSections();
    void renderButton(const MenuButton& button);
    void renderSlider(const MenuSlider& slider);
    void renderText(const Vec2& pos, const std::string& text, const Color& color, float size);
    
    void setupMenu();
    void updateAnimations();
    
    Color getGradientColor(float progress);
    void drawGradientRect(const Vec2& topLeft, const Vec2& bottomRight, const Color& color1, const Color& color2);
    void drawRoundedRect(const Vec2& topLeft, const Vec2& bottomRight, const Color& color, float radius);
    
    int screenWidth_;
    int screenHeight_;
    bool visible_;
    bool minimized_;
    bool isDragging_;
    
    Vec2 menuPosition_;
    Vec2 menuSize_;
    Vec2 dragStartPos_;
    Vec2 touchStartPos_;
    
    float titleBarHeight_;
    float sectionSpacing_;
    float animTime_;
    
    std::vector<MenuSection> sections_;
    RenderSettings* settings_;
    
    // Menu state
    bool espLinesEnabled_;
    bool espBoxEnabled_;
    bool espHealthEnabled_;
    bool espSkeletonEnabled_;
    bool espNamesEnabled_;
    bool espDistanceEnabled_;
    bool espAimbotEnabled_;
    bool wallhackEnabled_;
    bool showEnemies_;
    bool showFriendlies_;
    
    float lineThickness_;
    float boxThickness_;
    float textSize_;
    float opacity_;
    float maxDistance_;
};
