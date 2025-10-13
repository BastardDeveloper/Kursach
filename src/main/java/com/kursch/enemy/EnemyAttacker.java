package com.kursch.enemy;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.kursch.Player;
import com.kursch.patterns.DiveAttackPattern;

import java.util.*;

public class EnemyAttacker {

    private final Array<Enemy> enemies;
    private final Random random = new Random();

    private float attackTimer = 0f;
    private float attackInterval = 5f;

    private final int baseMinAttackers = 1;
    private final int baseMaxAttackers = 3;
    private final float diveDuration = 6f;

    public EnemyAttacker(Array<Enemy> enemies) {
        this.enemies = enemies;
    }

    public void update(float delta, Player player, float speedMultiplier) {
        attackTimer += delta;
        if (attackTimer >= attackInterval) {
            launchAttack(player, speedMultiplier);
            attackTimer = 0f;
            attackInterval = Math.max(2f, 5f / speedMultiplier);
        }
    }

    private void launchAttack(Player player, float speedMultiplier) {
        List<Enemy> formed = new ArrayList<>();
        for (Enemy e : enemies) {
            if (e.isActive() && e.isInFormation())
                formed.add(e);
        }

        if (formed.isEmpty())
            return;

        int attackers = Math.min(baseMinAttackers + random.nextInt(baseMaxAttackers - baseMinAttackers + 1),
                formed.size());
        Collections.shuffle(formed, random);

        for (int i = 0; i < attackers; i++) {
            Enemy e = formed.get(i);
            Vector2 start = new Vector2(e.getPosition());
            Vector2 target = new Vector2(player.getPosition());
            e.setMovementPattern(new DiveAttackPattern(start, target, diveDuration / speedMultiplier));
            e.shooting(player);
        }
    }
}
