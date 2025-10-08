package com.kursch;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Background {
    private Texture texture;
    private FitViewport viewport;
    private float scrollY = 0f;

    public Background(FitViewport viewport) {
        this.viewport = viewport;
        texture = new Texture("Bg1.png");
    }

    public void update(float delta, float speed) {
        scrollY += speed * delta;
        if (scrollY > 900)
            scrollY = 0;
    }

    public void draw(SpriteBatch batch) {

        ScreenUtils.clear(Color.GREEN);
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        float worldHeight = viewport.getWorldHeight();
        float worldWidth = viewport.getWorldWidth();

        // Рисуем фон дважды, чтобы создать эффект бесконечного скролла
        batch.draw(texture, 0, -scrollY, worldWidth, worldHeight);
        batch.draw(texture, 0, -scrollY + worldHeight, worldWidth, worldHeight);
    }

    public void dispose() {
        texture.dispose();
    }
}