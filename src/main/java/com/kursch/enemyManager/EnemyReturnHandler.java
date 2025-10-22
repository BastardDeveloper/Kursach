package com.kursch.enemyManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.kursch.entities.Enemy;
import com.kursch.entities.Player;
import com.kursch.patterns.CurvedTurnFormationPattern;
import com.kursch.patterns.DiveAttackPattern;
import com.kursch.patterns.IMovementPattern;

import java.util.*;

public class EnemyReturnHandler {

    private final Array<Enemy> enemies;
    private final FitViewport viewport;
    private final Map<Enemy, Float> pendingReturnTimers = new HashMap<>();

    private final float entryDuration = 5f;
    private final float formationY = 700f;
    private final int formationCols = 8;
    private final int formationRows = 2;
    private final float formationSpacing = 50f;
    private final float formationRowSpacing = 60f;

    public EnemyReturnHandler(Array<Enemy> enemies, FitViewport viewport) {
        this.enemies = enemies;
        this.viewport = viewport;
    }

    public void update(float delta, Player player, float speedMultiplier) {
        for (Enemy e : enemies) {
            if (!e.isActive())
                continue;

            IMovementPattern pattern = e.getPattern();

            if (pattern instanceof DiveAttackPattern && !pendingReturnTimers.containsKey(e)) {
                pendingReturnTimers.put(e, 0.6f / speedMultiplier);
            }
        }

        Iterator<Map.Entry<Enemy, Float>> it = pendingReturnTimers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Enemy, Float> entry = it.next();
            Enemy e = entry.getKey();
            float t = entry.getValue() - delta;

            if (t <= 0f && e.isActive() && e.getAssignedSlot() != -1) {
                int cell = e.getAssignedSlot();
                float slotX = (viewport.getWorldWidth() / 2f - (formationCols - 1) * formationSpacing / 2f)
                        + (cell % formationCols) * formationSpacing;
                float slotY = formationY
                        + ((cell / formationCols) - (formationRows - 1) / 2f) * formationRowSpacing;

                int direction = e.getPosition().x < slotX ? 1 : -1;
                e.setMovementPattern(new CurvedTurnFormationPattern(
                        new Vector2(e.getPosition()),
                        player.getPosition(),
                        new Vector2(slotX, slotY),
                        (entryDuration / 1.5f) / speedMultiplier,
                        120f,
                        direction));

                it.remove();
            } else {
                entry.setValue(t);
            }
        }
    }
}
