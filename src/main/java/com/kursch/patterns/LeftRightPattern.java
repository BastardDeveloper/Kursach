package com.kursch.patterns;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class LeftRightPattern implements MovementPattern {

    private final Vector2 center; // Центр движения
    private final float amplitude; // Амплитуда колебаний (влево-вправо)
    private final float speed; // Скорость движения (в радианах/сек)

    public LeftRightPattern(Vector2 center, float amplitude, float speed) {
        this.center = center;
        this.amplitude = amplitude;
        this.speed = speed;
    }

    @Override
    public Vector2 getPosition(float t) {
        float angle = speed * t;

        // Движение влево-вправо по синусоиде

        float x = center.x + amplitude * MathUtils.sin(angle);
        float y = center.y; // остаётся на месте по вертикали

        return new Vector2(x, y);
    }
}
