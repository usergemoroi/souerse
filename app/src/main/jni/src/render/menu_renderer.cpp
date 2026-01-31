#include "menu_renderer.h"
#include <cmath>
#include <android/log.h>

#define LOG_TAG "MENU_RENDERER"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

MenuRenderer::MenuRenderer() 
    : screenWidth_(1920), screenHeight_(1080),
      visible_(true), minimized_(false), isDragging_(false),
      titleBarHeight_(80.0f), sectionSpacing_(20.0f), animTime_(0.0f),
      settings_(nullptr) {
    
    menuPosition_ = Vec2(50, 50);
    menuSize_ = Vec2(500, 800);
    
    // Initialize default values
    espLinesEnabled_ = true;
    espBoxEnabled_ = true;
    espHealthEnabled_ = true;
    espSkeletonEnabled_ = true;
    espNamesEnabled_ = true;
    espDistanceEnabled_ = true;
    espAimbotEnabled_ = true;
    wallhackEnabled_ = false;
    showEnemies_ = true;
    showFriendlies_ = false;
    
    lineThickness_ = 2.5f;
    boxThickness_ = 2.5f;
    textSize_ = 14.0f;
    opacity_ = 0.9f;
    maxDistance_ = 500.0f;
}

MenuRenderer::~MenuRenderer() {
}

void MenuRenderer::init(int screenWidth, int screenHeight) {
    screenWidth_ = screenWidth;
    screenHeight_ = screenHeight;
    setupMenu();
    LOGD("MenuRenderer initialized: %dx%d", screenWidth, screenHeight);
}

void MenuRenderer::setupMenu() {
    sections_.clear();
    
    // ESP Features Section
    MenuSection espSection;
    espSection.title = "ESP FEATURES";
    espSection.position = Vec2(20, titleBarHeight_ + 20);
    espSection.scrollOffset = 0;
    
    float yOffset = 0;
    float buttonHeight = 50.0f;
    float buttonSpacing = 10.0f;
    
    // Add toggle buttons
    MenuButton linesBtn;
    linesBtn.position = Vec2(0, yOffset);
    linesBtn.size = Vec2(menuSize_.x - 40, buttonHeight);
    linesBtn.label = "ESP Lines";
    linesBtn.valuePtr = &espLinesEnabled_;
    linesBtn.isToggle = true;
    linesBtn.animProgress = 0.0f;
    espSection.buttons.push_back(linesBtn);
    yOffset += buttonHeight + buttonSpacing;
    
    MenuButton boxBtn;
    boxBtn.position = Vec2(0, yOffset);
    boxBtn.size = Vec2(menuSize_.x - 40, buttonHeight);
    boxBtn.label = "ESP Box";
    boxBtn.valuePtr = &espBoxEnabled_;
    boxBtn.isToggle = true;
    boxBtn.animProgress = 0.0f;
    espSection.buttons.push_back(boxBtn);
    yOffset += buttonHeight + buttonSpacing;
    
    MenuButton healthBtn;
    healthBtn.position = Vec2(0, yOffset);
    healthBtn.size = Vec2(menuSize_.x - 40, buttonHeight);
    healthBtn.label = "ESP Health Bars";
    healthBtn.valuePtr = &espHealthEnabled_;
    healthBtn.isToggle = true;
    healthBtn.animProgress = 0.0f;
    espSection.buttons.push_back(healthBtn);
    yOffset += buttonHeight + buttonSpacing;
    
    MenuButton skeletonBtn;
    skeletonBtn.position = Vec2(0, yOffset);
    skeletonBtn.size = Vec2(menuSize_.x - 40, buttonHeight);
    skeletonBtn.label = "ESP Skeleton";
    skeletonBtn.valuePtr = &espSkeletonEnabled_;
    skeletonBtn.isToggle = true;
    skeletonBtn.animProgress = 0.0f;
    espSection.buttons.push_back(skeletonBtn);
    yOffset += buttonHeight + buttonSpacing;
    
    MenuButton namesBtn;
    namesBtn.position = Vec2(0, yOffset);
    namesBtn.size = Vec2(menuSize_.x - 40, buttonHeight);
    namesBtn.label = "ESP Names";
    namesBtn.valuePtr = &espNamesEnabled_;
    namesBtn.isToggle = true;
    namesBtn.animProgress = 0.0f;
    espSection.buttons.push_back(namesBtn);
    yOffset += buttonHeight + buttonSpacing;
    
    MenuButton distanceBtn;
    distanceBtn.position = Vec2(0, yOffset);
    distanceBtn.size = Vec2(menuSize_.x - 40, buttonHeight);
    distanceBtn.label = "ESP Distance";
    distanceBtn.valuePtr = &espDistanceEnabled_;
    distanceBtn.isToggle = true;
    distanceBtn.animProgress = 0.0f;
    espSection.buttons.push_back(distanceBtn);
    yOffset += buttonHeight + buttonSpacing;
    
    MenuButton aimbotBtn;
    aimbotBtn.position = Vec2(0, yOffset);
    aimbotBtn.size = Vec2(menuSize_.x - 40, buttonHeight);
    aimbotBtn.label = "Aimbot Indicator";
    aimbotBtn.valuePtr = &espAimbotEnabled_;
    aimbotBtn.isToggle = true;
    aimbotBtn.animProgress = 0.0f;
    espSection.buttons.push_back(aimbotBtn);
    yOffset += buttonHeight + buttonSpacing;
    
    MenuButton wallhackBtn;
    wallhackBtn.position = Vec2(0, yOffset);
    wallhackBtn.size = Vec2(menuSize_.x - 40, buttonHeight);
    wallhackBtn.label = "Wallhack Mode";
    wallhackBtn.valuePtr = &wallhackEnabled_;
    wallhackBtn.isToggle = true;
    wallhackBtn.animProgress = 0.0f;
    espSection.buttons.push_back(wallhackBtn);
    yOffset += buttonHeight + buttonSpacing;
    
    sections_.push_back(espSection);
    
    // Settings Section
    MenuSection settingsSection;
    settingsSection.title = "SETTINGS";
    settingsSection.position = Vec2(20, espSection.position.y + yOffset + 60);
    settingsSection.scrollOffset = 0;
    
    yOffset = 0;
    float sliderHeight = 60.0f;
    
    MenuSlider lineThickSlider;
    lineThickSlider.position = Vec2(0, yOffset);
    lineThickSlider.size = Vec2(menuSize_.x - 40, sliderHeight);
    lineThickSlider.label = "Line Thickness";
    lineThickSlider.valuePtr = &lineThickness_;
    lineThickSlider.minValue = 1.0f;
    lineThickSlider.maxValue = 10.0f;
    lineThickSlider.isDragging = false;
    lineThickSlider.animProgress = 0.0f;
    settingsSection.sliders.push_back(lineThickSlider);
    yOffset += sliderHeight + buttonSpacing;
    
    MenuSlider boxThickSlider;
    boxThickSlider.position = Vec2(0, yOffset);
    boxThickSlider.size = Vec2(menuSize_.x - 40, sliderHeight);
    boxThickSlider.label = "Box Thickness";
    boxThickSlider.valuePtr = &boxThickness_;
    boxThickSlider.minValue = 1.0f;
    boxThickSlider.maxValue = 10.0f;
    boxThickSlider.isDragging = false;
    boxThickSlider.animProgress = 0.0f;
    settingsSection.sliders.push_back(boxThickSlider);
    yOffset += sliderHeight + buttonSpacing;
    
    MenuSlider opacitySlider;
    opacitySlider.position = Vec2(0, yOffset);
    opacitySlider.size = Vec2(menuSize_.x - 40, sliderHeight);
    opacitySlider.label = "Opacity";
    opacitySlider.valuePtr = &opacity_;
    opacitySlider.minValue = 0.1f;
    opacitySlider.maxValue = 1.0f;
    opacitySlider.isDragging = false;
    opacitySlider.animProgress = 0.0f;
    settingsSection.sliders.push_back(opacitySlider);
    yOffset += sliderHeight + buttonSpacing;
    
    MenuSlider maxDistSlider;
    maxDistSlider.position = Vec2(0, yOffset);
    maxDistSlider.size = Vec2(menuSize_.x - 40, sliderHeight);
    maxDistSlider.label = "Max Distance";
    maxDistSlider.valuePtr = &maxDistance_;
    maxDistSlider.minValue = 50.0f;
    maxDistSlider.maxValue = 1000.0f;
    maxDistSlider.isDragging = false;
    maxDistSlider.animProgress = 0.0f;
    settingsSection.sliders.push_back(maxDistSlider);
    yOffset += sliderHeight + buttonSpacing;
    
    sections_.push_back(settingsSection);
    
    // Filters Section
    MenuSection filtersSection;
    filtersSection.title = "FILTERS";
    filtersSection.position = Vec2(20, settingsSection.position.y + yOffset + 60);
    filtersSection.scrollOffset = 0;
    
    yOffset = 0;
    
    MenuButton enemiesBtn;
    enemiesBtn.position = Vec2(0, yOffset);
    enemiesBtn.size = Vec2(menuSize_.x - 40, buttonHeight);
    enemiesBtn.label = "Show Enemies";
    enemiesBtn.valuePtr = &showEnemies_;
    enemiesBtn.isToggle = true;
    enemiesBtn.animProgress = 0.0f;
    filtersSection.buttons.push_back(enemiesBtn);
    yOffset += buttonHeight + buttonSpacing;
    
    MenuButton friendliesBtn;
    friendliesBtn.position = Vec2(0, yOffset);
    friendliesBtn.size = Vec2(menuSize_.x - 40, buttonHeight);
    friendliesBtn.label = "Show Friendlies";
    friendliesBtn.valuePtr = &showFriendlies_;
    friendliesBtn.isToggle = true;
    friendliesBtn.animProgress = 0.0f;
    filtersSection.buttons.push_back(friendliesBtn);
    yOffset += buttonHeight + buttonSpacing;
    
    sections_.push_back(filtersSection);
}

void MenuRenderer::render() {
    if (!visible_) return;
    
    updateAnimations();
    
    NativeRenderer& renderer = NativeRenderer::getInstance();
    
    // Render background with gradient
    renderBackground();
    
    // Render title bar
    renderTitleBar();
    
    if (!minimized_) {
        // Render sections
        renderSections();
    }
    
    // Sync settings to native renderer
    if (settings_) {
        settings_->espLines = espLinesEnabled_;
        settings_->espBox = espBoxEnabled_;
        settings_->espHealthBars = espHealthEnabled_;
        settings_->espSkeleton = espSkeletonEnabled_;
        settings_->espNames = espNamesEnabled_;
        settings_->espDistance = espDistanceEnabled_;
        settings_->aimbotIndicator = espAimbotEnabled_;
        settings_->wallhack = wallhackEnabled_;
        settings_->showEnemies = showEnemies_;
        settings_->showFriendlies = showFriendlies_;
        settings_->lineThickness = lineThickness_;
        settings_->boxThickness = boxThickness_;
        settings_->opacity = opacity_;
        settings_->maxDistance = maxDistance_;
    }
}

void MenuRenderer::renderBackground() {
    NativeRenderer& renderer = NativeRenderer::getInstance();
    
    // Semi-transparent dark background with modern gradient
    Vec2 topLeft = menuPosition_;
    Vec2 bottomRight = Vec2(menuPosition_.x + menuSize_.x, 
                            menuPosition_.y + (minimized_ ? titleBarHeight_ : menuSize_.y));
    
    // Main background
    Color bgColor1(0.05f, 0.05f, 0.08f, 0.95f);
    Color bgColor2(0.08f, 0.08f, 0.12f, 0.95f);
    drawGradientRect(topLeft, bottomRight, bgColor1, bgColor2);
    
    // Border with neon effect
    Color borderColor(0.0f, 0.8f, 1.0f, 0.9f);
    renderer.drawRect(topLeft, bottomRight, borderColor, 3.0f);
    
    // Inner glow effect
    Vec2 innerTopLeft = Vec2(topLeft.x + 2, topLeft.y + 2);
    Vec2 innerBottomRight = Vec2(bottomRight.x - 2, bottomRight.y - 2);
    Color glowColor(0.0f, 0.6f, 0.9f, 0.3f);
    renderer.drawRect(innerTopLeft, innerBottomRight, glowColor, 1.0f);
}

void MenuRenderer::renderTitleBar() {
    NativeRenderer& renderer = NativeRenderer::getInstance();
    
    Vec2 titleBarTopLeft = menuPosition_;
    Vec2 titleBarBottomRight = Vec2(menuPosition_.x + menuSize_.x, 
                                     menuPosition_.y + titleBarHeight_);
    
    // Title bar gradient (cyan to blue)
    Color titleColor1(0.0f, 0.8f, 1.0f, 0.4f);
    Color titleColor2(0.0f, 0.4f, 0.8f, 0.4f);
    drawGradientRect(titleBarTopLeft, titleBarBottomRight, titleColor1, titleColor2);
    
    // Title bar border
    Color titleBorderColor(0.0f, 1.0f, 1.0f, 0.8f);
    renderer.drawRect(titleBarTopLeft, titleBarBottomRight, titleBorderColor, 2.0f);
    
    // Close button (X)
    float btnSize = 40.0f;
    float btnPadding = 20.0f;
    Vec2 closeBtnPos = Vec2(menuPosition_.x + menuSize_.x - btnPadding - btnSize, 
                            menuPosition_.y + (titleBarHeight_ - btnSize) / 2);
    Vec2 closeBtnEnd = Vec2(closeBtnPos.x + btnSize, closeBtnPos.y + btnSize);
    
    Color closeBtnColor(0.9f, 0.1f, 0.1f, 0.8f);
    renderer.drawFilledRect(closeBtnPos, closeBtnEnd, closeBtnColor);
    renderer.drawRect(closeBtnPos, closeBtnEnd, Color(1.0f, 0.0f, 0.0f, 1.0f), 2.0f);
    
    // Draw X
    renderer.drawLine(Vec2(closeBtnPos.x + 10, closeBtnPos.y + 10),
                     Vec2(closeBtnEnd.x - 10, closeBtnEnd.y - 10),
                     Color(1.0f, 1.0f, 1.0f, 1.0f), 3.0f);
    renderer.drawLine(Vec2(closeBtnEnd.x - 10, closeBtnPos.y + 10),
                     Vec2(closeBtnPos.x + 10, closeBtnEnd.y - 10),
                     Color(1.0f, 1.0f, 1.0f, 1.0f), 3.0f);
    
    // Minimize button (_)
    Vec2 minBtnPos = Vec2(closeBtnPos.x - btnSize - 10, closeBtnPos.y);
    Vec2 minBtnEnd = Vec2(minBtnPos.x + btnSize, minBtnPos.y + btnSize);
    
    Color minBtnColor(0.9f, 0.8f, 0.1f, 0.8f);
    renderer.drawFilledRect(minBtnPos, minBtnEnd, minBtnColor);
    renderer.drawRect(minBtnPos, minBtnEnd, Color(1.0f, 1.0f, 0.0f, 1.0f), 2.0f);
    
    // Draw minimize line
    renderer.drawLine(Vec2(minBtnPos.x + 10, minBtnPos.y + btnSize - 15),
                     Vec2(minBtnEnd.x - 10, minBtnPos.y + btnSize - 15),
                     Color(1.0f, 1.0f, 1.0f, 1.0f), 3.0f);
}

void MenuRenderer::renderSections() {
    NativeRenderer& renderer = NativeRenderer::getInstance();
    
    float currentY = menuPosition_.y + titleBarHeight_ + 20;
    
    for (auto& section : sections_) {
        // Section title
        Vec2 sectionTitlePos = Vec2(menuPosition_.x + 20, currentY);
        
        // Section title background
        Vec2 titleBgTopLeft = Vec2(menuPosition_.x + 10, currentY - 5);
        Vec2 titleBgBottomRight = Vec2(menuPosition_.x + menuSize_.x - 10, currentY + 30);
        Color sectionBgColor(0.0f, 0.8f, 0.4f, 0.3f);
        renderer.drawFilledRect(titleBgTopLeft, titleBgBottomRight, sectionBgColor);
        
        Color sectionTitleColor(0.0f, 1.0f, 0.5f, 1.0f);
        renderer.drawRect(titleBgTopLeft, titleBgBottomRight, sectionTitleColor, 2.0f);
        
        currentY += 50;
        
        // Render buttons
        for (auto& button : section.buttons) {
            Vec2 btnPos = Vec2(menuPosition_.x + section.position.x, currentY + button.position.y);
            
            Vec2 btnTopLeft = btnPos;
            Vec2 btnBottomRight = Vec2(btnPos.x + button.size.x, btnPos.y + button.size.y);
            
            // Button background based on state
            Color btnColor;
            if (*button.valuePtr) {
                // Active state - gradient from green to cyan
                Color activeColor1(0.0f, 0.7f, 0.3f, 0.7f);
                Color activeColor2(0.0f, 0.5f, 0.7f, 0.7f);
                drawGradientRect(btnTopLeft, btnBottomRight, activeColor1, activeColor2);
                btnColor = Color(0.0f, 1.0f, 0.5f, 1.0f);
            } else {
                // Inactive state
                Color inactiveColor(0.2f, 0.2f, 0.25f, 0.7f);
                renderer.drawFilledRect(btnTopLeft, btnBottomRight, inactiveColor);
                btnColor = Color(0.5f, 0.5f, 0.5f, 1.0f);
            }
            
            renderer.drawRect(btnTopLeft, btnBottomRight, btnColor, 2.0f);
            
            currentY += button.size.y + 10;
        }
        
        // Render sliders
        for (auto& slider : section.sliders) {
            Vec2 sliderPos = Vec2(menuPosition_.x + section.position.x, currentY + slider.position.y);
            
            // Slider background
            Vec2 sliderBgTopLeft = sliderPos;
            Vec2 sliderBgBottomRight = Vec2(sliderPos.x + slider.size.x, sliderPos.y + slider.size.y);
            Color sliderBgColor(0.15f, 0.15f, 0.2f, 0.8f);
            renderer.drawFilledRect(sliderBgTopLeft, sliderBgBottomRight, sliderBgColor);
            renderer.drawRect(sliderBgTopLeft, sliderBgBottomRight, Color(0.3f, 0.3f, 0.4f, 1.0f), 2.0f);
            
            // Slider track
            float trackY = sliderPos.y + slider.size.y - 20;
            float trackWidth = slider.size.x - 20;
            Vec2 trackStart = Vec2(sliderPos.x + 10, trackY);
            Vec2 trackEnd = Vec2(sliderPos.x + 10 + trackWidth, trackY);
            
            // Track background
            renderer.drawLine(trackStart, trackEnd, Color(0.3f, 0.3f, 0.4f, 1.0f), 4.0f);
            
            // Track fill (progress)
            float progress = (*slider.valuePtr - slider.minValue) / (slider.maxValue - slider.minValue);
            Vec2 fillEnd = Vec2(trackStart.x + trackWidth * progress, trackY);
            Color fillColor = getGradientColor(progress);
            renderer.drawLine(trackStart, fillEnd, fillColor, 4.0f);
            
            // Slider handle
            Vec2 handlePos = Vec2(fillEnd.x, trackY);
            renderer.drawCircle(handlePos, 8.0f, Color(1.0f, 1.0f, 1.0f, 1.0f), 3.0f);
            renderer.drawCircle(handlePos, 6.0f, fillColor, 2.0f);
            
            currentY += slider.size.y + 10;
        }
        
        currentY += 30; // Section spacing
    }
}

void MenuRenderer::renderButton(const MenuButton& button) {
    // Implemented in renderSections for better organization
}

void MenuRenderer::renderSlider(const MenuSlider& slider) {
    // Implemented in renderSections for better organization
}

void MenuRenderer::renderText(const Vec2& pos, const std::string& text, const Color& color, float size) {
    // Text rendering would require font atlas - future implementation
    // For now, UI is visual-only with intuitive design
}

void MenuRenderer::handleTouch(float x, float y, bool isDown) {
    if (!visible_) return;
    
    Vec2 touchPos(x, y);
    
    // Check if touch is in title bar for dragging
    if (isDown) {
        Vec2 titleBarTopLeft = menuPosition_;
        Vec2 titleBarBottomRight = Vec2(menuPosition_.x + menuSize_.x, 
                                        menuPosition_.y + titleBarHeight_);
        
        if (touchPos.x >= titleBarTopLeft.x && touchPos.x <= titleBarBottomRight.x &&
            touchPos.y >= titleBarTopLeft.y && touchPos.y <= titleBarBottomRight.y) {
            
            // Check close button
            float btnSize = 40.0f;
            float btnPadding = 20.0f;
            Vec2 closeBtnPos = Vec2(menuPosition_.x + menuSize_.x - btnPadding - btnSize, 
                                    menuPosition_.y + (titleBarHeight_ - btnSize) / 2);
            Vec2 closeBtnEnd = Vec2(closeBtnPos.x + btnSize, closeBtnPos.y + btnSize);
            
            if (touchPos.x >= closeBtnPos.x && touchPos.x <= closeBtnEnd.x &&
                touchPos.y >= closeBtnPos.y && touchPos.y <= closeBtnEnd.y) {
                visible_ = false;
                return;
            }
            
            // Check minimize button
            Vec2 minBtnPos = Vec2(closeBtnPos.x - btnSize - 10, closeBtnPos.y);
            Vec2 minBtnEnd = Vec2(minBtnPos.x + btnSize, minBtnPos.y + btnSize);
            
            if (touchPos.x >= minBtnPos.x && touchPos.x <= minBtnEnd.x &&
                touchPos.y >= minBtnPos.y && touchPos.y <= minBtnEnd.y) {
                minimized_ = !minimized_;
                return;
            }
            
            // Start dragging
            isDragging_ = true;
            dragStartPos_ = menuPosition_;
            touchStartPos_ = touchPos;
            return;
        }
        
        if (minimized_) return;
        
        // Check buttons
        float currentY = menuPosition_.y + titleBarHeight_ + 20;
        
        for (auto& section : sections_) {
            currentY += 50; // Section header
            
            for (auto& button : section.buttons) {
                Vec2 btnPos = Vec2(menuPosition_.x + section.position.x, currentY + button.position.y);
                Vec2 btnTopLeft = btnPos;
                Vec2 btnBottomRight = Vec2(btnPos.x + button.size.x, btnPos.y + button.size.y);
                
                if (touchPos.x >= btnTopLeft.x && touchPos.x <= btnBottomRight.x &&
                    touchPos.y >= btnTopLeft.y && touchPos.y <= btnBottomRight.y) {
                    *button.valuePtr = !(*button.valuePtr);
                    return;
                }
                
                currentY += button.size.y + 10;
            }
            
            // Check sliders
            for (auto& slider : section.sliders) {
                Vec2 sliderPos = Vec2(menuPosition_.x + section.position.x, currentY + slider.position.y);
                float trackY = sliderPos.y + slider.size.y - 20;
                float trackWidth = slider.size.x - 20;
                Vec2 trackStart = Vec2(sliderPos.x + 10, trackY);
                
                if (touchPos.y >= trackY - 10 && touchPos.y <= trackY + 10 &&
                    touchPos.x >= trackStart.x && touchPos.x <= trackStart.x + trackWidth) {
                    slider.isDragging = true;
                    
                    float progress = (touchPos.x - trackStart.x) / trackWidth;
                    progress = std::max(0.0f, std::min(1.0f, progress));
                    *slider.valuePtr = slider.minValue + progress * (slider.maxValue - slider.minValue);
                    return;
                }
                
                currentY += slider.size.y + 10;
            }
            
            currentY += 30;
        }
    } else {
        // Touch up
        isDragging_ = false;
        
        for (auto& section : sections_) {
            for (auto& slider : section.sliders) {
                slider.isDragging = false;
            }
        }
    }
}

void MenuRenderer::handleMove(float x, float y) {
    if (!visible_) return;
    
    Vec2 touchPos(x, y);
    
    if (isDragging_) {
        Vec2 delta = Vec2(touchPos.x - touchStartPos_.x, touchPos.y - touchStartPos_.y);
        menuPosition_ = Vec2(dragStartPos_.x + delta.x, dragStartPos_.y + delta.y);
        
        // Keep menu on screen
        menuPosition_.x = std::max(0.0f, std::min((float)screenWidth_ - menuSize_.x, menuPosition_.x));
        menuPosition_.y = std::max(0.0f, std::min((float)screenHeight_ - titleBarHeight_, menuPosition_.y));
        return;
    }
    
    // Check sliders being dragged
    float currentY = menuPosition_.y + titleBarHeight_ + 20;
    
    for (auto& section : sections_) {
        currentY += 50; // Section header
        
        for (auto& button : section.buttons) {
            currentY += button.size.y + 10;
        }
        
        for (auto& slider : section.sliders) {
            if (slider.isDragging) {
                Vec2 sliderPos = Vec2(menuPosition_.x + section.position.x, currentY + slider.position.y);
                float trackWidth = slider.size.x - 20;
                Vec2 trackStart = Vec2(sliderPos.x + 10, sliderPos.y + slider.size.y - 20);
                
                float progress = (touchPos.x - trackStart.x) / trackWidth;
                progress = std::max(0.0f, std::min(1.0f, progress));
                *slider.valuePtr = slider.minValue + progress * (slider.maxValue - slider.minValue);
            }
            
            currentY += slider.size.y + 10;
        }
        
        currentY += 30;
    }
}

void MenuRenderer::updateAnimations() {
    animTime_ += 0.016f; // ~60 FPS
    
    // Update button animations
    for (auto& section : sections_) {
        for (auto& button : section.buttons) {
            if (*button.valuePtr) {
                button.animProgress = std::min(1.0f, button.animProgress + 0.1f);
            } else {
                button.animProgress = std::max(0.0f, button.animProgress - 0.1f);
            }
        }
    }
}

void MenuRenderer::setVisible(bool visible) {
    visible_ = visible;
}

void MenuRenderer::setSettings(RenderSettings* settings) {
    settings_ = settings;
    
    // Initialize from settings
    if (settings_) {
        espLinesEnabled_ = settings_->espLines;
        espBoxEnabled_ = settings_->espBox;
        espHealthEnabled_ = settings_->espHealthBars;
        espSkeletonEnabled_ = settings_->espSkeleton;
        espNamesEnabled_ = settings_->espNames;
        espDistanceEnabled_ = settings_->espDistance;
        espAimbotEnabled_ = settings_->aimbotIndicator;
        wallhackEnabled_ = settings_->wallhack;
        showEnemies_ = settings_->showEnemies;
        showFriendlies_ = settings_->showFriendlies;
        lineThickness_ = settings_->lineThickness;
        boxThickness_ = settings_->boxThickness;
        textSize_ = settings_->textSize;
        opacity_ = settings_->opacity;
        maxDistance_ = settings_->maxDistance;
    }
}

Color MenuRenderer::getGradientColor(float progress) {
    // Rainbow gradient based on progress
    if (progress < 0.5f) {
        float t = progress * 2.0f;
        return Color(1.0f - t, t, 0.0f, 1.0f); // Red to Green
    } else {
        float t = (progress - 0.5f) * 2.0f;
        return Color(0.0f, 1.0f - t, t, 1.0f); // Green to Blue
    }
}

void MenuRenderer::drawGradientRect(const Vec2& topLeft, const Vec2& bottomRight, 
                                     const Color& color1, const Color& color2) {
    NativeRenderer& renderer = NativeRenderer::getInstance();
    
    // Simple approximation with multiple horizontal strips
    int strips = 20;
    float stripHeight = (bottomRight.y - topLeft.y) / strips;
    
    for (int i = 0; i < strips; i++) {
        float t = (float)i / strips;
        Color stripColor(
            color1.r + (color2.r - color1.r) * t,
            color1.g + (color2.g - color1.g) * t,
            color1.b + (color2.b - color1.b) * t,
            color1.a + (color2.a - color1.a) * t
        );
        
        Vec2 stripTop = Vec2(topLeft.x, topLeft.y + i * stripHeight);
        Vec2 stripBottom = Vec2(bottomRight.x, topLeft.y + (i + 1) * stripHeight);
        
        renderer.drawFilledRect(stripTop, stripBottom, stripColor);
    }
}

void MenuRenderer::drawRoundedRect(const Vec2& topLeft, const Vec2& bottomRight, 
                                     const Color& color, float radius) {
    // Simplified rounded rect - would need more complex implementation for true rounded corners
    NativeRenderer& renderer = NativeRenderer::getInstance();
    renderer.drawFilledRect(topLeft, bottomRight, color);
}
