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
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.kursch.Main;

public class GameOverScreen implements Screen {
    private final Main game;
    private final Stage stage;
    private final Skin skin;
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;

    private float fadeAlpha = 0f;
    private float fadeSpeed = 0.8f;
    private boolean fadingIn = true;

    private final int score;
    private final GameScreen previousScreen;

    public GameOverScreen(final Main game, GameScreen previousScreen, int score) {

        this.game = game;
        this.previousScreen = previousScreen;
        this.score = score;

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        stage = new Stage(new FitViewport(1600, 900));
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Создаем главную таблицу
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        // Заголовок Game Over
        Label.LabelStyle titleStyle = new Label.LabelStyle(skin.getFont("default-font"), Color.RED);
        Label titleLabel = new Label("GAME OVER", titleStyle);
        titleLabel.setFontScale(3f);

        // Счет
        Label.LabelStyle scoreStyle = new Label.LabelStyle(skin.getFont("default-font"), Color.WHITE);
        Label scoreLabel = new Label("Score: " + score, scoreStyle);
        scoreLabel.setFontScale(2f);

        // Кнопки
        TextButton restartButton = new TextButton("Restart", skin);
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
        mainTable.add(scoreLabel).padBottom(60).row();
        mainTable.add(restartButton).width(300).height(80).pad(15).row();
        mainTable.add(menuButton).width(300).height(80).pad(15).row();
        mainTable.add(exitButton).width(300).height(80).pad(15);

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
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
            previousScreen.render(0); // delta = 0 чтобы заморозить анимации
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