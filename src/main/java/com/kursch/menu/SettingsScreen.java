package com.kursch.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.kursch.Main;

public class SettingsScreen implements Screen {
    private final Main game;
    private final Stage stage;
    private final Skin skin;
    private final Music menuMusic;

    public SettingsScreen(final Main game, final Music menuMusic) {
        this.game = game;
        this.menuMusic = menuMusic;
        this.stage = new Stage(game.viewport);
        this.skin = new Skin(Gdx.files.internal("uiskin.json"));
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Label volumeLabel = new Label("Music Volume", skin);
        Slider volumeSlider = new Slider(0f, 1f, 0.01f, false, skin);
        volumeSlider.setValue(menuMusic.getVolume()); // поточна гучність

        TextButton backButton = new TextButton("Back", skin);

        // Реакція на зміну повзунка
        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                menuMusic.setVolume(volumeSlider.getValue());
            }
        });

        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        });

        table.add(volumeLabel).pad(10).row();
        table.add(volumeSlider).width(300).pad(10).row();
        table.add(backButton).width(200).height(60).pad(20);
    }

    @Override
    public void render(float delta) {
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
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
