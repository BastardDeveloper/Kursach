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
    private float width = 30, height = 30;

    private float animationTimer = 0f;
    private int animIndex = 0;
    private final float animationSpeed = 0.5f;

    // Добавляем сглаженное направление
    private Vector2 smoothDirection = new Vector2(0, 1); // начальное направление - вверх
    private final float directionSmoothness = 0.15f; // чем меньше значение, тем плавнее (0.05-0.2)

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

        // Вычисляем мгновенное направление
        float dx = position.x - prevPosition.x;
        float dy = position.y - prevPosition.y;

        // Сглаживаем направление (lerp)
        Vector2 instantDirection = new Vector2(dx, dy);
        if (instantDirection.len2() > 0.01f) { // только если есть реальное движение
            instantDirection.nor(); // нормализуем
            smoothDirection.lerp(instantDirection, directionSmoothness);
            smoothDirection.nor(); // нормализуем результат
        }

        currentFrame = getFrameForDirection(smoothDirection.x, smoothDirection.y);
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
            animIndex = (animIndex + 1) % 2;
            animationTimer = 0f;
        }

        // Если почти не двигается
        if (Math.abs(dx) < 0.1f && Math.abs(dy) < 0.1f) {
            return directionFrames[0 + animIndex];
        }

        // Вычисляем угол направления в градусах (0° = вправо, 90° = вверх)
        float angle = (float) Math.toDegrees(Math.atan2(dy, dx));
        // Нормализуем угол к диапазону [0, 360)
        if (angle < 0)
            angle += 360;

        int baseIndex;

        // Определяем направление по секторам
        // Вверх: 75° - 105°
        if (angle >= 75 && angle < 105) {
            baseIndex = 0; // вверх
        }
        // Вниз: 255° - 285°
        else if (angle >= 255 && angle < 285) {
            baseIndex = 2; // вниз
        }
        // Влево полностью: 165° - 195°
        else if (angle >= 165 && angle < 195) {
            baseIndex = 8; // полностью влево
        }
        // Влево верх средне: 120° - 150°
        else if (angle >= 120 && angle < 150) {
            baseIndex = 6; // влево средне (верх)
        }
        // Влево верх слабо: 105° - 120°
        else if (angle >= 105 && angle < 120) {
            baseIndex = 4; // влево слабо (верх)
        }
        // Влево низ средне: 210° - 240°
        else if (angle >= 210 && angle < 240) {
            baseIndex = 18; // влево средне (низ)
        }
        // Влево низ слабо: 240° - 255°
        else if (angle >= 240 && angle < 255) {
            baseIndex = 16; // влево слабо (низ)
        }
        // Влево полностью низ: 195° - 210°
        else if (angle >= 195 && angle < 210) {
            baseIndex = 20; // полностью влево (низ)
        }
        // Вправо полностью: 345° - 15°
        else if (angle >= 345 || angle < 15) {
            baseIndex = 14; // полностью вправо
        }
        // Вправо верх средне: 30° - 60°
        else if (angle >= 30 && angle < 60) {
            baseIndex = 12; // вправо средне (верх)
        }
        // Вправо верх слабо: 60° - 75°
        else if (angle >= 60 && angle < 75) {
            baseIndex = 10; // вправо слабо (верх)
        }
        // Вправо низ средне: 300° - 330°
        else if (angle >= 300 && angle < 330) {
            baseIndex = 24; // вправо средне (низ)
        }
        // Вправо низ слабо: 285° - 300°
        else if (angle >= 285 && angle < 300) {
            baseIndex = 22; // вправо слабо (низ)
        }
        // Вправо полностью низ: 330° - 345°
        else if (angle >= 330 && angle < 345) {
            baseIndex = 26; // полностью вправо (низ)
        }
        // Диагонали (заполняем промежутки)
        else if (angle >= 15 && angle < 30) {
            baseIndex = 14; // вправо
        } else if (angle >= 150 && angle < 165) {
            baseIndex = 8; // влево
        }
        // По умолчанию
        else {
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

    public void dispose() {
        currentFrame.getTexture().dispose();
    }
}