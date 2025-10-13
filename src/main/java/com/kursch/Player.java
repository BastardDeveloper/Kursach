package com.kursch;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.kursch.menu.GameScreen;
import com.badlogic.gdx.audio.Sound;

public class Player {

    private Animation<TextureRegion> deadAnimation;
    private int lives;
    private Array<Bullet> bullets;
    private TextureRegion playerTexture;
    private TextureRegion bulletTexture;
    private GameScreen gameScreen;
    private Sprite playerSprite;
    private FitViewport viewport;
    private float stateTime = 0f; // счётчик времени для анимации
    private boolean isDead = false; // пример состояния игрока
    private float width = 35, height = 35;

    public Player(FitViewport viewport, GameScreen gameScreen) {

        this.viewport = viewport;
        this.lives = 3;
        this.gameScreen = gameScreen;
        playerTexture = new TextureRegion(new Texture("SpriteSheet1_Enemies.png"), 100, 78, 250, 250);
        bulletTexture = new TextureRegion(new Texture("ВеселаяНарезка.png"), 312, 139, 4, 9);
        Texture deathSpriteSheet = new Texture("ВеселаяНарезка.png");

        playerSprite = new Sprite(playerTexture);
        playerSprite.setSize(width, height);
        playerSprite.setPosition(viewport.getWorldWidth() / 2f - 17.5f, 20);

        int startX = 146;
        int startY = 2;
        int frameWidth = 30;
        int frameHeight = 30;
        int frameCount = 4;
        int frameOffset = 34;

        // FIX: Array size should match frameCount
        TextureRegion[] frames = new TextureRegion[frameCount];

        // Нарезаем вручную
        for (int i = 0; i < frameCount; i++) {
            int x = startX + i * frameOffset;
            frames[i] = new TextureRegion(deathSpriteSheet, x, startY, frameWidth, frameHeight);
        }

        // Создаём анимацию с длительностью 0.2 секунды на кадр
        deadAnimation = new Animation<>(0.2f, frames);

        bullets = new Array<>();
    }

    public void update(float delta) {
        stateTime += delta;

        // Если проигрывается анимация смерти — ничего не делаем
        if (isDead) {
            // Проверяем, закончилась ли анимация
            if (deadAnimation.isAnimationFinished(stateTime)) {
                isDead = false; // Возвращаемся в нормальное состояние
                stateTime = 0f; // Сбрасываем таймер
            }
            return;
        }

        float speed = 850 * delta;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
            playerSprite.translateX(-speed);
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            playerSprite.translateX(speed);
        if (Gdx.input.isKeyPressed(Input.Keys.UP))
            playerSprite.translateY(speed);
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
            playerSprite.translateY(-speed);

        playerSprite.setX(MathUtils.clamp(playerSprite.getX(), 0, viewport.getWorldWidth() - playerSprite.getWidth()));
        playerSprite
                .setY(MathUtils.clamp(playerSprite.getY(), 0, viewport.getWorldHeight() - playerSprite.getHeight()));

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
            shoot();

        for (Bullet b : bullets)
            b.update(delta);
    }

    private void shoot() {
        Sprite bulletSprite = new Sprite(bulletTexture);
        bulletSprite.setSize(20, 37);
        float startX = (playerSprite.getX() + (playerSprite.getWidth() - bulletSprite.getWidth()) / 2) - 4;
        float startY = playerSprite.getY() + playerSprite.getHeight();
        Vector2 direction = new Vector2(0, 1); // напрям тільки вгору

        Sound bulletSound = Gdx.audio.newSound(Gdx.files.internal("BulletSound.mp3"));
        Bullet.loadSound(bulletSound);

        bullets.add(new Bullet(bulletSprite, startX, startY, direction, 2000f));
    }

    public void draw(SpriteBatch batch) {
        if (isDead) {
            TextureRegion frame = deadAnimation.getKeyFrame(stateTime, false);
            batch.draw(frame, playerSprite.getX(), playerSprite.getY(), playerSprite.getWidth() * 2,
                    playerSprite.getHeight() * 2);
        } else {
            playerSprite.draw(batch);
        }

        for (Bullet b : bullets)
            b.draw(batch);
    }

    public void addHit() {
        lives--;
        isDead = true;
        stateTime = 0f;
        if (lives <= 0) {
            gameScreen.triggerGameOver();

        }
    }

    public Rectangle getBounds() {
        return new Rectangle(playerSprite.getX(), playerSprite.getY(), playerSprite.getWidth(),
                playerSprite.getHeight());
    }

    public Array<Bullet> getPlayerBullets() {
        return bullets;
    }

    public Vector2 getPosition() {
        return new Vector2(playerSprite.getX(), playerSprite.getY());
    }

    public void dispose() {
        playerTexture.getTexture().dispose();
        bulletTexture.getTexture().dispose();
    }
}
