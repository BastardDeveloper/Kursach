package com.kursch;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.kursch.patterns.MovementPattern;

public class blueRed_Bazz_Enemy extends Enemy {

    private static final Texture enemyAtlas = new Texture(Gdx.files.internal("ВеселаяНарезка.png"));

    // --- UP ---
    private static final TextureRegion enemy_up = new TextureRegion(enemyAtlas, 127, 73, 16, 15);
    private static final TextureRegion enemy_up_2 = new TextureRegion(enemyAtlas, 109, 73, 16, 15);

    // --- LEFT ---
    private static final TextureRegion enemy_Left1 = new TextureRegion(enemyAtlas, 91, 73, 16, 15);
    private static final TextureRegion enemy_Left1_2 = new TextureRegion(enemyAtlas, 73, 73, 16, 15);
    private static final TextureRegion enemy_Left2 = new TextureRegion(enemyAtlas, 55, 73, 16, 15);
    private static final TextureRegion enemy_Left2_2 = new TextureRegion(enemyAtlas, 37, 73, 16, 15);
    private static final TextureRegion enemy_FullLeft = new TextureRegion(enemyAtlas, 19, 73, 16, 15);
    private static final TextureRegion enemy_FullLeft_2 = new TextureRegion(enemyAtlas, 1, 73, 16, 15);

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

    static {
        // Отзеркаливание вправо
        enemy_Right1.flip(true, false);
        enemy_Right1_2.flip(true, false);
        enemy_Right2.flip(true, false);
        enemy_Right2_2.flip(true, false);
        enemy_FullRight.flip(true, false);
        enemy_FullRight_2.flip(true, false);

        // Отзеркаливание вниз (по вертикали)
        enemy_Down.flip(false, true);
        enemy_Down_2.flip(false, true);
    }

    // --- Все фреймы ---
    private static final TextureRegion[] directionFrames = new TextureRegion[] {
            // вверх
            enemy_up, enemy_up_2,
            // вниз
            enemy_Down, enemy_Down_2,
            // влево
            enemy_Left1, enemy_Left1_2, enemy_Left2, enemy_Left2_2, enemy_FullLeft, enemy_FullLeft_2,
            // вправо
            enemy_Right1, enemy_Right1_2, enemy_Right2, enemy_Right2_2, enemy_FullRight, enemy_FullRight_2
    };

    public blueRed_Bazz_Enemy(MovementPattern pattern, float x, float y) {
        super(directionFrames, pattern, x, y);
    }
}
