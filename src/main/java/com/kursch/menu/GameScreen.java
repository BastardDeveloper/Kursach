package com.kursch.menu;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kursch.Background;
import com.kursch.EnemyManager;
import com.kursch.Main;
import com.kursch.Player;

public class GameScreen implements Screen {
    private final Main game;
    private final BitmapFont font;
    private int score = 0;
    private boolean gameOver = false;

    public GameScreen(Main game) {
        this.game = game;
        font = new BitmapFont();
        font.getData().setScale(10.0f);

        // Создаём новые объекты игры
        game.player = new Player(game.viewport, this);
        game.enemyManager = new EnemyManager(game.viewport);
        game.background = new Background(game.viewport);

        score = 0;
        gameOver = false;
    }

    // Метод для проверки Game Over (вызывайте его когда игрок умирает)
    public void triggerGameOver() {
        if (!gameOver) {

            gameOver = true;
            game.setScreen(new GameOverScreen(game, this, score));
        }
    }

    // Метод для обновления счета
    public void addScore(int points) {
        score += points;
    }

    @Override
    public void render(float delta) {
        // Обновление (только если игра не завершена)
        if (!gameOver) {
            game.background.update(delta, 400);
            game.player.update(delta);
            game.enemyManager.update(delta, game.player);

        }

        // Очистка экрана
        ScreenUtils.clear(Color.BLACK);

        // Применение viewport и отрисовка
        game.viewport.apply();
        game.spriteBatch.setProjectionMatrix(game.viewport.getCamera().combined);

        game.spriteBatch.begin();
        game.background.draw(game.spriteBatch);
        game.player.draw(game.spriteBatch);
        game.enemyManager.draw(game.spriteBatch);

        font.draw(game.spriteBatch, "Score: " + score, 10, 890);

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