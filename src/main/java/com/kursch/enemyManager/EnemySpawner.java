// EnemySpawner.java
package com.kursch.enemyManager;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.kursch.entities.Enemy;
import com.kursch.entities.Player;
import com.kursch.entities.enemyCollection.blueRed_Bazz_Enemy;
import com.kursch.patterns.CurvedTurnFormationPattern;

import java.util.*;

public class EnemySpawner {

    private final FitViewport viewport;
    private final Array<Enemy> enemies;
    private final Random random = new Random();

    private final boolean[] slotReserved;
    private final Map<Enemy, Integer> reservedMap;

    private final int formationCols = 8;
    private final int formationRows = 2;
    private final float formationY = 700f;
    private final float formationSpacing = 50f;
    private final float formationRowSpacing = 60f;

    private float spawnTimer = 0f;
    private float spawnInterval = 5f;

    private final float baseMinSpawnInterval = 3f;
    private final float baseMaxSpawnInterval = 6f;
    private final int baseMinSpawn = 3;
    private final int baseMaxSpawn = 6;
    private final float entryDuration = 5f;

    public EnemySpawner(FitViewport viewport, Array<Enemy> enemies) {
        this.viewport = viewport;
        this.enemies = enemies;
        this.slotReserved = new boolean[formationCols * formationRows];
        this.reservedMap = new HashMap<>();
    }

    public void spawnIfNeeded(float delta, Player player, float speedMultiplier) {
        spawnTimer += delta;
        if (spawnTimer >= spawnInterval) {
            spawnWave(player, speedMultiplier);
            spawnTimer = 0f;
            spawnInterval = Math.max(1f, baseMinSpawnInterval / speedMultiplier +
                    random.nextFloat()
                            * ((baseMaxSpawnInterval / speedMultiplier) - (baseMinSpawnInterval / speedMultiplier)));
        }
    }

    private void spawnWave(Player player, float speedMultiplier) {
        List<Integer> freeSlots = new ArrayList<>();
        for (int i = 0; i < slotReserved.length; i++) {
            if (!slotReserved[i])
                freeSlots.add(i);
        }
        if (freeSlots.isEmpty())
            return;

        Collections.shuffle(freeSlots, random);
        int maxSpawnCount = Math.min(baseMaxSpawn + 1, formationCols);
        int spawnCount = Math.min(baseMinSpawn + random.nextInt(maxSpawnCount - baseMinSpawn + 1), freeSlots.size());

        boolean fromLeft = random.nextBoolean();
        float startX = fromLeft ? -100f : viewport.getWorldWidth() + 100f;
        float startY = viewport.getWorldHeight() + 50f;
        int direction = fromLeft ? 1 : -1;
        float chainSpacing = 30f;

        for (int i = 0; i < spawnCount; i++) {
            int cell = freeSlots.get(i);
            slotReserved[cell] = true;

            int row = cell / formationCols;
            int col = cell % formationCols;

            float targetX = (viewport.getWorldWidth() / 2f - (formationCols - 1) * formationSpacing / 2f)
                    + col * formationSpacing;
            float targetY = formationY + (row - (formationRows - 1) / 2f) * formationRowSpacing;

            float enemyStartX = startX;
            float enemyStartY = startY - i * chainSpacing;
            float spawnDelay = i * 0.15f;

            float phaseShift = (col % 2) * MathUtils.PI; // Альтернирующий сдвиг по колоннам

            Enemy newE = new blueRed_Bazz_Enemy(
                    new CurvedTurnFormationPattern(
                            new Vector2(enemyStartX, enemyStartY),
                            player.getPosition(),
                            new Vector2(targetX, targetY),
                            (entryDuration + spawnDelay) / speedMultiplier,
                            150f,
                            direction),
                    enemyStartX, enemyStartY);
            newE.setSpawnDelay(spawnDelay);
            newE.setAssignedSlot(cell);
            newE.setPhaseOffset(phaseShift); // Устанавливаем фазовый сдвиг

            enemies.add(newE);
            reservedMap.put(newE, cell);
        }
    }

    public void freeSlot(int slot) {
        if (slot >= 0 && slot < slotReserved.length)
            slotReserved[slot] = false;
    }

    public int getFormationCols() {
        return formationCols;
    }

    public int getFormationRows() {
        return formationRows;
    }

    public float getFormationY() {
        return formationY;
    }

    public float getFormationSpacing() {
        return formationSpacing;
    }

    public float getFormationRowSpacing() {
        return formationRowSpacing;
    }
}
