package com.kursch.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kursch.Main;
import com.kursch.entities.Player;
import com.kursch.factory.EnemyFactory;
import com.kursch.graphics.Background;
import com.kursch.graphics.animation.AnimationManager;
import com.badlogic.gdx.graphics.GL20;

public class GameScreen implements Screen {
    private final Main game;
    private final BitmapFont font;
    private final BitmapFont pauseFont;
    private final BitmapFont sideFont;
    private final BitmapFont sideTitleFont;
    private int score = 0;
    private boolean gameOver = false;
    private boolean paused = false;
    private Music gameMusic;
    private float musicFadeTimer = 0f;
    private final float FADE_DURATION = 3f;
    private final float TARGET_VOLUME = 0.5f;
    private ShapeRenderer shapeRenderer;
    public AnimationManager animationManager;

    public GameScreen(Main game) {
        this.game = game;

        font = new BitmapFont();
        font.getData().setScale(5f);

        pauseFont = new BitmapFont();
        pauseFont.getData().setScale(5f);
        pauseFont.setColor(Color.WHITE);

        // Шрифты для боковых панелей
        sideFont = new BitmapFont();
        sideFont.setColor(Color.WHITE);
        sideFont.getData().setScale(1.2f);

        sideTitleFont = new BitmapFont();
        sideTitleFont.setColor(Color.CYAN);
        sideTitleFont.getData().setScale(1.5f);

        shapeRenderer = new ShapeRenderer();

        gameMusic = Gdx.audio.newMusic(Gdx.files.internal("assets/SaundForGame.mp3"));
        gameMusic.setLooping(true);
        gameMusic.setVolume(0f);

        animationManager = new AnimationManager();
        game.player = new Player(game.viewport, this, animationManager);
        game.enemyManager = new EnemyFactory(game.viewport, this, animationManager);
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

        // Отрисовка игрового мира
        ScreenUtils.clear(Color.BLACK);
        game.viewport.apply();
        game.spriteBatch.setProjectionMatrix(game.viewport.getCamera().combined);

        game.spriteBatch.begin();
        game.background.draw(game.spriteBatch);
        game.player.draw(game.spriteBatch);
        game.enemyManager.draw(game.spriteBatch);
        game.spriteBatch.end();

        // Отрисовка UI (счет и боковые панели)
        game.uiViewport.apply();
        game.spriteBatch.setProjectionMatrix(game.uiViewport.getCamera().combined);
        game.spriteBatch.begin();

        // Рисуем боковые панели
        drawLeftPanel();
        drawRightPanel();

        game.spriteBatch.end();

        // Если пауза, рисуем оверлей с кнопками
        if (paused)
            drawPauseOverlay();

    }

    // Рисуем левую панель
    private void drawLeftPanel() {
        float uiWidth = game.uiViewport.getWorldWidth();
        float uiHeight = game.uiViewport.getWorldHeight();

        float leftX = 20;
        float startY = uiHeight / 2;

        sideFont.getData().setScale(2f);
        sideFont.setColor(Color.YELLOW);
        startY -= 300;
        sideFont.draw(game.spriteBatch, "Score: " + score, leftX, startY);

        sideFont.setColor(Color.WHITE);
        startY += 600;
        sideFont.draw(game.spriteBatch, "ESC - Pause", leftX, startY);

    }

    // Рисуем правую панель
    private void drawRightPanel() {
        float uiWidth = game.uiViewport.getWorldWidth();
        float uiHeight = game.uiViewport.getWorldHeight();

        float rightX = uiWidth - 250;
        float startY = uiHeight / 2;

        sideFont.setColor(Color.RED);
        sideFont.getData().setScale(2f);
        sideFont.draw(game.spriteBatch, "Health: " + (game.player != null ? game.player.getLives() : 0), rightX,
                uiHeight / 2 - 300);

        // Цели
        startY += 300;
        sideFont.setColor(Color.GREEN);
        sideFont.draw(game.spriteBatch, "OBJECTIVES:", rightX, startY);

        sideFont.setColor(Color.WHITE);
        sideFont.getData().setScale(1.5f);
        startY -= 45;
        sideFont.draw(game.spriteBatch, "Survive!", rightX, startY);
        startY -= 35;
        sideFont.draw(game.spriteBatch, "High score", rightX, startY);
        startY -= 35;
        sideFont.draw(game.spriteBatch, "No damage", rightX, startY);
    }

    // Рисуем оверлей паузы

    private void drawPauseOverlay() {
        // Полупрозрачный фон на игровом viewport
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        game.uiViewport.apply();
        shapeRenderer.setProjectionMatrix(game.uiViewport.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.7f);
        shapeRenderer.rect(0, 0, game.uiViewport.getWorldWidth(), game.uiViewport.getWorldHeight());
        shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);

        // Текст паузы на игровом viewport
        game.spriteBatch.setProjectionMatrix(game.uiViewport.getCamera().combined);
        game.spriteBatch.begin();

        float centerX = game.uiViewport.getWorldWidth() / 2;
        float centerY = game.viewport.getWorldHeight() / 2;

        pauseFont.getData().setScale(2f);

        // Задаём ширину строки = ширина viewport’a
        float width = game.uiViewport.getWorldWidth();

        pauseFont.draw(game.spriteBatch, "PAUSE", 0, centerY + 100, width, Align.center, false);

        pauseFont.draw(game.spriteBatch, "S - Setting", 0, centerY - 100, width, Align.center, false);

        pauseFont.draw(game.spriteBatch, "ESC - Continue", 0, centerY, width, Align.center, false);

        pauseFont.draw(game.spriteBatch, "M - Main Menu", 0, centerY - 200, width, Align.center, false);

        game.spriteBatch.end();

        // Проверка клавиш для меню
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            if (gameMusic != null)
                gameMusic.stop();
            game.setScreen(new MainMenuScreen(game));
            dispose();
        }

        // Проверка клавиш для настроек
        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            if (gameMusic != null)
                game.setScreen(new SettingsScreen(game, gameMusic, GameScreen.this));
        }
    }

    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height, true);
        game.uiViewport.update(width, height, true);
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
        sideFont.dispose();
        sideTitleFont.dispose();
        shapeRenderer.dispose();
        if (gameMusic != null)
            gameMusic.dispose();
    }
}