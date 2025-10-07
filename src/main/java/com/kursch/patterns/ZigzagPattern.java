package com.kursch.patterns;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class ZigzagPattern implements MovementPattern {

    private final Vector2 start; // Стартовая позиция
    private final float speed; // Скорость движения вниз
    private final float amplitude; // Амплитуда зигзага по X
    private final float frequency; // Частота колебаний

    public ZigzagPattern(Vector2 start, float speed, float amplitude, float frequency) {
        this.start = start;
        this.speed = speed;
        this.amplitude = amplitude;
        this.frequency = frequency;
    }

    @Override
    public Vector2 getPosition(float t) {
        // Движение вниз с постоянной скоростью
        float y = start.y - speed * t;

        // Горизонтальные колебания по синусоиде
        float x = start.x + amplitude * MathUtils.sin(frequency * t);

        return new Vector2(x, y);
    }
}
