package com.kursch.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
    private final Music menuMusic;
    private final Screen previousScreen;
    private final Stage stage;
    private final Skin skin;
    private final ShapeRenderer shapeRenderer;

    public SettingsScreen(final Main game, final Music menuMusic, final Screen previousScreen) {
        this.game = game;
        this.menuMusic = menuMusic;
        this.previousScreen = previousScreen;
        this.stage = new Stage(game.uiViewport);
        this.skin = new Skin(Gdx.files.internal("assets/uiskin.json"));
        this.shapeRenderer = new ShapeRenderer();

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Label titleLabel = new Label("Settings", skin, "default");
        titleLabel.setFontScale(1.5f);

        Label volumeLabel = new Label("Music Volume", skin);
        final Slider volumeSlider = new Slider(0f, 1f, 0.01f, false, skin);
        volumeSlider.setValue(menuMusic.getVolume());
        final Label volumeValueLabel = new Label(String.format("%.0f%%", menuMusic.getVolume() * 100), skin);

        TextButton backButton = new TextButton("Back", skin);

        // Реакція на зміну повзунка
        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float value = volumeSlider.getValue();
                menuMusic.setVolume(value);
                volumeValueLabel.setText(String.format("%.0f%%", value * 100));
            }
        });

        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(previousScreen);
            }
        });

        table.add(titleLabel).colspan(2).pad(20).row();
        table.add(volumeLabel).pad(10);
        table.add(volumeValueLabel).pad(10).row();
        table.add(volumeSlider).colspan(2).width(300).pad(10).row();
        table.add(backButton).colspan(2).width(200).height(60).pad(20);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        // Рендерим предыдущий экран как фон
        previousScreen.render(delta);

        // Включаем прозрачность
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // Рисуем полупрозрачный темный оверлей
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.7f); // Черный с прозрачностью 70%
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();

        // Рисуем UI настроек поверх
        stage.act(Math.min(delta, 1 / 30f));
        stage.draw();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
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
    public void dispose() {
        stage.dispose();
        skin.dispose();
        shapeRenderer.dispose();
    }
}