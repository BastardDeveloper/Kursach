package com.kursch;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.kursch.patterns.MovementPattern;

public class Enemy {

    private TextureRegion[] directionFrames;
    private TextureRegion currentFrame;

    private MovementPattern pattern;
    private boolean active = true;

    private float time;
    private Vector2 position = new Vector2();
    private Vector2 prevPosition = new Vector2();

    private float width = 50, height = 50;

    // Добавляем в класс Enemy
    private float animationTimer = 0f;
    private int animIndex = 0;
    private final float animationSpeed = 0.5f; // время между кадрами в секундах

    public Enemy(TextureRegion[] directionFrames, MovementPattern pattern, float x, float y) {
        this.directionFrames = directionFrames;
        this.pattern = pattern;
        this.position.set(x, y);
        this.prevPosition.set(x, y);
        this.currentFrame = directionFrames[0];
    }

    public void update(float delta) {
        if (!active)
            return;

        time += delta;
        prevPosition.set(position);
        Vector2 newPos = pattern.getPosition(time);
        position.set(newPos);

        // вычисляем направление движения (dx) и (dy)
        float dx = position.x - prevPosition.x;
        float dy = position.y - prevPosition.y;
        currentFrame = getFrameForDirection(dx, dy);
    }

    public void draw(SpriteBatch batch) {
        if (!active)
            return;

        batch.draw(
                currentFrame,
                position.x, position.y,
                width, height);
    }

    private TextureRegion getFrameForDirection(float dx, float dy) {
        // Обновляем таймер анимации крыльев
        animationTimer += Gdx.graphics.getDeltaTime();
        if (animationTimer >= animationSpeed) {
            animIndex = (animIndex + 1) % 2; // переключаем 0 -> 1 -> 0...
            animationTimer = 0f;
        }

        // Если почти не двигается - стоим на месте (нейтрально)
        if (Math.abs(dx) < 0.2f && Math.abs(dy) < 0.2f) {
            return directionFrames[0 + animIndex]; // вверх с анимацией крыльев
        }

        // === Определяем НАПРАВЛЕНИЕ (baseIndex) ===
        int baseIndex;

        // Движение вверх
        if (dy > 1f) {
            baseIndex = 0; // вверх
        }
        // Движение вниз
        else if (dy < -1f) {
            baseIndex = 2; // вниз
        }
        // Движение влево (3 степени наклона)
        else if (dx < -2f) {
            baseIndex = 8; // полностью влево
        } else if (dx < -1.5f) {
            baseIndex = 6; // влево средне
        } else if (dx < -1f) {
            baseIndex = 4; // влево слабо
        }
        // Движение вправо (3 степени наклона)
        else if (dx > 2f) {
            baseIndex = 14; // полностью вправо
        } else if (dx > 1.5f) {
            baseIndex = 12; // вправо средне
        } else if (dx > 1f) {
            baseIndex = 10; // вправо слабо
        }
        // По умолчанию
        else {
            baseIndex = 0; // вверх
        }

        // Возвращаем кадр: направление + анимация крыльев
        return directionFrames[baseIndex + animIndex];
    }

    public Rectangle getBounds() {
        return new Rectangle(position.x, position.y, width, height);
    }

    public void destroy() {
        active = false;
    }

    public boolean isActive() {
        return active;
    }
}
