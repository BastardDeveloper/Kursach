package com.kursch.entities.enemyCollection;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.kursch.entities.Enemy;
import com.kursch.patterns.IMovementPattern;
import com.kursch.graphics.animation.AnimationManager;

public class yellowBlue_Bazz_Enemy extends Enemy {
    private static Texture enemyAtlas;

    static {
        Pixmap pixmap = new Pixmap(Gdx.files.internal("assets/ВеселаяНарезка.png"));
        enemyAtlas = new Texture(pixmap);
        pixmap.dispose();
        enemyAtlas.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        enemyAtlas.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
    }

    // --- UP ---
    private static final TextureRegion enemy_up = createRegion(127, 91, 16, 15);
    private static final TextureRegion enemy_up_2 = createRegion(109, 91, 16, 15);

    // --- LEFT (верхний полукруг) ---
    private static final TextureRegion enemy_Left1 = createRegion(91, 91, 16, 15);
    private static final TextureRegion enemy_Left1_2 = createRegion(73, 91, 16, 15);
    private static final TextureRegion enemy_Left2 = createRegion(55, 91, 16, 15);
    private static final TextureRegion enemy_Left2_2 = createRegion(37, 91, 16, 15);
    private static final TextureRegion enemy_FullLeft = createRegion(19, 91, 16, 15);
    private static final TextureRegion enemy_FullLeft_2 = createRegion(1, 91, 16, 15);

    // --- RIGHT (зеркала LEFT - верхний полукруг) ---
    private static final TextureRegion enemy_Right1 = new TextureRegion(enemy_Left1);
    private static final TextureRegion enemy_Right1_2 = new TextureRegion(enemy_Left1_2);
    private static final TextureRegion enemy_Right2 = new TextureRegion(enemy_Left2);
    private static final TextureRegion enemy_Right2_2 = new TextureRegion(enemy_Left2_2);
    private static final TextureRegion enemy_FullRight = new TextureRegion(enemy_FullLeft);
    private static final TextureRegion enemy_FullRight_2 = new TextureRegion(enemy_FullLeft_2);

    // --- LEFT BOTTOM (нижний полукруг - перевернутые LEFT) ---
    private static final TextureRegion enemy_LeftBottom1 = new TextureRegion(enemy_Left1);
    private static final TextureRegion enemy_LeftBottom1_2 = new TextureRegion(enemy_Left1_2);
    private static final TextureRegion enemy_LeftBottom2 = new TextureRegion(enemy_Left2);
    private static final TextureRegion enemy_LeftBottom2_2 = new TextureRegion(enemy_Left2_2);
    private static final TextureRegion enemy_FullLeftBottom = new TextureRegion(enemy_FullLeft);
    private static final TextureRegion enemy_FullLeftBottom_2 = new TextureRegion(enemy_FullLeft_2);

    // --- RIGHT BOTTOM (нижний полукруг - перевернутые RIGHT) ---
    private static final TextureRegion enemy_RightBottom1 = new TextureRegion(enemy_Right1);
    private static final TextureRegion enemy_RightBottom1_2 = new TextureRegion(enemy_Right1_2);
    private static final TextureRegion enemy_RightBottom2 = new TextureRegion(enemy_Right2);
    private static final TextureRegion enemy_RightBottom2_2 = new TextureRegion(enemy_Right2_2);
    private static final TextureRegion enemy_FullRightBottom = new TextureRegion(enemy_FullRight);
    private static final TextureRegion enemy_FullRightBottom_2 = new TextureRegion(enemy_FullRight_2);

    // --- DOWN (зеркало UP) ---
    private static final TextureRegion enemy_Down = new TextureRegion(enemy_up);
    private static final TextureRegion enemy_Down_2 = new TextureRegion(enemy_up_2);

    // Вспомогательный метод для создания TextureRegion с padding
    private static TextureRegion createRegion(int x, int y, int width, int height) {
        TextureRegion region = new TextureRegion(enemyAtlas);
        float u = (x + 0.5f) / enemyAtlas.getWidth();
        float v = (y + 0.5f) / enemyAtlas.getHeight();
        float u2 = (x + width - 0.5f) / enemyAtlas.getWidth();
        float v2 = (y + height - 0.5f) / enemyAtlas.getHeight();
        region.setRegion(u, v, u2, v2);
        return region;
    }

    static {
        // Отзеркаливание вправо (верхний полукруг)
        enemy_Right1.flip(true, false);
        enemy_Right1_2.flip(true, false);
        enemy_Right2.flip(true, false);
        enemy_Right2_2.flip(true, false);
        enemy_FullRight.flip(true, false);
        enemy_FullRight_2.flip(true, false);

        // Отзеркаливание вниз (нижний полукруг слева)
        enemy_LeftBottom1.flip(false, true);
        enemy_LeftBottom1_2.flip(false, true);
        enemy_LeftBottom2.flip(false, true);
        enemy_LeftBottom2_2.flip(false, true);
        enemy_FullLeftBottom.flip(false, true);
        enemy_FullLeftBottom_2.flip(false, true);

        // Отзеркаливание по обеим осям (нижний полукруг справа)
        enemy_RightBottom1.flip(true, true);
        enemy_RightBottom1_2.flip(true, true);
        enemy_RightBottom2.flip(true, true);
        enemy_RightBottom2_2.flip(true, true);
        enemy_FullRightBottom.flip(true, true);
        enemy_FullRightBottom_2.flip(true, true);

        // Отзеркаливание вниз
        enemy_Down.flip(false, true);
        enemy_Down_2.flip(false, true);
    }

    // --- Все фреймы ---
    private static final TextureRegion[] directionFrames = new TextureRegion[] {
            enemy_up, enemy_up_2, // 0-1: вверх
            enemy_Down, enemy_Down_2, // 2-3: вниз
            enemy_Left1, enemy_Left1_2, // 4-5: влево слабо (верх)
            enemy_Left2, enemy_Left2_2, // 6-7: влево средне (верх)
            enemy_FullLeft, enemy_FullLeft_2, // 8-9: полностью влево
            enemy_Right1, enemy_Right1_2, // 10-11: вправо слабо (верх)
            enemy_Right2, enemy_Right2_2, // 12-13: вправо средне (верх)
            enemy_FullRight, enemy_FullRight_2, // 14-15: полностью вправо
            enemy_LeftBottom1, enemy_LeftBottom1_2, // 16-17: влево слабо (низ)
            enemy_LeftBottom2, enemy_LeftBottom2_2, // 18-19: влево средне (низ)
            enemy_FullLeftBottom, enemy_FullLeftBottom_2, // 20-21: полностью влево (низ)
            enemy_RightBottom1, enemy_RightBottom1_2, // 22-23: вправо слабо (низ)
            enemy_RightBottom2, enemy_RightBottom2_2, // 24-25: вправо средне (низ)
            enemy_FullRightBottom, enemy_FullRightBottom_2 // 26-27: полностью вправо (низ)
    };

    public yellowBlue_Bazz_Enemy(IMovementPattern pattern, float x, float y, AnimationManager animationManager) {
        super(directionFrames, pattern, x, y, 100, animationManager);
    }

    public static void disposeStatic() {
        if (enemyAtlas != null) {
            enemyAtlas.dispose();
        }
    }
}