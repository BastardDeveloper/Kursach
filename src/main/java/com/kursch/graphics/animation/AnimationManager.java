package com.kursch.graphics.animation;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ObjectMap;

public class AnimationManager {

    private Texture spriteSheet;
    private ObjectMap<String, Animation<TextureRegion>> animations;

    public AnimationManager() {
        spriteSheet = new Texture("ВеселаяНарезка.png");
        animations = new ObjectMap<>();
        loadAnimations();
    }

    private void loadAnimations() {

        animations.put("player_death", createAnimation(146, 2, 30, 30, 4, 34, 0.2f));

    }

    private Animation<TextureRegion> createAnimation(int startX, int startY, int frameWidth, int frameHeight,
            int frameCount, int frameOffset, float frameDuration) {
        TextureRegion[] frames = new TextureRegion[frameCount];
        for (int i = 0; i < frameCount; i++) {
            int x = startX + i * frameOffset;
            frames[i] = new TextureRegion(spriteSheet, x, startY, frameWidth, frameHeight);
        }
        return new Animation<>(frameDuration, frames);
    }

    public Animation<TextureRegion> get(String name) {
        return animations.get(name);
    }

    public void dispose() {
        spriteSheet.dispose();
    }
}
