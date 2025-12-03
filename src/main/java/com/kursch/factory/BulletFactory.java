package com.kursch.factory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.kursch.entities.Bullet;

public class BulletFactory {
    private TextureRegion bulletTexture;
    private Sound bulletSound;

    public BulletFactory(Texture spriteSheet) {
        // Текстура пули из спрайт-листа
        this.bulletTexture = new TextureRegion(spriteSheet, 312, 139, 4, 9);
        this.bulletSound = Gdx.audio.newSound(Gdx.files.internal("assets/BulletSound.mp3"));
    }

    /**
     * Создает пулю врага, направленную в игрока
     */
    public Bullet createEnemyBullet(Vector2 enemyPosition, float enemyWidth, float enemyHeight,
            Vector2 playerPosition, float speed) {

        if (bulletSound != null) {
            bulletSound.play(0.2f);
        }

        Sprite bulletSprite = new Sprite(bulletTexture);
        bulletSprite.setSize(20, 32);

        float startX = enemyPosition.x + enemyWidth / 2f - bulletSprite.getWidth() / 2f;
        float startY = enemyPosition.y + enemyHeight / 2f - bulletSprite.getHeight() / 2f;

        Vector2 direction = new Vector2(
                playerPosition.x - enemyPosition.x,
                playerPosition.y - enemyPosition.y).nor();

        return new Bullet(bulletSprite, startX, startY, direction, speed);
    }

    /**
     * Создает пулю игрока
     */
    public Bullet createPlayerBullet(Vector2 playerPosition, float playerWidth, float playerHeight,
            Vector2 direction, float speed) {
        if (bulletSound != null) {
            bulletSound.play(0.2f);
        }
        Sprite bulletSprite = new Sprite(bulletTexture);
        bulletSprite.setSize(20, 32);

        float startX = playerPosition.x + playerWidth / 2f - bulletSprite.getWidth() / 2f;
        float startY = playerPosition.y + playerHeight / 2f - bulletSprite.getHeight() / 2f;

        return new Bullet(bulletSprite, startX, startY, direction, speed);
    }
}