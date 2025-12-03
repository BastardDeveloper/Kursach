package com.kursch.entities;

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
import com.kursch.entities.enemyCollection.blueRed_Bazz_Enemy;
import com.kursch.factory.BulletFactory;
import com.kursch.graphics.animation.AnimationManager;

public class Player extends AGameObject {

    private Animation<TextureRegion> deathAnimation;
    private BulletFactory bulletFactory;
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
        this.deathAnimation = animationManager.get("assets/player_death");

        sprite = new Sprite(new TextureRegion(new Texture("assets/SpriteSheet1_Enemies.png"), 100, 78, 250, 250));
        bulletTexture = new TextureRegion(new Texture("assets/ВеселаяНарезка.png"), 312, 139, 4, 9);
        sprite.setPosition(viewport.getWorldWidth() / 2f - 17.5f, 20);
        sprite.setSize(35, 35);
        bullets = new Array<>();
        bulletFactory = new BulletFactory(animationManager.getSpriteSheet());

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

        sprite.setX(MathUtils.clamp(sprite.getX(), 0, viewport.getWorldWidth() - sprite.getWidth()));
        sprite.setY(MathUtils.clamp(sprite.getY(), 0, viewport.getWorldHeight() - sprite.getHeight()));

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
            shoot();

        for (Bullet b : bullets)
            b.update(delta);
    }

    private void shoot() {
        if (bullets.size < 2) {

            Vector2 direction = new Vector2(0, 1); // напрям тільки вгору
            Bullet bullet = bulletFactory.createPlayerBullet(getPosition(), sprite.getWidth(), sprite.getHeight(),
                    direction, 2000);

            bullets.add(bullet);

        }

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

    public int getLives() {
        return lives;
    }

    public Array<Bullet> getPlayerBullets() {
        return bullets;
    }

    @Override
    public void dispose() {
        bulletTexture.getTexture().dispose();
    }
}
