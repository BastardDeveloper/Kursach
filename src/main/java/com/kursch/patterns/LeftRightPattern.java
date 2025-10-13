package com.kursch.patterns;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class LeftRightPattern implements MovementPattern {
    private final Vector2 center; // Центр движения
    private final float amplitude; // Амплитуда колебаний (влево-вправо)
    private final float frequency; // Частота колебаний (циклов в секунду)
    private final float phaseOffset; // Сдвиг фазы для каждого врага

    private float globalTime = 0f; // Глобальное время паттерна

    /**
     * @param center      центр движения
     * @param amplitude   амплитуда колебаний
     * @param frequency   частота колебаний (циклов/сек), например 0.5f = 1 полный
     *                    цикл за 2 секунды
     * @param phaseOffset сдвиг фазы в радианах (например, для волнового эффекта)
     */
    public LeftRightPattern(Vector2 center, float amplitude, float frequency, float phaseOffset) {
        this.center = center;
        this.amplitude = amplitude;
        this.frequency = frequency;
        this.phaseOffset = phaseOffset;
    }

    /**
     * Конструктор без сдвига фазы (синхронное движение)
     */
    public LeftRightPattern(Vector2 center, float amplitude, float frequency) {
        this(center, amplitude, frequency, 0f);
    }

    @Override
    public Vector2 getPosition(float elapsedTime) {
        globalTime += elapsedTime;

        // Используем глобальное время + сдвиг фазы для синхронизации
        float angle = MathUtils.PI2 * frequency * globalTime + phaseOffset;

        // Движение влево-вправо по синусоиде
        float x = center.x + amplitude * MathUtils.sin(angle);
        float y = center.y; // остаётся на месте по вертикали

        return new Vector2(x, y);
    }

    @Override
    public boolean isComplete(float time) {
        return false; // Бесконечный паттерн
    }

    /**
     * Установить глобальное время (для синхронизации с игрой)
     */
    public void setGlobalTime(float time) {
        this.globalTime = time;
    }
}