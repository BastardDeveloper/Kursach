package com.kursch.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.kursch.patterns.IMovementPattern;

public class Enemy {

    private Animation<TextureRegion> deadAnimation;
    private Array<Bullet> bullets;
    private TextureRegion bulletTexture;
    private TextureRegion[] directionFrames;
    private TextureRegion currentFrame;
    private IMovementPattern pattern;
    private boolean active = true;
    private boolean isDead = false;
    private boolean isReallyDead = false; // Флаг что враг точно мёртв
    private float stateTime = 0f;
    private boolean inFormation = false;
    private float time;
    private int points;
    private int assignedSlot = -1;
    private Vector2 position = new Vector2();
    private Vector2 prevPosition = new Vector2();
    private float width = 40, height = 40;
    private float phaseOffset = 0f;

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
    Sound enemyDead_Sound = Gdx.audio.newSound(Gdx.files.internal("EnemyDeadSound.mp3"));

    // Сохраняем текстуру для правильной очистки
    private Texture spriteSheet;

    public Enemy(TextureRegion[] directionFrames, IMovementPattern pattern, float x, float y, int points) {
        this.directionFrames = directionFrames;
        this.pattern = pattern;
        this.points = points;
        this.position.set(x, y);
        this.prevPosition.set(x, y);
        this.currentFrame = directionFrames[0];
        bullets = new Array<>();

        spriteSheet = new Texture("ВеселаяНарезка.png");
        spriteSheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        bulletTexture = new TextureRegion(spriteSheet, 312, 139, 4, 9);

        TextureRegion[] frames = new TextureRegion[5];
        int startX = 290;
        int startY = 2;
        int frameWidth = 30;
        int frameHeight = 30;
        int frameCount = 5;
        int frameOffset = 34;

        // Используем одну текстуру для всех кадров
        for (int i = 0; i < frameCount; i++) {
            int SpryteX = startX + i * frameOffset;
            frames[i] = new TextureRegion(spriteSheet, SpryteX, startY, frameWidth, frameHeight);
        }

        // Создаём анимацию с длительностью 0.1 секунды на кадр
        deadAnimation = new Animation<>(0.1f, frames);
    }

    public void update(float delta) {

        if (isSpawning) {
            spawnTimer += delta;
            if (spawnTimer < spawnDelay) {
                return;
            } else {
                isSpawning = false;
            }
        }

        if (isDead) {
            stateTime += delta;
            if (deadAnimation.isAnimationFinished(stateTime)) {
                active = false;
                isReallyDead = true;
            }
            return;
        }

        if (!active)
            return;

        time += delta;
        prevPosition.set(position);

        Vector2 newPos;
        if (pattern instanceof com.kursch.patterns.FormationEntryPattern) {
            newPos = pattern.getPosition(com.kursch.patterns.FormationEntryPattern.getGlobalGameTime());
        } else if (pattern instanceof com.kursch.patterns.GalagaFormationPattern) {
            newPos = pattern.getPosition(com.kursch.patterns.GalagaFormationPattern.getGlobalGameTime());
        } else {
            newPos = pattern.getPosition(time);
        }
        position.set(newPos);

        if (!inFormation && pattern.isComplete(time)) {
            inFormation = true;
        }

        float dx = position.x - prevPosition.x;
        float dy = position.y - prevPosition.y;

        Vector2 instantDirection = new Vector2(dx, dy);
        if (instantDirection.len2() > 0.01f) {
            instantDirection.nor();
            smoothDirection.lerp(instantDirection, directionSmoothness);
            smoothDirection.nor();
        }

        currentFrame = getFrameForDirection(smoothDirection.x, smoothDirection.y);

        for (Bullet b : bullets) {
            b.update(delta);
        }
    }

    public void draw(SpriteBatch batch) {
        if (isDead) {
            // Используем false для loop, чтобы анимация не повторялась
            TextureRegion frame = deadAnimation.getKeyFrame(stateTime, false);

            // ИСПРАВЛЕНИЕ: Увеличиваем размер анимации смерти
            float deathWidth = width * 1.5f; // Увеличиваем в 1.5 раза
            float deathHeight = height * 1.5f;

            // Центрируем увеличенную анимацию
            float offsetX = (width - deathWidth) / 2f;
            float offsetY = (height - deathHeight) / 2f;

            batch.draw(frame,
                    position.x + offsetX,
                    position.y + offsetY,
                    deathWidth,
                    deathHeight);
        } else if (active) {
            batch.draw(currentFrame, position.x, position.y, width, height);
        }

        for (Bullet b : bullets) {
            b.draw(batch);
        }
    }

    private TextureRegion getFrameForDirection(float dx, float dy) {
        // Обновляем таймер анимации крыльев
        animationTimer += Gdx.graphics.getDeltaTime();
        if (animationTimer >= animationSpeed) {
            animIndex = (animIndex + 1) % 2;
            animationTimer = 0f;
        }

        // Если почти не двигается
        if (Math.abs(dx) < 0.1f && Math.abs(dy) < 0.5f) {
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
        // ИСПРАВЛЕНИЕ: Не возвращаем коллизию для мёртвых врагов
        if (isDead || isReallyDead) {
            return new Rectangle(0, 0, 0, 0); // Пустой прямоугольник
        }
        return new Rectangle(position.x, position.y, width, height);
    }

    public void destroy() {
        // ИСПРАВЛЕНИЕ: Проверяем что враг ещё не начал умирать
        if (isDead || isReallyDead) {
            return; // Игнорируем повторные вызовы
        }

        if (enemyDead_Sound != null) {
            enemyDead_Sound.play();
        }
        isDead = true;
        stateTime = 0f; // сбрасываем время, чтобы анимация шла с начала
    }

    public void setBaseFrame() {
        smoothDirection.set(0, 1);
        animIndex = 0;
        animationTimer = 0f;
        currentFrame = directionFrames[0];
    }

    public void shooting(Player player) {
        Sprite bulletSprite = new Sprite(bulletTexture);
        bulletSprite.setSize(10, 25);

        // Початкова позиція кулі — центр ворога
        float startX = position.x + width / 2f - bulletSprite.getWidth() / 2f;
        float startY = position.y + height / 2f - bulletSprite.getHeight() / 2f;

        // Вектор напрямку від ворога до гравця
        Vector2 direction = new Vector2(
                player.getPosition().x - position.x,
                player.getPosition().y - position.y).nor(); // нормалізація

        // Додаємо кулю з цим напрямком
        bullets.add(new Bullet(bulletSprite, startX, startY, direction, 600f));
    }

    public void setMovementPattern(IMovementPattern newPattern) {
        this.pattern = newPattern;
        this.time = 0f;
        setBaseFrame();
        // Если переходим в атаку - враг не в формации
        if (newPattern instanceof com.kursch.patterns.DiveAttackPattern) {
            this.inFormation = false;
        }
        // Если переходим в FormationEntryPattern или GalagaFormationPattern - враг
        // атакует
        else if (newPattern instanceof com.kursch.patterns.FormationEntryPattern
                || newPattern instanceof com.kursch.patterns.GalagaFormationPattern
                || newPattern instanceof com.kursch.patterns.LeftRightPattern) {
            this.inFormation = true;
        } else {
            this.inFormation = false;
        }
    }

    public void setSpawnDelay(float delay) {
        this.spawnDelay = delay;
        this.spawnTimer = 0f;
        this.isSpawning = delay > 0;
    }

    public void setPhaseOffset(float phase) {
        this.phaseOffset = phase;
    }

    public void setAssignedSlot(int slot) {
        this.assignedSlot = slot;
    }

    public boolean isPatternComplete() {
        return pattern != null && pattern.isComplete(time);
    }

    public boolean isInFormation() {
        return inFormation;
    }

    public boolean isSpawning() {
        return isSpawning;
    }

    public boolean isDead() {
        return isDead;
    }

    public boolean isReallyDead() {
        return isReallyDead;
    }

    public boolean isActive() {
        return active;
    }

    public float getPhaseOffset() {
        return phaseOffset;
    }

    public Vector2 getPosition() {
        return new Vector2(position.x, position.y);
    }

    public Array<Bullet> getEnemyBullets() {
        return bullets;
    }

    public int getAssignedSlot() {
        return assignedSlot;
    }

    public int getPoints() {
        return points;
    }

    public IMovementPattern getPattern() {
        return pattern;
    }

    public void dispose() {
        // Правильная очистка ресурсов
        if (spriteSheet != null) {
            spriteSheet.dispose();
        }
        if (enemyDead_Sound != null) {
            enemyDead_Sound.dispose();
        }
    }
}