package com.kursch.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class AGameObject {
    protected Sprite sprite;
    protected boolean alive = true;

    public abstract void update(float delta);

    public abstract void draw(SpriteBatch batch);

    // создаем хит бокс
    public Rectangle getBounds() {
        if (sprite != null) {
            return new Rectangle(sprite.getX(), sprite.getY(),
                    sprite.getWidth(), sprite.getHeight());
        }
        return new Rectangle(0, 0, 0, 0);
    }

    // передать позицию
    public Vector2 getPosition() {
        if (sprite != null) {
            return new Vector2(sprite.getX(), sprite.getY());
        }
        return new Vector2(0, 0);
    }

    // задать позицию
    public void setPosition(float x, float y) {
        if (sprite != null) {
            sprite.setPosition(x, y);
        }
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public float getWidth() {
        return sprite != null ? sprite.getWidth() : 0;
    }

    public float getHeight() {
        return sprite != null ? sprite.getHeight() : 0;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void dispose() {
    }
}