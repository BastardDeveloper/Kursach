package com.kursch.patterns;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class DiveAttackPattern implements MovementPattern {

    private final Vector2 start;
    private final Vector2 target;
    private final float amplitude; // амплитуда синусоиды
    private final float frequency; // количество волн
    private final float duration; // сколько секунд длится атака
    // elapsedTime removed: we use the elapsed parameter from Enemy.update()

    public DiveAttackPattern(Vector2 start, Vector2 target, float duration) {
        this.start = new Vector2(start);
        this.target = new Vector2(target);
        this.duration = duration; // например, 3–4 секунды для медленного полета
        this.amplitude = 40f;
        this.frequency = 2f;
    }

    @Override
    public Vector2 getPosition(float elapsed) {
        // elapsed is time since attack started (Enemy resets its time on pattern
        // change)
        float t = elapsed / duration; // allow t to exceed 1 to continue past target

        // direction from start to target
        Vector2 dir = new Vector2(target).sub(start);
        Vector2 pos = new Vector2(start).add(new Vector2(dir).scl(t));

        // sinusoidal offset perpendicular to the direction
        Vector2 perp = new Vector2(-dir.y, dir.x);
        if (perp.len2() > 0.0001f)
            perp.nor();
        float offset = MathUtils.sin(t * frequency * MathUtils.PI2) * amplitude;
        pos.add(perp.scl(offset));

        return pos;
    }

    @Override
    public boolean isComplete(float time) {
        return time >= duration;
    }
}
