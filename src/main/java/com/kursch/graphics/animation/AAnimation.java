package com.kursch.graphics.animation;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.kursch.menu.GameScreen;

public class AAnimation {
    private Animation<TextureRegion> deadAnimation;
    private GameScreen gameScreen;
    private FitViewport viewport;
    private float stateTime = 0f; // счётчик времени для анимации
    Texture SpriteSheet = new Texture("ВеселаяНарезка.png");

    private int startX;
    private int startY;
    private int frameWidth;
    private int frameHeight;
    private int frameCount;
    private int frameOffset;

    TextureRegion[] frames = new TextureRegion[frameCount];
}