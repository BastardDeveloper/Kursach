package com.kursch;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Player {

    private Array<Bullet> bullets;
    private TextureRegion playerTexture;
    private TextureRegion bulletTexture;

    private Sprite playerSprite;
    private FitViewport viewport;

    public Player(FitViewport viewport) {
        this.viewport = viewport;

        playerTexture = new TextureRegion(new Texture("SpriteSheet1_Enemies.png"), 100, 78, 250, 250);
        bulletTexture = new TextureRegion(new Texture("ВеселаяНарезка.png"), 312, 139, 4, 9);

        playerSprite = new Sprite(playerTexture);
        playerSprite.setSize(40, 40);
        playerSprite.setPosition(viewport.getWorldWidth() / 2f - 17.5f, 20);

        bullets = new Array<>();
    }

    public void update(float delta) {
        float speed = 850 * delta;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
            playerSprite.translateX(-speed);
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            playerSprite.translateX(speed);
        if (Gdx.input.isKeyPressed(Input.Keys.UP))
            playerSprite.translateY(speed);
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
            playerSprite.translateY(-speed);

        // незя в лево право
        playerSprite.setX(MathUtils.clamp(playerSprite.getX(), 0,
                viewport.getWorldWidth() - playerSprite.getWidth()));

        // незя верх в низ
        playerSprite.setY(MathUtils.clamp(playerSprite.getY(), 0,
                viewport.getWorldHeight() - playerSprite.getHeight()));
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
            shoot();

        for (Bullet b : bullets) {
            b.update(delta);
        }

    }

    private void shoot() {
        Sprite bulletSprite = new Sprite(bulletTexture);
        bulletSprite.setSize(12, 27);
        float startX = (playerSprite.getX() + (playerSprite.getWidth() - bulletSprite.getWidth()) / 2) - 4;
        float startY = playerSprite.getY() + playerSprite.getHeight();
        bullets.add(new Bullet(bulletSprite, startX, startY));
    }

    public void draw(SpriteBatch batch) {
        playerSprite.draw(batch);
        for (Bullet b : bullets) {
            b.draw(batch);
        }
    }

    public Array<Bullet> getPlayerBullets() {
        return bullets;
    }

    public void dispose() {
        playerTexture.getTexture().dispose();
        bulletTexture.getTexture().dispose();
    }
}
