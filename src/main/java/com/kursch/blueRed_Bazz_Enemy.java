package com.kursch;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.kursch.patterns.MovementPattern;

public class blueRed_Bazz_Enemy extends Enemy {
    private static Texture enemyAtlas;

    static {
        Pixmap pixmap = new Pixmap(Gdx.files.internal("ВеселаяНарезка.png"));
        enemyAtlas = new Texture(pixmap);
        pixmap.dispose();

        enemyAtlas.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        enemyAtlas.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
    }

    // ВАЖНО: Добавляем отступы! Вместо (x, y, w, h) используем (x+0.5, y+0.5, w-1,
    // h-1)
    // Это предотвращает захват соседних пикселей

    // --- UP ---
    private static final TextureRegion enemy_up = createRegion(127, 73, 16, 15);
    private static final TextureRegion enemy_up_2 = createRegion(109, 73, 16, 15);

    // --- LEFT ---
    private static final TextureRegion enemy_Left1 = createRegion(91, 73, 16, 15);
    private static final TextureRegion enemy_Left1_2 = createRegion(73, 73, 16, 15);
    private static final TextureRegion enemy_Left2 = createRegion(55, 73, 16, 15);
    private static final TextureRegion enemy_Left2_2 = createRegion(37, 73, 16, 15);
    private static final TextureRegion enemy_FullLeft = createRegion(19, 73, 16, 15);
    private static final TextureRegion enemy_FullLeft_2 = createRegion(1, 73, 16, 15);

    // --- RIGHT (зеркала LEFT) ---
    private static final TextureRegion enemy_Right1 = new TextureRegion(enemy_Left1);
    private static final TextureRegion enemy_Right1_2 = new TextureRegion(enemy_Left1_2);
    private static final TextureRegion enemy_Right2 = new TextureRegion(enemy_Left2);
    private static final TextureRegion enemy_Right2_2 = new TextureRegion(enemy_Left2_2);
    private static final TextureRegion enemy_FullRight = new TextureRegion(enemy_FullLeft);
    private static final TextureRegion enemy_FullRight_2 = new TextureRegion(enemy_FullLeft_2);

    // --- DOWN (зеркало UP) ---
    private static final TextureRegion enemy_Down = new TextureRegion(enemy_up);
    private static final TextureRegion enemy_Down_2 = new TextureRegion(enemy_up_2);

    // Вспомогательный метод для создания TextureRegion с padding
    private static TextureRegion createRegion(int x, int y, int width, int height) {
        // Добавляем отступ 0.5 пикселя с каждой стороны
        // Используем float координаты для точности
        TextureRegion region = new TextureRegion(enemyAtlas);

        // Вычисляем UV координаты с отступами
        float u = (x + 0.5f) / enemyAtlas.getWidth();
        float v = (y + 0.5f) / enemyAtlas.getHeight();
        float u2 = (x + width - 0.5f) / enemyAtlas.getWidth();
        float v2 = (y + height - 0.5f) / enemyAtlas.getHeight();

        region.setRegion(u, v, u2, v2);

        return region;
    }

    static {
        // Отзеркаливание вправо
        enemy_Right1.flip(true, false);
        enemy_Right1_2.flip(true, false);
        enemy_Right2.flip(true, false);
        enemy_Right2_2.flip(true, false);
        enemy_FullRight.flip(true, false);
        enemy_FullRight_2.flip(true, false);

        // Отзеркаливание вниз
        enemy_Down.flip(false, true);
        enemy_Down_2.flip(false, true);
    }

    // --- Все фреймы ---
    private static final TextureRegion[] directionFrames = new TextureRegion[] {
            enemy_up, enemy_up_2,
            enemy_Down, enemy_Down_2,
            enemy_Left1, enemy_Left1_2, enemy_Left2, enemy_Left2_2, enemy_FullLeft, enemy_FullLeft_2,
            enemy_Right1, enemy_Right1_2, enemy_Right2, enemy_Right2_2, enemy_FullRight, enemy_FullRight_2
    };

    public blueRed_Bazz_Enemy(MovementPattern pattern, float x, float y) {
        super(directionFrames, pattern, x, y);
    }

    public static void disposeStatic() {
        if (enemyAtlas != null) {
            enemyAtlas.dispose();
        }
    }
}