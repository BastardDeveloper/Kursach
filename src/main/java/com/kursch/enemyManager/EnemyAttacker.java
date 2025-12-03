package com.kursch.enemyManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.kursch.entities.Enemy;
import com.kursch.entities.Player;
import com.kursch.patterns.DiveAttackPattern;
import java.util.*;

public class EnemyAttacker {
    private final Array<Enemy> enemies;
    private final Random random = new Random();
    private float attackTimer = 0f;
    private float attackInterval = 5f;
    private final int baseMinAttackers = 1;
    private final int baseMaxAttackers = 3;

    // Базовые значения для стрельбы
    private final int baseShootIterationMax = 3;
    private final float baseTimeShootIterationMax = 3f;
    private final int baseShootIterationMin = 1;
    private final float baseTimeShootIterationMin = 1f;

    public EnemyAttacker(Array<Enemy> enemies) {
        this.enemies = enemies;
    }

    public void update(float delta, Player player, float speedMultiplier) {
        attackTimer += delta;

        // Обновляем стрельбу для всех врагов
        for (Enemy e : enemies) {
            if (e.isAlive()) {
                e.updateShooting(delta, player);
            }
        }

        if (attackTimer >= attackInterval) {
            launchAttack(player, speedMultiplier);
            attackTimer = 0f;
            // Интервал между атаками уменьшается с ростом сложности
            attackInterval = Math.max(2f, 5f / speedMultiplier);
        }
    }

    private void launchAttack(Player player, float speedMultiplier) {
        List<Enemy> formed = new ArrayList<>();
        for (Enemy e : enemies) {
            if (e.isAlive() && e.isInFormation())
                formed.add(e);
        }
        if (formed.isEmpty())
            return;

        // Количество атакующих увеличивается с ростом сложности
        int maxAttackers = Math.min(
                baseMaxAttackers + (int) (speedMultiplier / 2), // +1 атакующий каждые 2 уровня скорости
                formed.size());
        int minAttackers = Math.min(baseMinAttackers, maxAttackers);

        int attackers = Math.min(
                minAttackers + random.nextInt(maxAttackers - minAttackers + 1),
                formed.size());

        Collections.shuffle(formed, random);

        for (int i = 0; i < attackers; i++) {
            Enemy e = formed.get(i);

            // Количество выстрелов увеличивается с ростом сложности
            int shootIterationMax = baseShootIterationMax + (int) (speedMultiplier / 3); // +1 выстрел каждые 3 уровня
            int shootIterationMin = baseShootIterationMin;

            int shootIteration = shootIterationMin +
                    random.nextInt(Math.max(1, shootIterationMax - shootIterationMin + 1));

            // Время между выстрелами уменьшается с ростом сложности
            float timeShootIterationMax = baseTimeShootIterationMax / Math.max(1f, speedMultiplier * 0.5f);
            float timeShootIterationMin = baseTimeShootIterationMin / Math.max(1f, speedMultiplier * 0.5f);

            float totalTime = timeShootIterationMin +
                    random.nextFloat() * (timeShootIterationMax - timeShootIterationMin);

            Vector2 start = new Vector2(e.getPosition());
            Vector2 target = new Vector2(player.getPosition());

            // Скорость атаки увеличивается с ростом сложности
            float diveSpeed = 20 / speedMultiplier * 1.5f; // Враги атакуют быстрее
            e.setMovementPattern(new DiveAttackPattern(start, target, diveSpeed));

            // Планируем выстрелы с задержкой
            float delayBetweenShots = totalTime / shootIteration;
            for (int j = 0; j < shootIteration; j++) {
                e.scheduleShot(delayBetweenShots * j);
            }
        }
    }
}