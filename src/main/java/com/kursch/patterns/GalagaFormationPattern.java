package com.kursch.patterns;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class GalagaFormationPattern implements MovementPattern {
    private final Vector2 center;
    private final float amplitude;
    private final float frequency;
    private static float globalGameTime = 0f;

    public GalagaFormationPattern(Vector2 center, float amplitude, float frequency) {
        this.center = new Vector2(center);
        this.amplitude = amplitude;
        this.frequency = frequency;
    }

    @Override
    public Vector2 getPosition(float elapsedTime) {

        float angle = MathUtils.PI2 * frequency * globalGameTime;
        float offsetX = amplitude * MathUtils.sin(angle);

        return new Vector2(center.x + offsetX, center.y);
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