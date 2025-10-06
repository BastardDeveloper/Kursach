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
        bulletTexture = new TextureRegion(new Texture("26482.png"), 307, 136, 15, 15);

        playerSprite = new Sprite(playerTexture);
        playerSprite.setSize(35, 35);
        playerSprite.setPosition(viewport.getWorldWidth() / 2f - 17.5f, 20);

        bullets = new Array<>();
    }

    public void update(float delta) {
        float speed = 450 * delta;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
            playerSprite.translateX(-speed);
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            playerSprite.translateX(speed);
        playerSprite.setX(MathUtils.clamp(playerSprite.getX(), 0,
                viewport.getWorldWidth() - playerSprite.getWidth()));

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
            shoot();

        for (Bullet b : bullets) {
            b.update(delta);
        }

    }

    private void shoot() {
        Bullet newBullet = new Bullet(new Sprite(bulletTexture),
                playerSprite.getX() + playerSprite.getWidth() / 2f - 2,
                playerSprite.getY() + playerSprite.getHeight());
        bullets.add(newBullet);
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
