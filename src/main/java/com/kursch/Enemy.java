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

        // вычисляем направление движения (dx)
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
        animationTimer += Gdx.graphics.getDeltaTime(); // прибавляем время с последнего кадра

        if (animationTimer >= animationSpeed) {
            animIndex = (animIndex + 1) % 2; // переключаем между 0 и 1
            animationTimer = 0f;
        }
        // Если почти не двигается - стоим на месте (нейтрально)
        if (Math.abs(dx) < 0.2f && Math.abs(dy) < 0.2f) {
            return directionFrames[0];
        }

        int animIndex = (int) ((time * 8) % 2);

        // === Движение вверх ===
        if (dy > 1f) {
            return directionFrames[0 + animIndex];
        }

        // === Движение вниз ===
        if (dy < -1f) {
            return directionFrames[2 + animIndex];
        }

        // === Движение влево ===
        if (dx < -2) {
            return directionFrames[8 + animIndex];
        } else if (dx < -1.5f) {
            return directionFrames[6 + animIndex];
        } else if (dx < -1f) {
            return directionFrames[4 + animIndex];
        }

        // === Движение вправо ===
        if (dx > 2) {
            return directionFrames[14 + animIndex];
        } else if (dx > 1.5f) {
            return directionFrames[12 + animIndex];
        } else if (dx > 1f) {
            return directionFrames[10 + animIndex];
        }

        // === Если слабое движение — вверх по умолчанию ===
        return directionFrames[0 + animIndex];
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
