package com.kursch.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
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
    private Music menuMusic;

    public MainMenuScreen(final Main game) {
        batch = new SpriteBatch();
        background = new Background(game.viewport);
        stage = new Stage(game.viewport);
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Загружаем и настраиваем музыку
        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("SaundMenu.mp3"));
        menuMusic.setLooping(true);
        menuMusic.setVolume(0.5f); // Громкость от 0.0 до 1.0
        menuMusic.play();

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
        background.update(delta, 400);
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
        if (menuMusic != null) {
            menuMusic.pause();
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        if (menuMusic != null) {
            menuMusic.play();
        }
    }

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        skin.dispose();
        if (menuMusic != null) {
            menuMusic.dispose();
        }
    }
}