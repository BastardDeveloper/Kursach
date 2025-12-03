package com.kursch.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.Gdx;

public class Bullet {

    private Sprite bulletSprite;
    private Vector2 position;
    private Vector2 velocity;
    private boolean active = true;

    public Bullet(Sprite bulletSprite, float startX, float startY, Vector2 direction, float speed) {
        this.bulletSprite = bulletSprite;
        this.position = new Vector2(startX, startY);
        this.velocity = new Vector2(direction).nor().scl(speed);

        this.bulletSprite.setPosition(startX, startY);

    }

    public void update(float delta) {
        if (!active)
            return;

        position.x += velocity.x * delta;
        position.y += velocity.y * delta;

        bulletSprite.setPosition(position.x, position.y);

        if (position.y < -50 ||
                position.y > Gdx.graphics.getHeight() * 2 ||
                position.x < -50 ||
                position.x > Gdx.graphics.getWidth() * 2) {

            active = false;
        }
    }

    public void draw(SpriteBatch batch) {
        if (active) {
            bulletSprite.draw(batch);
        }
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

    public Vector2 getPosition() {
        return position;
    }
}
