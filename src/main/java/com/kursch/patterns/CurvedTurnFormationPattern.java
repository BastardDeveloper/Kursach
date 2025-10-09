package com.kursch.patterns;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.MathUtils;

public class CurvedTurnFormationPattern implements MovementPattern {
    private final Vector2 start;
    private final Vector2 playerTarget;
    private final Vector2 formationTarget;
    private final float duration;
    private final int direction; // 1 = вправо, -1 = влево
    private final float turnRadius;

    // Кэшированные точки для оптимизации
    private final Vector2 approachPoint;
    private final Vector2 arcCenter;
    private final float arcStartAngle;
    private final float arcEndAngle;
    private final Vector2 exitPoint;

    public CurvedTurnFormationPattern(Vector2 start, Vector2 playerTarget, Vector2 formationTarget,
            float duration, float turnRadius, int direction) {
        this.start = new Vector2(start);
        this.playerTarget = new Vector2(playerTarget);
        this.formationTarget = new Vector2(formationTarget);
        this.duration = Math.max(0.1f, duration);
        this.turnRadius = turnRadius;
        this.direction = MathUtils.clamp(direction, -1, 1);

        // Предварительно вычисляем все ключевые точки траектории
        this.approachPoint = calculateApproachPoint();
        this.arcCenter = calculateArcCenter();
        this.arcStartAngle = calculateArcStartAngle();
        this.arcEndAngle = calculateArcEndAngle();
        this.exitPoint = calculateExitPoint();
    }

    @Override
    public Vector2 getPosition(float elapsedTime) {
        float t = MathUtils.clamp(elapsedTime / duration, 0f, 1f);

        // 🔹 Фаза 1 (0.0–0.4): Подлёт к игроку с небольшим ускорением
        if (t < 0.4f) {
            float phase = easeInOut(t / 0.4f);
            return new Vector2(start).lerp(approachPoint, phase);
        }

        // 🔹 Фаза 2 (0.4–0.75): Петля вокруг игрока
        else if (t < 0.75f) {
            float phase = (t - 0.4f) / 0.35f;
            float angle = MathUtils.lerp(arcStartAngle, arcEndAngle, easeInOut(phase));

            float x = arcCenter.x + MathUtils.cos(angle) * turnRadius;
            float y = arcCenter.y + MathUtils.sin(angle) * turnRadius;

            return new Vector2(x, y);
        }

        // 🔹 Фаза 3 (0.75–1.0): Плавный выход в формацию
        else {
            float phase = (t - 0.75f) / 0.25f;
            return new Vector2(exitPoint).lerp(formationTarget, easeOut(phase));
        }
    }

    // Точка приближения к игроку (чуть ближе середины)
    private Vector2 calculateApproachPoint() {
        return new Vector2(start).lerp(playerTarget, 0.6f);
    }

    // Центр дуги для петли
    private Vector2 calculateArcCenter() {
        // Направление от точки подлёта к игроку
        Vector2 dirToPlayer = new Vector2(playerTarget).sub(approachPoint).nor();
        // Перпендикуляр влево/вправо
        Vector2 perpendicular = new Vector2(-dirToPlayer.y, dirToPlayer.x).scl(direction);
        // Центр дуги относительно ТОЧКИ ПОДЛЁТА, не игрока
        return new Vector2(approachPoint).add(perpendicular.scl(turnRadius));
    }

    // Начальный угол дуги
    private float calculateArcStartAngle() {
        return MathUtils.atan2(approachPoint.y - arcCenter.y, approachPoint.x - arcCenter.x);
    }

    // Конечный угол дуги (полный разворот 180° + небольшая коррекция)
    private float calculateArcEndAngle() {
        float targetAngle = MathUtils.atan2(formationTarget.y - arcCenter.y,
                formationTarget.x - arcCenter.x);

        // Выбираем направление разворота для наиболее естественной траектории
        float angleDiff = targetAngle - arcStartAngle;
        while (angleDiff > MathUtils.PI)
            angleDiff -= MathUtils.PI2;
        while (angleDiff < -MathUtils.PI)
            angleDiff += MathUtils.PI2;

        // Делаем петлю не ровно на 180°, а с учётом направления к формации
        return arcStartAngle + MathUtils.PI * direction + angleDiff * 0.3f;
    }

    // Точка выхода из петли
    private Vector2 calculateExitPoint() {
        float x = arcCenter.x + MathUtils.cos(arcEndAngle) * turnRadius;
        float y = arcCenter.y + MathUtils.sin(arcEndAngle) * turnRadius;
        return new Vector2(x, y);
    }

    // Ease-in-out для плавности
    private float easeInOut(float t) {
        return t < 0.5f
                ? 2f * t * t
                : 1f - (1f - 2f * (t - 0.5f)) * (1f - 2f * (t - 0.5f)) / 2f;
    }

    // Ease-out для финального замедления
    private float easeOut(float t) {
        return 1f - (1f - t) * (1f - t);
    }

    @Override
    public boolean isComplete(float time) {
        return time >= duration;
    }
}