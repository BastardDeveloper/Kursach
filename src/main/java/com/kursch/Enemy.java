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
    private boolean inFormation = false;
    private float time;
    private int assignedSlot = -1;
    private Vector2 position = new Vector2();
    private Vector2 prevPosition = new Vector2();
    private float width = 30, height = 30;

    private float animationTimer = 0f;
    private int animIndex = 0;
    private final float animationSpeed = 0.5f;

    // Сглаженное направление
    private Vector2 smoothDirection = new Vector2(0, 1);
    private final float directionSmoothness = 0.15f;

    // Задержка спавна
    private float spawnDelay = 0f;
    private float spawnTimer = 0f;
    private boolean isSpawning = false;

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

        // Обработка задержки спавна
        if (isSpawning) {
            spawnTimer += delta;
            if (spawnTimer >= spawnDelay) {
                isSpawning = false;
            } else {
                return; // Пропускаем обновление пока не истекла задержка
            }
        }

        time += delta;
        prevPosition.set(position);
        Vector2 newPos = pattern.getPosition(time);
        position.set(newPos);

        // Отмечаем что враг в формации если паттерн завершен
        if (!inFormation && pattern.isComplete(time)) {
            inFormation = true;
        }

        // Вычисляем мгновенное направление движения
        float dx = position.x - prevPosition.x;
        float dy = position.y - prevPosition.y;

        // Сглаживаем направление через lerp
        Vector2 instantDirection = new Vector2(dx, dy);
        if (instantDirection.len2() > 0.01f) {
            instantDirection.nor();
            smoothDirection.lerp(instantDirection, directionSmoothness);
            smoothDirection.nor();
        }

        currentFrame = getFrameForDirection(smoothDirection.x, smoothDirection.y);
    }

    public void draw(SpriteBatch batch) {
        if (!active || isSpawning)
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
            animIndex = (animIndex + 1) % 2;
            animationTimer = 0f;
        }

        // Если почти не двигается
        if (Math.abs(dx) < 0.5f && Math.abs(dy) < 0.5f) {
            return directionFrames[0 + animIndex];
        }

        // Вычисляем угол направления в градусах (0° = вправо, 90° = вверх)
        float angle = (float) Math.toDegrees(Math.atan2(dy, dx));
        if (angle < 0)
            angle += 360;

        int baseIndex;

        // Определяем направление по секторам
        if (angle >= 75 && angle < 105) {
            baseIndex = 0; // вверх
        } else if (angle >= 255 && angle < 285) {
            baseIndex = 2; // вниз
        } else if (angle >= 165 && angle < 195) {
            baseIndex = 8; // полностью влево
        } else if (angle >= 120 && angle < 150) {
            baseIndex = 6; // влево средне (верх)
        } else if (angle >= 105 && angle < 120) {
            baseIndex = 4; // влево слабо (верх)
        } else if (angle >= 210 && angle < 240) {
            baseIndex = 18; // влево средне (низ)
        } else if (angle >= 240 && angle < 255) {
            baseIndex = 16; // влево слабо (низ)
        } else if (angle >= 195 && angle < 210) {
            baseIndex = 20; // полностью влево (низ)
        } else if (angle >= 345 || angle < 15) {
            baseIndex = 14; // полностью вправо
        } else if (angle >= 30 && angle < 60) {
            baseIndex = 12; // вправо средне (верх)
        } else if (angle >= 60 && angle < 75) {
            baseIndex = 10; // вправо слабо (верх)
        } else if (angle >= 300 && angle < 330) {
            baseIndex = 24; // вправо средне (низ)
        } else if (angle >= 285 && angle < 300) {
            baseIndex = 22; // вправо слабо (низ)
        } else if (angle >= 330 && angle < 345) {
            baseIndex = 26; // полностью вправо (низ)
        } else if (angle >= 15 && angle < 30) {
            baseIndex = 14; // вправо
        } else if (angle >= 150 && angle < 165) {
            baseIndex = 8; // влево
        } else {
            baseIndex = 0;
        }

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

    public void setBaseFrame() {
        smoothDirection.set(0, 1);
        animIndex = 0;
        animationTimer = 0f;
        currentFrame = directionFrames[0];
    }

    public void shooting() {
        // Логика стрельбы
    }

    public Vector2 getPosition() {
        return new Vector2(position.x, position.y);
    }

    public void setMovementPattern(MovementPattern newPattern) {
        this.pattern = newPattern;
        this.time = 0f;
        if (newPattern instanceof com.kursch.patterns.LeftRightPattern) {
            this.inFormation = true;
        } else {
            this.inFormation = false;
        }
    }

    public void setAssignedSlot(int slot) {
        this.assignedSlot = slot;
    }

    public int getAssignedSlot() {
        return assignedSlot;
    }

    public boolean isPatternComplete() {
        return pattern != null && pattern.isComplete(time);
    }

    public MovementPattern getPattern() {
        return pattern;
    }

    public boolean isInFormation() {
        return inFormation;
    }

    public void setSpawnDelay(float delay) {
        this.spawnDelay = delay;
        this.spawnTimer = 0f;
        this.isSpawning = delay > 0;
    }

    public boolean isSpawning() {
        return isSpawning;
    }

    public void dispose() {
        if (currentFrame != null && currentFrame.getTexture() != null) {
            currentFrame.getTexture().dispose();
        }
    }
}