#pragma once
#include "../types.h"
#include <cstdint>

class ESP {
public:
    ESP(uint32_t libunity_base);
    void Render();
    int GetLastPlayerCount() const { return last_player_count_; }
    
private:
    uint32_t libunity_base_;
    int last_player_count_ = 0;
};
