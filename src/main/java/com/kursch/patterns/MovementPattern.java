package com.kursch.patterns;

import com.badlogic.gdx.math.Vector2;

public interface MovementPattern {
    Vector2 getPosition(float time);

    /**
     * Optional: whether the pattern is complete at given time (e.g., entry
     * finished).
     * Default: not complete.
     */
    default boolean isComplete(float time) {
        return false;
    }
}