package com.example.espapp;

public class EspData {
    public static class PlayerInfo {
        public float screenX;
        public float screenY;
        public float footX;
        public float footY;
        public float headX;
        public float headY;
        public float boxLeft;
        public float boxTop;
        public float boxRight;
        public float boxBottom;
        public float distance;
        public int health;
        public int maxHealth;
        public int armor;
        public int team;
        public String name;
        public boolean isVisible;
        public boolean isEnemy;
        
        public PlayerInfo() {
            this.screenX = 0;
            this.screenY = 0;
            this.footX = 0;
            this.footY = 0;
            this.headX = 0;
            this.headY = 0;
            this.boxLeft = 0;
            this.boxTop = 0;
            this.boxRight = 0;
            this.boxBottom = 0;
            this.distance = 0;
            this.health = 100;
            this.maxHealth = 100;
            this.armor = 0;
            this.team = 0;
            this.name = "Unknown";
            this.isVisible = false;
            this.isEnemy = true;
        }
    }
    
    public PlayerInfo[] players;
    public int playerCount;
    public float screenWidth;
    public float screenHeight;
    public float centerX;
    public float centerY;
    
    public EspData() {
        this.players = new PlayerInfo[0];
        this.playerCount = 0;
        this.screenWidth = 1920;
        this.screenHeight = 1080;
        this.centerX = 960;
        this.centerY = 540;
    }
}
