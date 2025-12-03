package com.kursch.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kursch.Main;
import com.kursch.utils.HighScoreManager;

public class GameOverScreen implements Screen {
    private final Stage stage;
    private final Skin skin;
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;

    private float fadeAlpha = 0f;
    private float fadeSpeed = 0.8f;
    private boolean fadingIn = true;

    private final GameScreen previousScreen;
    private final HighScoreManager highScoreManager;
    private final int finalScore;
    private boolean isHighScore;

    private TextField nameField;
    private TextButton submitButton;
    private Table mainTable;

    public GameOverScreen(final Main game, GameScreen previousScreen, int score) {

        this.previousScreen = previousScreen;
        this.highScoreManager = new HighScoreManager();
        this.finalScore = score;

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        stage = new Stage(game.uiViewport, game.spriteBatch);

        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("assets/uiskin.json"));

        // Проверяем, является ли счет рекордом
        isHighScore = highScoreManager.isHighScore(score);

        // Создаем главную таблицу
        mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        if (isHighScore) {
            showNameInputScreen(game);
        } else {
            showFinalScreen(game, "Player", false, 0);
        }

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    private void showNameInputScreen(final Main game) {
        mainTable.clear();

        // Заголовок
        Label.LabelStyle titleStyle = new Label.LabelStyle(skin.getFont("default-font"), Color.GOLD);
        Label titleLabel = new Label("NEW HIGH SCORE!", titleStyle);
        titleLabel.setFontScale(2.5f);

        // Счет
        Label.LabelStyle scoreStyle = new Label.LabelStyle(skin.getFont("default-font"), Color.WHITE);
        Label scoreLabel = new Label("Score: " + finalScore, scoreStyle);
        scoreLabel.setFontScale(2f);

        // Инструкция
        Label.LabelStyle instructionStyle = new Label.LabelStyle(skin.getFont("default-font"), Color.LIGHT_GRAY);
        Label instructionLabel = new Label("Enter your name:", instructionStyle);
        instructionLabel.setFontScale(1.3f);

        // Поле ввода
        nameField = new TextField("", skin);
        nameField.setMaxLength(15);
        nameField.setMessageText("Your Name");

        // Кнопка подтверждения
        submitButton = new TextButton("Submit", skin);
        submitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String playerName = nameField.getText().trim();
                if (playerName.isEmpty()) {
                    playerName = "Player";
                }

                // Сохраняем рекорд
                highScoreManager.addScore(playerName, finalScore);
                int rank = highScoreManager.getScoreRank(playerName, finalScore);

                // Показываем финальный экран
                showFinalScreen(game, playerName, true, rank);
            }
        });

        // Компоновка
        mainTable.add(titleLabel).padBottom(30).row();
        mainTable.add(scoreLabel).padBottom(50).row();
        mainTable.add(instructionLabel).padBottom(20).row();
        mainTable.add(nameField).width(400).height(60).padBottom(30).row();
        mainTable.add(submitButton).width(300).height(80);

        // Устанавливаем фокус на поле ввода
        stage.setKeyboardFocus(nameField);
    }

    private void showFinalScreen(final Main game, String playerName, boolean wasHighScore, int rank) {
        mainTable.clear();

        // Заголовок Game Over
        Label.LabelStyle titleStyle = new Label.LabelStyle(skin.getFont("default-font"), Color.RED);
        Label titleLabel = new Label("GAME OVER", titleStyle);
        titleLabel.setFontScale(3f);

        // Счет
        Label.LabelStyle scoreStyle = new Label.LabelStyle(skin.getFont("default-font"), Color.WHITE);
        Label scoreLabel = new Label("Score: " + finalScore, scoreStyle);
        scoreLabel.setFontScale(2f);

        // Сообщение о рекорде
        Label highScoreLabel = null;
        if (wasHighScore && rank > 0) {
            Label.LabelStyle highScoreStyle = new Label.LabelStyle(skin.getFont("default-font"), Color.YELLOW);
            highScoreLabel = new Label(playerName + " - Rank #" + rank, highScoreStyle);
            highScoreLabel.setFontScale(1.5f);
        }

        // Кнопки
        TextButton restartButton = new TextButton("Restart", skin);
        TextButton highScoresButton = new TextButton("High Scores", skin);
        TextButton menuButton = new TextButton("Main Menu", skin);
        TextButton exitButton = new TextButton("Exit", skin);

        // Обработчики кнопок
        restartButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new GameScreen(game));
                dispose();
            }
        });

        highScoresButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new HighScoresScreen(game, GameOverScreen.this));
            }
        });

        menuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        });

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        // Компоновка элементов
        mainTable.add(titleLabel).padBottom(40).row();
        mainTable.add(scoreLabel).padBottom(20).row();

        if (highScoreLabel != null) {
            mainTable.add(highScoreLabel).padBottom(40).row();
        } else {
            mainTable.add().padBottom(40).row();
        }

        mainTable.add(restartButton).width(300).height(80).pad(10).row();
        mainTable.add(highScoresButton).width(300).height(80).pad(10).row();
        mainTable.add(menuButton).width(300).height(80).pad(10).row();
        mainTable.add(exitButton).width(300).height(80).pad(10);
    }

    @Override
    public void render(float delta) {
        // Обновляем fade эффект
        if (fadingIn) {
            fadeAlpha += fadeSpeed * delta;
            if (fadeAlpha >= 1f) {
                fadeAlpha = 1f;
                fadingIn = false;
            }
        }

        // Рисуем предыдущий экран (замороженный)
        if (previousScreen != null) {
            previousScreen.render(0);
        }

        // Полупрозрачный затемняющий слой
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.7f * fadeAlpha);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();

        // Обновляем прозрачность UI
        stage.getRoot().getColor().a = fadeAlpha;

        // Рисуем UI
        stage.act(delta);
        stage.draw();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        if (previousScreen != null) {
            previousScreen.resize(width, height);
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        shapeRenderer.dispose();
        skin.dispose();
        if (previousScreen != null) {
            previousScreen.dispose();
        }
    }
}