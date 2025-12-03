package com.kursch.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kursch.Main;
import com.kursch.graphics.Background;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;

public class MainMenuScreen implements Screen {
    private final Stage stage;
    private final Skin skin;

    private final SpriteBatch batch;
    private final Main game;
    public float volume;
    private Background background;
    private Music menuMusic;
    private BitmapFont rulesFont;
    private BitmapFont rulesTitleFont;

    public MainMenuScreen(final Main game) {
        this.game = game;
        batch = game.spriteBatch;

        // Шрифты для правил
        rulesFont = new BitmapFont();
        rulesFont.setColor(Color.WHITE);
        rulesFont.getData().setScale(1.2f);

        rulesTitleFont = new BitmapFont();
        rulesTitleFont.setColor(Color.CYAN);
        rulesTitleFont.getData().setScale(2f);

        // Используем viewport для меню (как раньше)
        background = new Background(game.viewport);
        stage = new Stage(game.viewport);
        skin = new Skin(Gdx.files.internal("assets/uiskin.json"));

        // Загружаем и настраиваем музыку
        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("assets/SaundMenu.mp3"));
        menuMusic.setLooping(true);
        menuMusic.setVolume(0.5f);
        menuMusic.play();

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        TextButton playButton = new TextButton("play", skin);
        TextButton HighScoresButton = new TextButton("HighScores", skin);
        TextButton exitButton = new TextButton("Exit", skin);
        TextButton settingsButton = new TextButton("Settings", skin);

        settingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new SettingsScreen(game, menuMusic, MainMenuScreen.this));
            }
        });

        HighScoresButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new HighScoresScreen(game, MainMenuScreen.this));
            }
        });

        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new GameScreen(game));
                dispose();
            }
        });

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        table.add(settingsButton).width(300).height(80).pad(20).row();
        table.add(playButton).width(300).height(80).pad(20).row();
        table.add(HighScoresButton).width(300).height(80).row();
        table.add(exitButton).width(300).height(80).pad(20);

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        if (menuMusic != null && !menuMusic.isPlaying()) {
            menuMusic.play();
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Рендерим фон игрового viewport
        game.viewport.apply();
        batch.setProjectionMatrix(game.viewport.getCamera().combined);
        batch.begin();
        background.update(delta, 200);
        background.draw(batch);
        batch.end();

        // Рендерим меню
        stage.act(delta);
        stage.draw();

        // Рендерим правила слева - используем UI viewport
        game.uiViewport.apply();
        batch.setProjectionMatrix(game.uiViewport.getCamera().combined);
        batch.begin();
        drawGameRules();
        batch.end();
    }

    private void drawGameRules() {
        // Получаем размеры UI viewport
        float uiWidth = game.uiViewport.getWorldWidth();
        float uiHeight = game.uiViewport.getWorldHeight();

        float leftX = 30;
        float startY = uiHeight - 80; // Используем высоту UI viewport

        // Заголовок
        rulesTitleFont.draw(batch, "GAME RULES", leftX, startY);

        startY -= 60;
        rulesFont.setColor(Color.LIGHT_GRAY);

        // Правила игры
        rulesFont.draw(batch, "HOW TO PLAY:", leftX, startY);
        startY -= 40;

        rulesFont.setColor(Color.WHITE);
        rulesFont.draw(batch, " Move: Arrow Keys(<--  -->)", leftX, startY);
        startY -= 35;

        rulesFont.draw(batch, " Shoot: SPACE", leftX, startY);
        startY -= 35;

        rulesFont.draw(batch, " Pause: ESC", leftX, startY);
        startY -= 60;

        // Цели
        rulesFont.setColor(Color.LIGHT_GRAY);
        rulesFont.draw(batch, "OBJECTIVES:", leftX, startY);
        startY -= 40;

        rulesFont.setColor(Color.WHITE);
        rulesFont.draw(batch, "1. Destroy enemies", leftX, startY);
        startY -= 35;

        rulesFont.draw(batch, "2. Survive waves", leftX, startY);
        startY -= 35;

        rulesFont.draw(batch, "3. Get high score", leftX, startY);
        startY -= 60;

        rulesFont.getData().setScale(1.2f);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        game.uiViewport.update(width, height, true); // Обновляем UI viewport
    }

    @Override
    public void pause() {
        if (menuMusic != null) {
            menuMusic.pause();
        }
    }

    @Override
    public void resume() {
        if (menuMusic != null) {
            menuMusic.play();
        }
    }

    @Override
    public void hide() {
        // НЕ останавливаем музыку при переходе в настройки
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        rulesFont.dispose();
        rulesTitleFont.dispose();
        if (menuMusic != null) {
            menuMusic.dispose();
        }
    }
}