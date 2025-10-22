package com.kursch.patterns;

import com.badlogic.gdx.math.Vector2;

public interface IMovementPattern {
    Vector2 getPosition(float time);

    default boolean isComplete(float time) {
        return false;
    }
}