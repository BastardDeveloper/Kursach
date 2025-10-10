package com.kursch.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kursch.Background;
import com.kursch.EnemyManager;
import com.kursch.Main;
import com.kursch.Player;

public class GameScreen implements Screen {
    private final Main game;
    private final BitmapFont font;
    private final BitmapFont pauseFont;
    private int score = 0;
    private boolean gameOver = false;
    private boolean paused = false;
    private Music gameMusic;
    private float musicFadeTimer = 0f;
    private final float FADE_DURATION = 3f;
    private final float TARGET_VOLUME = 0.5f;
    private ShapeRenderer shapeRenderer;

    public GameScreen(Main game) {
        this.game = game;

        font = new BitmapFont();
        font.getData().setScale(10f);

        pauseFont = new BitmapFont();
        pauseFont.getData().setScale(8f);
        pauseFont.setColor(Color.WHITE);

        shapeRenderer = new ShapeRenderer();

        gameMusic = Gdx.audio.newMusic(Gdx.files.internal("SaundForGame.mp3"));
        gameMusic.setLooping(true);
        gameMusic.setVolume(0f);

        game.player = new Player(game.viewport, this);
        game.enemyManager = new EnemyManager(game.viewport);
        game.background = new Background(game.viewport);

        score = 0;
        gameOver = false;
        paused = false;
    }

    public void triggerGameOver() {
        if (!gameOver) {
            gameOver = true;
            if (gameMusic != null)
                gameMusic.stop();
            game.setScreen(new GameOverScreen(game, this, score));
        }
    }

    public void addScore(int points) {
        score += points;
    }

    @Override
    public void render(float delta) {
        // Обработка паузы клавишей ESC
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && !gameOver) {
            paused = !paused;
            if (paused && gameMusic != null) {
                gameMusic.pause();
            } else if (!paused && gameMusic != null) {
                gameMusic.play();
            }
        }

        // Плавное увеличение громкости музыки
        if (musicFadeTimer < FADE_DURATION && gameMusic != null && !gameOver && !paused) {
            musicFadeTimer += delta;
            float volume = Math.min(musicFadeTimer / FADE_DURATION, 1f) * TARGET_VOLUME;
            gameMusic.setVolume(volume);
        }

        // Обновление игры, если не пауза и не game over
        if (!gameOver && !paused) {
            game.background.update(delta, 300);
            game.player.update(delta);
            game.enemyManager.update(delta, game.player);
        }

        // Отрисовка
        ScreenUtils.clear(Color.BLACK);
        game.viewport.apply();
        game.spriteBatch.setProjectionMatrix(game.viewport.getCamera().combined);

        game.spriteBatch.begin();
        game.background.draw(game.spriteBatch);
        game.player.draw(game.spriteBatch);
        game.enemyManager.draw(game.spriteBatch);
        font.draw(game.spriteBatch, "Score: " + score, 10, 890);
        game.spriteBatch.end();

        // Если пауза, рисуем оверлей с кнопками
        if (paused)
            drawPauseOverlay();
    }

    private void drawPauseOverlay() {
        // Полупрозрачный фон
        Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
        Gdx.gl.glBlendFunc(Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.setProjectionMatrix(game.viewport.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.7f);
        shapeRenderer.rect(0, 0, game.viewport.getWorldWidth(), game.viewport.getWorldHeight());
        shapeRenderer.end();

        Gdx.gl.glDisable(Gdx.gl.GL_BLEND);

        // Текст паузы
        game.spriteBatch.begin();

        float centerX = game.viewport.getWorldWidth() / 2;
        float centerY = game.viewport.getWorldHeight() / 2;

        pauseFont.getData().setScale(5f);
        pauseFont.draw(game.spriteBatch, "PAUSE", centerX - 80, centerY + 120);
        pauseFont.draw(game.spriteBatch, "ESC - Continue", centerX - 140, centerY + 40);
        pauseFont.draw(game.spriteBatch, "M - Main Menu", centerX - 140, centerY - 40);

        game.spriteBatch.end();

        // Проверка клавиш для меню
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            if (gameMusic != null)
                gameMusic.stop();
            game.setScreen(new MainMenuScreen(game));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height, true);
    }

    @Override
    public void pause() {
        if (gameMusic != null)
            gameMusic.pause();
    }

    @Override
    public void resume() {
        if (gameMusic != null && !gameOver && !paused)
            gameMusic.play();
    }

    @Override
    public void hide() {
        if (gameMusic != null)
            gameMusic.pause();
    }

    @Override
    public void show() {
        if (gameMusic != null && !gameOver && !paused) {
            musicFadeTimer = 0f;
            gameMusic.setVolume(0f);
            gameMusic.play();
        }
    }

    @Override
    public void dispose() {
        font.dispose();
        pauseFont.dispose();
        shapeRenderer.dispose();
        if (gameMusic != null)
            gameMusic.dispose();
    }
}
