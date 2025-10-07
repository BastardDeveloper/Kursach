package com.kursch;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Main implements ApplicationListener {
    SpriteBatch spriteBatch;
    FitViewport viewport;

    Texture enemiesTexture;
    Texture bulletTexture;

    Player player;
    Background background;
    EnemyManager EnemyManager;

    Sprite playerSprite;
    Sprite enemySprite;

    TextureRegion playerTexture;

    Array<Sprite> enemySprites;

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(1600, 900);

        player = new Player(viewport);
        background = new Background(viewport);
        EnemyManager = new EnemyManager(viewport);

    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();

        background.update(delta);
        player.update(delta);
        EnemyManager.update(delta, player);

        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        spriteBatch.begin();

        background.draw(spriteBatch);
        player.draw(spriteBatch);
        EnemyManager.draw(spriteBatch);

        spriteBatch.end();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        background.dispose();
        player.dispose();
        EnemyManager.dispose();
    }
}