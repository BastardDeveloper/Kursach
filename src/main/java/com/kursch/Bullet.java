package com.kursch;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Bullet {

    private Sprite bulletSprite;
    private Vector2 position;
    private float speed = 2000f;
    private boolean active = true;

    public Bullet(Sprite bulletSprite, float startX, float startY) {
        this.bulletSprite = bulletSprite;
        this.position = new Vector2(startX, startY);
    }

    public void update(float delta) {
        position.y += speed * delta;
        if (position.y > 900) { // можна підлаштувати під розмір вікна
            active = false;
        }
    }

    public void draw(SpriteBatch batch) {
        batch.draw(bulletSprite, position.x, position.y,
                bulletSprite.getWidth(), bulletSprite.getHeight());
    }

    public boolean isActive() {
        return active;
    }

    public void destroy() {
        active = false;
    }

    public Rectangle getBounds() {
        return new Rectangle(position.x, position.y,
                bulletSprite.getWidth(), bulletSprite.getHeight());
    }
}
