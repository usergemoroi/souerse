#pragma once
#include "../types.h"
#include <cstdint>

class ESPRenderer;

class ESP {
public:
    ESP(uint32_t libunity_base);
    ~ESP();
    void Render();
    void RenderNative();
    int GetLastPlayerCount() const { return last_player_count_; }
    
private:
    uint32_t libunity_base_;
    int last_player_count_ = 0;
    ESPRenderer* native_renderer_ = nullptr;
};
