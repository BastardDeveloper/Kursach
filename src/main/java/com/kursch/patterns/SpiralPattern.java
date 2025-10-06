package com.kursch.patterns;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class SpiralPattern implements MovementPattern {
    private Vector2 center;
    private float radiusSpeed, angleSpeed;

    public SpiralPattern(Vector2 center, float radiusSpeed, float angleSpeed) {
        this.center = center;
        this.radiusSpeed = radiusSpeed;
        this.angleSpeed = angleSpeed;
    }

    @Override
    public Vector2 getPosition(float t) {
        float r = radiusSpeed * t;
        float angle = angleSpeed * t;
        return new Vector2(center.x, center.y);
    }
}
