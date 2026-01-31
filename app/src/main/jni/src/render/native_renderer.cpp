#include "native_renderer.h"
#include <cmath>
#include <android/log.h>

#define LOG_TAG "NATIVE_RENDERER"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

static const char* vertexShaderSource = R"(
attribute vec2 aPosition;
uniform mat4 uMVP;
void main() {
    gl_Position = uMVP * vec4(aPosition, 0.0, 1.0);
}
)";

static const char* fragmentShaderSource = R"(
precision mediump float;
uniform vec4 uColor;
void main() {
    gl_FragColor = uColor;
}
)";

NativeRenderer& NativeRenderer::getInstance() {
    static NativeRenderer instance;
    return instance;
}

NativeRenderer::NativeRenderer() 
    : screenWidth_(1920), screenHeight_(1080), 
      shaderProgram_(0), positionLoc_(-1), colorLoc_(-1), mvpLoc_(-1),
      initialized_(false) {
}

NativeRenderer::~NativeRenderer() {
    if (shaderProgram_) {
        glDeleteProgram(shaderProgram_);
    }
}

void NativeRenderer::init(int screenWidth, int screenHeight) {
    screenWidth_ = screenWidth;
    screenHeight_ = screenHeight;
    
    if (!initialized_) {
        setupOpenGL();
        createShaders();
        initialized_ = true;
        LOGD("NativeRenderer initialized: %dx%d", screenWidth, screenHeight);
    }
}

void NativeRenderer::setupOpenGL() {
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
}

void NativeRenderer::createShaders() {
    GLuint vertexShader = glCreateShader(GL_VERTEX_SHADER);
    glShaderSource(vertexShader, 1, &vertexShaderSource, nullptr);
    glCompileShader(vertexShader);
    
    GLint success;
    glGetShaderiv(vertexShader, GL_COMPILE_STATUS, &success);
    if (!success) {
        char infoLog[512];
        glGetShaderInfoLog(vertexShader, 512, nullptr, infoLog);
        LOGE("Vertex shader compilation failed: %s", infoLog);
        return;
    }
    
    GLuint fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
    glShaderSource(fragmentShader, 1, &fragmentShaderSource, nullptr);
    glCompileShader(fragmentShader);
    
    glGetShaderiv(fragmentShader, GL_COMPILE_STATUS, &success);
    if (!success) {
        char infoLog[512];
        glGetShaderInfoLog(fragmentShader, 512, nullptr, infoLog);
        LOGE("Fragment shader compilation failed: %s", infoLog);
        return;
    }
    
    shaderProgram_ = glCreateProgram();
    glAttachShader(shaderProgram_, vertexShader);
    glAttachShader(shaderProgram_, fragmentShader);
    glLinkProgram(shaderProgram_);
    
    glGetProgramiv(shaderProgram_, GL_LINK_STATUS, &success);
    if (!success) {
        char infoLog[512];
        glGetProgramInfoLog(shaderProgram_, 512, nullptr, infoLog);
        LOGE("Shader program linking failed: %s", infoLog);
        return;
    }
    
    glDeleteShader(vertexShader);
    glDeleteShader(fragmentShader);
    
    positionLoc_ = glGetAttribLocation(shaderProgram_, "aPosition");
    colorLoc_ = glGetUniformLocation(shaderProgram_, "uColor");
    mvpLoc_ = glGetUniformLocation(shaderProgram_, "uMVP");
    
    LOGD("Shaders created successfully");
}

void NativeRenderer::beginFrame() {
    if (!initialized_) return;
    
    glUseProgram(shaderProgram_);
    
    float ortho[16] = {
        2.0f / screenWidth_, 0, 0, 0,
        0, -2.0f / screenHeight_, 0, 0,
        0, 0, -1, 0,
        -1, 1, 0, 1
    };
    
    glUniformMatrix4fv(mvpLoc_, 1, GL_FALSE, ortho);
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
}

void NativeRenderer::endFrame() {
    glDisable(GL_BLEND);
    glUseProgram(0);
}

void NativeRenderer::drawLine(const Vec2& start, const Vec2& end, const Color& color, float thickness) {
    if (!initialized_) return;
    
    float vertices[] = {
        start.x, start.y,
        end.x, end.y
    };
    
    glUniform4f(colorLoc_, color.r, color.g, color.b, color.a * settings_.opacity);
    glVertexAttribPointer(positionLoc_, 2, GL_FLOAT, GL_FALSE, 0, vertices);
    glEnableVertexAttribArray(positionLoc_);
    
    glLineWidth(thickness);
    glDrawArrays(GL_LINES, 0, 2);
    
    glDisableVertexAttribArray(positionLoc_);
}

void NativeRenderer::drawRect(const Vec2& topLeft, const Vec2& bottomRight, const Color& color, float thickness) {
    if (!initialized_) return;
    
    float vertices[] = {
        topLeft.x, topLeft.y,
        bottomRight.x, topLeft.y,
        bottomRight.x, bottomRight.y,
        topLeft.x, bottomRight.y,
        topLeft.x, topLeft.y
    };
    
    glUniform4f(colorLoc_, color.r, color.g, color.b, color.a * settings_.opacity);
    glVertexAttribPointer(positionLoc_, 2, GL_FLOAT, GL_FALSE, 0, vertices);
    glEnableVertexAttribArray(positionLoc_);
    
    glLineWidth(thickness);
    glDrawArrays(GL_LINE_STRIP, 0, 5);
    
    glDisableVertexAttribArray(positionLoc_);
}

void NativeRenderer::drawFilledRect(const Vec2& topLeft, const Vec2& bottomRight, const Color& color) {
    if (!initialized_) return;
    
    float vertices[] = {
        topLeft.x, topLeft.y,
        bottomRight.x, topLeft.y,
        topLeft.x, bottomRight.y,
        bottomRight.x, bottomRight.y
    };
    
    glUniform4f(colorLoc_, color.r, color.g, color.b, color.a * settings_.opacity);
    glVertexAttribPointer(positionLoc_, 2, GL_FLOAT, GL_FALSE, 0, vertices);
    glEnableVertexAttribArray(positionLoc_);
    
    glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
    
    glDisableVertexAttribArray(positionLoc_);
}

void NativeRenderer::drawText(const Vec2& pos, const std::string& text, const Color& color, float size) {
    // Text rendering is complex and requires font atlas
    // For now, we'll skip text rendering or use simple placeholder
    // In a full implementation, we'd need to:
    // 1. Load a font atlas texture
    // 2. Render glyphs as textured quads
    // This would be implemented in a future version
}

void NativeRenderer::drawCircle(const Vec2& center, float radius, const Color& color, float thickness) {
    if (!initialized_) return;
    
    const int segments = 32;
    std::vector<float> vertices;
    
    for (int i = 0; i <= segments; i++) {
        float angle = (i * 2.0f * M_PI) / segments;
        vertices.push_back(center.x + cos(angle) * radius);
        vertices.push_back(center.y + sin(angle) * radius);
    }
    
    glUniform4f(colorLoc_, color.r, color.g, color.b, color.a * settings_.opacity);
    glVertexAttribPointer(positionLoc_, 2, GL_FLOAT, GL_FALSE, 0, vertices.data());
    glEnableVertexAttribArray(positionLoc_);
    
    glLineWidth(thickness);
    glDrawArrays(GL_LINE_STRIP, 0, segments + 1);
    
    glDisableVertexAttribArray(positionLoc_);
}

bool NativeRenderer::worldToScreen(const Vec3& worldPos, const Matrix& viewMatrix, Vec2& screenPos) {
    Vec4 clipCoords;
    clipCoords.x = worldPos.x * viewMatrix.m[0][0] + worldPos.y * viewMatrix.m[0][1] + 
                   worldPos.z * viewMatrix.m[0][2] + viewMatrix.m[0][3];
    clipCoords.y = worldPos.x * viewMatrix.m[1][0] + worldPos.y * viewMatrix.m[1][1] + 
                   worldPos.z * viewMatrix.m[1][2] + viewMatrix.m[1][3];
    clipCoords.z = worldPos.x * viewMatrix.m[2][0] + worldPos.y * viewMatrix.m[2][1] + 
                   worldPos.z * viewMatrix.m[2][2] + viewMatrix.m[2][3];
    clipCoords.w = worldPos.x * viewMatrix.m[3][0] + worldPos.y * viewMatrix.m[3][1] + 
                   worldPos.z * viewMatrix.m[3][2] + viewMatrix.m[3][3];
    
    if (clipCoords.w < 0.1f)
        return false;
    
    Vec3 ndc;
    ndc.x = clipCoords.x / clipCoords.w;
    ndc.y = clipCoords.y / clipCoords.w;
    ndc.z = clipCoords.z / clipCoords.w;
    
    screenPos.x = (screenWidth_ / 2.0f * ndc.x) + (ndc.x + screenWidth_ / 2.0f);
    screenPos.y = -(screenHeight_ / 2.0f * ndc.y) + (ndc.y + screenHeight_ / 2.0f);
    
    return true;
}

void NativeRenderer::setSettings(const RenderSettings& settings) {
    settings_ = settings;
}
