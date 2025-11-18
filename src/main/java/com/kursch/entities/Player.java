package com.kursch.entities;

/* Таски
 * 1. Унести логику создание пуль и звука в другие класы.
 * 2. Унести логику анимации смерти в друой класс
 * 3. Почистить методы которые в родительском класе
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.kursch.menu.GameScreen;
import com.badlogic.gdx.audio.Sound;
import com.kursch.graphics.animation.AnimationManager;

public class Player extends AGameObject {

    private Animation<TextureRegion> deathAnimation;
    private int lives;
    private Array<Bullet> bullets;
    private TextureRegion bulletTexture;
    private GameScreen gameScreen;
    private FitViewport viewport;
    private boolean isPlayingDeathAnimation = false; // Временное состояние
    private float stateTime = 0f;

    public Player(FitViewport viewport, GameScreen gameScreen, AnimationManager animationManager) {
        this.viewport = viewport;
        this.lives = 3;
        this.gameScreen = gameScreen;
        this.deathAnimation = animationManager.get("player_death");

        sprite = new Sprite(new TextureRegion(new Texture("SpriteSheet1_Enemies.png"), 100, 78, 250, 250));
        bulletTexture = new TextureRegion(new Texture("ВеселаяНарезка.png"), 312, 139, 4, 9);
        sprite.setPosition(viewport.getWorldWidth() / 2f - 17.5f, 20);
        sprite.setSize(35, 35);
        bullets = new Array<>();
    }

    @Override
    public void update(float delta) {

        stateTime += delta;
        if (isPlayingDeathAnimation) {
            if (deathAnimation.isAnimationFinished(stateTime)) {
                isPlayingDeathAnimation = false;
                stateTime = 0f;
            }
            return;
        }

        float speed = 850 * delta;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
            sprite.translateX(-speed);
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            sprite.translateX(speed);
        if (Gdx.input.isKeyPressed(Input.Keys.UP))
            sprite.translateY(speed);
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
            sprite.translateY(-speed);

        sprite.setX(MathUtils.clamp(sprite.getX(), 0, viewport.getWorldWidth() - sprite.getWidth()));
        sprite
                .setY(MathUtils.clamp(sprite.getY(), 0, viewport.getWorldHeight() - sprite.getHeight()));

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
            shoot();

        for (Bullet b : bullets)
            b.update(delta);
    }

    private void shoot() {
        Sprite bulletSprite = new Sprite(bulletTexture);
        bulletSprite.setSize(20, 37);
        float startX = (sprite.getX() + (sprite.getWidth() - bulletSprite.getWidth()) / 2) - 4;
        float startY = sprite.getY() + sprite.getHeight();
        Vector2 direction = new Vector2(0, 1); // напрям тільки вгору

        Sound bulletSound = Gdx.audio.newSound(Gdx.files.internal("BulletSound.mp3"));
        Bullet.loadSound(bulletSound);

        bullets.add(new Bullet(bulletSprite, startX, startY, direction, 2000f));
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (isPlayingDeathAnimation) {
            TextureRegion frame = deathAnimation.getKeyFrame(stateTime, false);
            batch.draw(frame, sprite.getX() - 20, sprite.getY() - 2, sprite.getWidth() * 2,
                    sprite.getHeight() * 2);
        } else {
            sprite.draw(batch);
        }

        for (Bullet b : bullets)
            b.draw(batch);
    }

    public void addHit() {
        lives--;
        isPlayingDeathAnimation = true;
        stateTime = 0f;
        if (lives <= 0) {
            gameScreen.triggerGameOver();
            alive = false;

        }
    }

    public Array<Bullet> getPlayerBullets() {
        return bullets;
    }

    @Override
    public void dispose() {
        bulletTexture.getTexture().dispose();
    }
}
