package com.kursch;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
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

    private Array<Bullet> bullets;
    private TextureRegion playerTexture;
    private TextureRegion bulletTexture;
    private GameScreen gameScreen;
    private Sprite playerSprite;
    private FitViewport viewport;

    public Player(FitViewport viewport, GameScreen gameScreen) {
        this.viewport = viewport;
        this.gameScreen = gameScreen;
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
        bulletSprite.setSize(20, 37);
        float startX = (playerSprite.getX() + (playerSprite.getWidth() - bulletSprite.getWidth()) / 2) - 4;
        float startY = playerSprite.getY() + playerSprite.getHeight();
        Vector2 direction = new Vector2(0, 1); // напрям тільки вгору

        Sound bulletSound = Gdx.audio.newSound(Gdx.files.internal("BulletSound.mp3"));
        Bullet.loadSound(bulletSound);

        bullets.add(new Bullet(bulletSprite, startX, startY, direction, 2000f));
    }

    public void draw(SpriteBatch batch) {
        playerSprite.draw(batch);
        for (Bullet b : bullets) {
            b.draw(batch);
        }
    }

    public void destroy() {
        gameScreen.triggerGameOver();
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
