package com.kursch.menu;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kursch.Main;

public class GameScreen implements Screen {
    private final Main game;
    private final BitmapFont font;

    public GameScreen(Main game) {
        this.game = game;
        font = new BitmapFont();
        font.getData().setScale(10.0f);
    }

    @Override
    public void render(float delta) {
        // Обновление
        game.background.update(delta);
        game.player.update(delta);
        game.EnemyManager.update(delta, game.player);

        // Очистка экрана
        ScreenUtils.clear(Color.BLACK);

        // Применение viewport и отрисовка
        game.viewport.apply();
        game.spriteBatch.setProjectionMatrix(game.viewport.getCamera().combined);

        game.spriteBatch.begin();

        game.background.draw(game.spriteBatch);
        game.player.draw(game.spriteBatch);
        game.EnemyManager.draw(game.spriteBatch);
        font.draw(game.spriteBatch, "this is game sreen", 10, 890);

        game.spriteBatch.end();
    }

    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void show() {
    }

    @Override
    public void dispose() {
        font.dispose();
    }
}