package com.kursch.patterns;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class LeftRightPattern implements MovementPattern {
    private final Vector2 center;
    private final float amplitude;
    private final float frequency;
    private final float phaseOffset; // Фаза каждого врага
    private static float globalGameTime = 0f;

    public LeftRightPattern(Vector2 center, float amplitude, float frequency, float phaseOffset) {
        this.center = center;
        this.amplitude = amplitude;
        this.frequency = frequency;
        this.phaseOffset = phaseOffset;
    }

    public LeftRightPattern(Vector2 center, float amplitude, float frequency) {
        this(center, amplitude, frequency, 0f);
    }

    @Override
    public Vector2 getPosition(float elapsedTime) {
        // Используем глобальное время + индивидуальная фаза для волнового эффекта
        float angle = MathUtils.PI2 * frequency * globalGameTime + phaseOffset;

        float x = center.x + amplitude * MathUtils.sin(angle);
        float y = center.y;

        return new Vector2(x, y);
    }

    @Override
    public boolean isComplete(float time) {
        return false;
    }

    public static void updateGlobalTime(float delta) {
        globalGameTime += delta;
    }

    public static void resetGlobalTime() {
        globalGameTime = 0f;
    }

    public static float getGlobalGameTime() {
        return globalGameTime;
    }
}