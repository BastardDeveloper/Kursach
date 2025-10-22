package com.kursch.patterns;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class CirclePattern implements IMovementPattern {

    private final Vector2 center; // Центр вращения
    private final float radius; // Радиус круга
    private final float angularSpeed; // Скорость вращения (в радианах/сек)

    public CirclePattern(Vector2 center, float radius, float angularSpeed) {
        this.center = center;
        this.radius = radius;
        this.angularSpeed = angularSpeed;
    }

    @Override
    public Vector2 getPosition(float t) {
        float angle = angularSpeed * t;

        float x = center.x + MathUtils.cos(angle) * radius * 8;
        float y = center.y + MathUtils.sin(angle) * radius * 8;

        return new Vector2(x, y);
    }
}
