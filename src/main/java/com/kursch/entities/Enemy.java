package com.kursch.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.kursch.patterns.IMovementPattern;
import com.kursch.graphics.animation.AnimationManager;

public class Enemy extends AGameObject {

    private Animation<TextureRegion> deadAnimation;
    private Array<Bullet> bullets;
    private BulletFactory bulletFactory;
    private TextureRegion[] directionFrames;
    private TextureRegion currentFrame;
    private IMovementPattern pattern;
    private boolean isDead = false;
    private boolean isReallyDead = false;
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

    private Vector2 smoothDirection = new Vector2(0, 1);
    private final float directionSmoothness = 0.15f;

    private float spawnDelay = 0f;
    private float spawnTimer = 0f;
    private boolean isSpawning = false;
    private Sound enemyDead_Sound;

    public Enemy(TextureRegion[] directionFrames, IMovementPattern pattern, float x, float y,
            int points, AnimationManager animationManager) {
        this.directionFrames = directionFrames;
        this.pattern = pattern;
        this.points = points;
        this.position.set(x, y);
        this.prevPosition.set(x, y);
        this.currentFrame = directionFrames[0];
        this.alive = true;
        bullets = new Array<>();

        // Инициализация sprite для базового класса
        sprite = new Sprite(directionFrames[0]);
        sprite.setSize(width, height);
        sprite.setPosition(x, y);

        // Загрузка звука
        enemyDead_Sound = Gdx.audio.newSound(Gdx.files.internal("EnemyDeadSound.mp3"));

        // Получение анимации смерти из AnimationManager
        deadAnimation = animationManager.get("enemy_death");

        // Инициализация фабрики пуль
        bulletFactory = new BulletFactory(animationManager.getSpriteSheet());
    }

    @Override
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
                alive = false;
                isReallyDead = true;
            }
            return;
        }

        if (!alive)
            return;

        time += delta;
        prevPosition.set(position);

        // Получаем новую позицию из паттерна движения
        Vector2 newPos;
        if (pattern instanceof com.kursch.patterns.FormationEntryPattern) {
            newPos = pattern.getPosition(com.kursch.patterns.FormationEntryPattern.getGlobalGameTime());
        } else if (pattern instanceof com.kursch.patterns.GalagaFormationPattern) {
            newPos = pattern.getPosition(com.kursch.patterns.GalagaFormationPattern.getGlobalGameTime());
        } else {
            newPos = pattern.getPosition(time);
        }
        position.set(newPos);

        // Синхронизируем sprite с position
        sprite.setPosition(position.x, position.y);

        if (!inFormation && pattern.isComplete(time)) {
            inFormation = true;
        }

        // Обновление направления для анимации
        float dx = position.x - prevPosition.x;
        float dy = position.y - prevPosition.y;

        Vector2 instantDirection = new Vector2(dx, dy);
        if (instantDirection.len2() > 0.01f) {
            instantDirection.nor();
            smoothDirection.lerp(instantDirection, directionSmoothness);
            smoothDirection.nor();
        }

        currentFrame = getFrameForDirection(smoothDirection.x, smoothDirection.y);
        sprite.setRegion(currentFrame);

        // Обновляем пули
        for (Bullet b : bullets) {
            b.update(delta);
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (isDead) {
            TextureRegion frame = deadAnimation.getKeyFrame(stateTime, false);

            float deathWidth = width * 1.5f;
            float deathHeight = height * 1.5f;

            float offsetX = (width - deathWidth) / 2f;
            float offsetY = (height - deathHeight) / 2f;

            batch.draw(frame,
                    position.x + offsetX,
                    position.y + offsetY,
                    deathWidth,
                    deathHeight);
        } else if (alive) {
            sprite.draw(batch);
        }

        for (Bullet b : bullets) {
            b.draw(batch);
        }
    }

    private TextureRegion getFrameForDirection(float dx, float dy) {
        animationTimer += Gdx.graphics.getDeltaTime();
        if (animationTimer >= animationSpeed) {
            animIndex = (animIndex + 1) % 2;
            animationTimer = 0f;
        }

        if (Math.abs(dx) < 0.2f && Math.abs(dy) < 0.2f) {
            return directionFrames[0 + animIndex];
        }

        float angle = (float) Math.toDegrees(Math.atan2(dy, dx));
        if (angle < 0)
            angle += 360;

        int baseIndex;

        if (angle >= 75 && angle < 105) {
            baseIndex = 0;
        } else if (angle >= 255 && angle < 285) {
            baseIndex = 2;
        } else if (angle >= 165 && angle < 195) {
            baseIndex = 8;
        } else if (angle >= 120 && angle < 150) {
            baseIndex = 6;
        } else if (angle >= 105 && angle < 120) {
            baseIndex = 4;
        } else if (angle >= 210 && angle < 240) {
            baseIndex = 18;
        } else if (angle >= 240 && angle < 255) {
            baseIndex = 16;
        } else if (angle >= 195 && angle < 210) {
            baseIndex = 20;
        } else if (angle >= 345 || angle < 15) {
            baseIndex = 14;
        } else if (angle >= 30 && angle < 60) {
            baseIndex = 12;
        } else if (angle >= 60 && angle < 75) {
            baseIndex = 10;
        } else if (angle >= 300 && angle < 330) {
            baseIndex = 24;
        } else if (angle >= 285 && angle < 300) {
            baseIndex = 22;
        } else if (angle >= 330 && angle < 345) {
            baseIndex = 26;
        } else if (angle >= 15 && angle < 30) {
            baseIndex = 14;
        } else if (angle >= 150 && angle < 165) {
            baseIndex = 8;
        } else {
            baseIndex = 0;
        }

        return directionFrames[baseIndex + animIndex];
    }

    @Override
    public Rectangle getBounds() {
        if (isDead || isReallyDead) {
            return new Rectangle(0, 0, 0, 0);
        }
        return new Rectangle(position.x, position.y, width, height);
    }

    @Override
    public Vector2 getPosition() {
        return new Vector2(position.x, position.y);
    }

    @Override
    public void setPosition(float x, float y) {
        position.set(x, y);
        if (sprite != null) {
            sprite.setPosition(x, y);
        }
    }

    public void destroy() {
        if (isDead || isReallyDead) {
            return;
        }

        if (enemyDead_Sound != null) {
            enemyDead_Sound.play();
        }
        isDead = true;
        stateTime = 0f;
    }

    public void setBaseFrame() {
        smoothDirection.set(0, 1);
        animIndex = 0;
        animationTimer = 0f;
        currentFrame = directionFrames[0];
        if (sprite != null) {
            sprite.setRegion(currentFrame);
        }
    }

    public void shooting(Player player) {
        // Используем BulletFactory для создания пули
        Bullet bullet = bulletFactory.createEnemyBullet(
                position,
                width,
                height,
                player.getPosition(),
                600f);
        bullets.add(bullet);
    }

    public void setMovementPattern(IMovementPattern newPattern) {
        this.pattern = newPattern;
        this.time = 0f;
        setBaseFrame();

        if (newPattern instanceof com.kursch.patterns.DiveAttackPattern) {
            this.inFormation = false;
        } else if (newPattern instanceof com.kursch.patterns.FormationEntryPattern
                || newPattern instanceof com.kursch.patterns.GalagaFormationPattern
                || newPattern instanceof com.kursch.patterns.LeftRightPattern) {
            this.inFormation = true;
        } else {
            this.inFormation = false;
        }
    }

    // Геттеры и сеттеры
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

    public float getPhaseOffset() {
        return phaseOffset;
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

    @Override
    public void dispose() {
        if (enemyDead_Sound != null) {
            enemyDead_Sound.dispose();
        }
    }
}