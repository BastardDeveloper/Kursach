package com.kursch;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.math.MathUtils;

public class Background {
    private Texture[] textures;
    private FitViewport viewport;
    private float scrollY = 0f;
    private float time = 0f;

    public Background(FitViewport viewport) {
        this.viewport = viewport;
        textures = new Texture[] {
                new Texture("bg1.png"),
                new Texture("bg2.png"),
                new Texture("bg3.png")
        };
    }

    public void update(float delta, float speed) {
        scrollY += speed * delta;
        if (scrollY > 1000)
            scrollY = 0;
        time += delta;
    }

    public void draw(SpriteBatch batch) {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        float worldHeight = viewport.getWorldHeight();
        float worldWidth = viewport.getWorldWidth();

        // Каждому фону — своя альфа по синусоиде со смещением фазы
        float alpha1 = 0.5f + 0.5f * MathUtils.sin(time) * 5;
        float alpha2 = 0.5f + 0.5f * MathUtils.sin(time + 2.09f);
        float alpha3 = 0.5f + 0.5f * MathUtils.sin(time + 4.18f);

        drawLayer(batch, textures[0], alpha1, worldWidth, worldHeight);
        drawLayer(batch, textures[1], alpha2, worldWidth, worldHeight);
        drawLayer(batch, textures[2], alpha3, worldWidth, worldHeight);
    }

    private void drawLayer(SpriteBatch batch, Texture texture, float alpha, float worldWidth, float worldHeight) {
        batch.setColor(1f, 1f, 1f, alpha);

        batch.draw(texture, 0, -scrollY, worldWidth, worldHeight);
        batch.draw(texture, 0, -scrollY + worldHeight, worldWidth, worldHeight);

        // Сбрасываем цвет после рисования слоя
        batch.setColor(1f, 1f, 1f, 1f);
    }

    public void dispose() {
        for (Texture t : textures)
            t.dispose();
    }
}
