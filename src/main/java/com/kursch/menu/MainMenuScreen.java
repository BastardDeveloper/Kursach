package com.kursch.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kursch.Background;
import com.kursch.Main;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class MainMenuScreen implements Screen {
    private final Stage stage;
    private final Skin skin;
    private final SpriteBatch batch;
    Background background;

    public MainMenuScreen(final Main game) {
        batch = new SpriteBatch();

        background = new Background(game.viewport);

        stage = new Stage(game.viewport);
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json"));

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        TextButton playButton = new TextButton("Game", skin);
        TextButton exitButton = new TextButton("Exit", skin);

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

        table.add(playButton).width(300).height(80).pad(20).row();
        table.add(exitButton).width(300).height(80).pad(20);

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void render(float delta) {
        batch.begin();
        background.update(delta, 200);

        // Рисуем фон
        background.draw(batch);
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        skin.dispose();
    }
}