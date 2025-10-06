package com.kursch;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.kursch.patterns.MovementPattern;

public class Enemy {
    private Sprite sprite;
    private float time;
    private MovementPattern pattern;
    private boolean active = true;
    private Player player;

    public Enemy(TextureRegion region, MovementPattern pattern, float x, float y) {
        this.pattern = pattern;
        sprite = new Sprite(region);
        sprite.setSize(30, 30);
        sprite.setPosition(x, y);
    }

    public void update(float delta) {
        time += delta;
        Vector2 newPos = pattern.getPosition(time);
        sprite.setPosition(newPos.x, newPos.y);
        if (newPos.y < -50) {
            active = false;
        }

    }

    public void draw(SpriteBatch batch) {
        if (active)
            sprite.draw(batch);
    }

    public Rectangle getBounds() {
        return sprite.getBoundingRectangle();
    }

    public void destroy() {
        active = false;
    }

    public boolean isActive() {
        return active;
    }

}
