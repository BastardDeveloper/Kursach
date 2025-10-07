package com.kursch.patterns;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class InfinityPattern implements MovementPattern {

    private final Vector2 center; // Центр "восьмёрки"
    private final float size; // Размер/амплитуда траектории
    private final float speed; // Скорость движения (радиан/сек)

    public InfinityPattern(Vector2 center, float size, float speed) {
        this.center = center;
        this.size = size;
        this.speed = speed;
    }

    @Override
    public Vector2 getPosition(float t) {
        float angle = speed * t;

        // Уравнение "восьмёрки": x = a * sin(t), y = a * sin(t) * cos(t)
        float x = center.x + size * MathUtils.sin(angle) * 10;
        float y = center.y + size * MathUtils.sin(angle) * MathUtils.cos(angle) * 10;

        return new Vector2(x, y);
    }
}
