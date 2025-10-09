package com.kursch.patterns;

import com.badlogic.gdx.math.Vector2;

public class CurvedEntryPattern implements MovementPattern {

    private final Vector2 start; // начальная позиция врага (за экраном)
    private final Vector2 target; // точка в строю
    private final Vector2 control; // контрольная точка для кривой Безье
    private final float duration; // seconds to complete the entry

    private final float spinAmplitude;
    private final float spinFrequency;

    public CurvedEntryPattern(Vector2 start, Vector2 target, float duration) {
        this.start = new Vector2(start);
        this.target = new Vector2(target);
        this.duration = Math.max(0.1f, duration);

        float controlYOffset = (start.y > target.y) ? 200f : -200f;
        this.control = new Vector2((start.x + target.x) / 2f, start.y + controlYOffset);

        this.spinAmplitude = 60f;
        this.spinFrequency = 2f;
    }

    @Override
    public Vector2 getPosition(float elapsedTime) {

        float t = com.badlogic.gdx.math.MathUtils.clamp(elapsedTime / duration, 0f, 1f);

        float u = 1 - t;
        float x = u * u * start.x + 2 * u * t * control.x + t * t * target.x;
        float y = u * u * start.y + 2 * u * t * control.y + t * t * target.y;

        Vector2 base = new Vector2(x, y);

        Vector2 pathDir = new Vector2(target).sub(start);
        Vector2 perp = new Vector2(-pathDir.y, pathDir.x);
        if (perp.len2() > 0.0001f)
            perp.nor();

        float spin = (float) Math.sin(t * spinFrequency * Math.PI * 2) * spinAmplitude * (1f - t);
        base.add(perp.scl(spin));

        return base;
    }

    @Override
    public boolean isComplete(float time) {
        return time / duration >= 1f;
    }
}
