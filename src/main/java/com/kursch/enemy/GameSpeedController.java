package com.kursch.enemy;

public class GameSpeedController {

    private float gameTime = 0f;
    private float speedMultiplier = 1f;

    private final float baseSpeedMultiplier = 1f;
    private final float maxSpeedMultiplier = 10;
    private final float speedIncreaseRate = 0.5f; // +50% кожні 10 секунд

    public void update(float delta) {
        gameTime += delta;
        speedMultiplier = Math.min(maxSpeedMultiplier,
                baseSpeedMultiplier + (gameTime / 10f) * speedIncreaseRate);
    }

    public float getSpeedMultiplier() {
        return speedMultiplier;
    }

    public float getGameTime() {
        return gameTime;
    }
}
