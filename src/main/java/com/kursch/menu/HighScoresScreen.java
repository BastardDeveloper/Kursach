package com.kursch.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kursch.utils.HighScoreManager;
import com.kursch.Main;

import java.util.List;

public class HighScoresScreen implements Screen {
    private final Main game;
    private final Stage stage;
    private final Skin skin;
    private final Screen previousScreen;

    public HighScoresScreen(final Main game, Screen previousScreen) {
        this.game = game;
        this.previousScreen = previousScreen;

        stage = new Stage(game.viewport, game.spriteBatch);
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("assets/uiskin.json"));

        HighScoreManager highScoreManager = new HighScoreManager();
        List<HighScoreManager.HighScoreEntry> highScores = highScoreManager.getHighScores();

        // Главная таблица
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        // Заголовок
        Label.LabelStyle titleStyle = new Label.LabelStyle(skin.getFont("default-font"), Color.GOLD);
        Label titleLabel = new Label("HIGH SCORES", titleStyle);
        titleLabel.setFontScale(2.5f);
        mainTable.add(titleLabel).padBottom(40).row();

        // Таблица рекордов
        Table scoresTable = new Table();
        Label.LabelStyle scoreStyle = new Label.LabelStyle(skin.getFont("default-font"), Color.WHITE);

        if (highScores.isEmpty()) {
            Label noScoresLabel = new Label("No scores yet!", scoreStyle);
            noScoresLabel.setFontScale(1.5f);
            scoresTable.add(noScoresLabel);
        } else {
            for (int i = 0; i < highScores.size(); i++) {
                HighScoreManager.HighScoreEntry entry = highScores.get(i);

                // Цвет для топ-3
                Color rankColor = Color.WHITE;
                if (i == 0)
                    rankColor = Color.GOLD;
                else if (i == 1)
                    rankColor = Color.LIGHT_GRAY;
                else if (i == 2)
                    rankColor = Color.ORANGE;

                Label.LabelStyle rankStyle = new Label.LabelStyle(skin.getFont("default-font"), rankColor);
                Label rankLabel = new Label((i + 1) + ".", rankStyle);
                Label nameLabel = new Label(entry.playerName, rankStyle);
                Label scoreLabel = new Label(String.valueOf(entry.score), rankStyle);

                rankLabel.setFontScale(1.5f);
                nameLabel.setFontScale(1.5f);
                scoreLabel.setFontScale(1.5f);

                scoresTable.add(rankLabel).padRight(20);
                scoresTable.add(nameLabel).left().padRight(40);
                scoresTable.add(scoreLabel).left().padBottom(15).row();
            }
        }

        mainTable.add(scoresTable).padBottom(50).row();

        // Кнопка назад
        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(previousScreen);
            }
        });

        mainTable.add(backButton).width(300).height(80);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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
        skin.dispose();
    }
}