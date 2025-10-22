package com.kursch.patterns;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Stap_wawe implements IMovementPattern {

    private final Vector2 center; // Центр вращения
    private final float maxRadius; // Максимальный радиус круга
    private final float angularSpeed; // Скорость вращения (в радианах/сек)
    private final float pulseSpeed; // Скорость пульсации радиуса

    public Stap_wawe(Vector2 center, float maxRadius, float angularSpeed, float pulseSpeed) {
        this.center = center;
        this.maxRadius = maxRadius;
        this.angularSpeed = angularSpeed;
        this.pulseSpeed = pulseSpeed;
    }

    @Override
    public Vector2 getPosition(float t) {
        float angle = angularSpeed * t;

        // Пульсирующий радиус (от 0 до maxRadius и обратно)
        float radius = maxRadius * 0.5f * (1 + MathUtils.sin(pulseSpeed * t));

        float x = center.x + MathUtils.cos(angle) * radius;
        float y = center.y + MathUtils.sin(angle) * radius;

        return new Vector2(x, y);
    }
}
