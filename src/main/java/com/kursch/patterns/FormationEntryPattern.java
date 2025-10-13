package com.kursch.patterns;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * Единый паттерн: плавный вход в формацию + волновое движение одновременно
 */
public class FormationEntryPattern implements MovementPattern {
    private final Vector2 startPos;
    private final Vector2 formationCenterX; // Только X позиция в центре волны
    private final float formationY; // Y позиция в строю
    private final float entryDuration;
    private final float frequency;
    private final float amplitude; // Амплитуда волны
    private static float globalGameTime = 0f;

    private float startTime = -1f;

    public FormationEntryPattern(Vector2 startPos, float formationCenterX, float formationY,
            float entryDuration, float frequency, float amplitude) {
        this.startPos = new Vector2(startPos);
        this.formationCenterX = new Vector2(formationCenterX, 0);
        this.formationY = formationY;
        this.entryDuration = entryDuration;
        this.frequency = frequency;
        this.amplitude = amplitude;
    }

    @Override
    public Vector2 getPosition(float elapsedTime) {
        if (startTime < 0) {
            startTime = globalGameTime;
        }

        float timeElapsed = globalGameTime - startTime;

        // Интерполяция входа в строй с плавным затуханием
        float entryProgress = Math.min(1f, timeElapsed / entryDuration);
        float easeInOutQuad = entryProgress < 0.5f
                ? 2f * entryProgress * entryProgress
                : 1f - (-2f * entryProgress + 2f) * (-2f * entryProgress + 2f) / 2f;

        // Y: плавно двигаемся от startY к formationY
        float y = MathUtils.lerp(startPos.y, formationY, easeInOutQuad);

        // X: плавно двигаемся от startX к центру + волна
        float angle = MathUtils.PI2 * frequency * globalGameTime;
        float waveOffset = amplitude * MathUtils.sin(angle);
        float x = MathUtils.lerp(startPos.x, formationCenterX.x, easeInOutQuad) + waveOffset;

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