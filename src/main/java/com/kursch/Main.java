package com.kursch;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.kursch.menu.MainMenuScreen;

public class Main extends Game {
    public SpriteBatch spriteBatch; // добавил public
    public FitViewport viewport; // добавил public
    Texture enemiesTexture;
    Texture bulletTexture;
    public Player player; // добавил public
    public Background background; // добавил public
    public EnemyManager EnemyManager;
    Sprite playerSprite;
    Sprite enemySprite;
    TextureRegion playerTexture;
    Array<Sprite> enemySprites;

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(1600, 900);

        setScreen(new MainMenuScreen(this));
        player = new Player(viewport);
        background = new Background(viewport);
        EnemyManager = new EnemyManager(viewport);

    }

    @Override
    public void render() {
        super.render();

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