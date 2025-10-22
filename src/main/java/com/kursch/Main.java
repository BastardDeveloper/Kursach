package com.kursch;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.kursch.menu.MainMenuScreen;
import com.kursch.entities.Player;
import com.kursch.factory.EnemyFactory;
import com.kursch.graphics.Background;
import com.kursch.menu.GameScreen;

public class Main extends Game {
    public SpriteBatch spriteBatch;
    public FitViewport viewport;

    public Player player;
    public Background background;
    public EnemyFactory enemyManager;
    public GameScreen gameScreen;
    Array<Sprite> enemySprites;

    @Override
    public void create() {

        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(700, 1000);
        setScreen(new MainMenuScreen(this));
        background = new Background(viewport);
        gameScreen = new GameScreen(this);
        player = new Player(viewport, gameScreen);
        enemyManager = new EnemyFactory(viewport, gameScreen);

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
        if (getScreen() != null) {
            getScreen().resize(width, height);
        }
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        background.dispose();
        player.dispose();
        enemyManager.dispose();
    }
}