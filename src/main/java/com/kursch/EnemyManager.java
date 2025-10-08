package com.kursch;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.kursch.patterns.MovementPattern;
import java.util.Random;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import com.badlogic.gdx.math.MathUtils;
// import com.kursch.patterns.ZigzagPattern;
// import com.kursch.patterns.CirclePattern; // unused
import com.kursch.patterns.CurvedEntryPattern;
import com.kursch.patterns.LeftRightPattern;
// import com.kursch.patterns.InfinityPattern;
// import com.kursch.patterns.LeftRightPattern;
// import com.kursch.patterns.Stap_wawe;
import com.kursch.patterns.DiveAttackPattern;

public class EnemyManager {
    private Array<Enemy> enemies;
    private float spawnTimer = 0f;
    private float spawnInterval = 5f; // каждые 5 секунд спавн новой группы
    private float attackTimer = 0f;
    private float attackInterval = 7f; // каждые 7 секунд атака из строя

    private final float formationY = 400f; // централь высота для среднего ряда
    private final int formationCols = 8;
    private final int formationRows = 4; // 4 линии в строю
    private final float formationSpacing = 120f; // horizontal spacing
    private final float formationRowSpacing = 60f; // vertical spacing between rows

    private final Random random = new Random();
    private final FitViewport viewport;

    // tuning parameters
    private float entryDuration = 6f; // seconds to reach formation (larger -> slower entry)
    private float diveDuration = 6f; // larger -> slower dive attack
    private float minSpawnInterval = 5f;
    private float maxSpawnInterval = 8f;

    private boolean[] slotReserved;
    private Map<Enemy, Integer> reservedMap;

    private Map<Enemy, Float> pendingReturnTimers;

    int count = MathUtils.random(0, 10);
    float random_Float = random.nextFloat();
    boolean random_Bool = random.nextBoolean();

    public EnemyManager(FitViewport viewport) {
        this.viewport = viewport;
        enemies = new Array<>();
        this.slotReserved = new boolean[formationCols * formationRows];
        this.reservedMap = new HashMap<>();
        this.pendingReturnTimers = new HashMap<>();
    }

    public void update(float delta, Player player) {
        spawnTimer += delta;
        attackTimer += delta;

        if (spawnTimer >= spawnInterval) {
            spawnRandomWave();
            spawnTimer = 0f;
            // randomize next spawn interval a bit
            spawnInterval = minSpawnInterval + random.nextFloat() * (maxSpawnInterval - minSpawnInterval);
        }

        if (attackTimer >= attackInterval) {
            launchAttackFromFormation(player);
            attackTimer = 0f;
        }
        // обновление врагов
        for (Enemy e : enemies) {
            if (e.isActive())
                e.update(delta);

            if (e.isActive() && e.getAssignedSlot() != -1 && e.isPatternComplete()) {
                MovementPattern cur = e.getPattern();
                if (cur instanceof DiveAttackPattern) {
                    if (!pendingReturnTimers.containsKey(e)) {
                        pendingReturnTimers.put(e, 0.6f);
                    }
                }

                else if (!(cur instanceof DiveAttackPattern) && !e.isInFormation()) {
                    int cell = e.getAssignedSlot();
                    if (cell >= 0) {
                        int row = cell / formationCols;
                        int col = cell % formationCols;
                        float slotX = (viewport.getWorldWidth() / 2f - (formationCols - 1) * formationSpacing / 2f)
                                + col * formationSpacing;
                        float slotY = formationY + (row - (formationRows - 1) / 2f) * formationRowSpacing;
                        MovementPattern lr = new LeftRightPattern(new Vector2(slotX, slotY), 8f,
                                1.2f + random.nextFloat() * 0.8f);
                        e.setMovementPattern(lr);

                    }
                }
            }
        }

        java.util.Iterator<Map.Entry<Enemy, Float>> prIt = pendingReturnTimers.entrySet().iterator();
        while (prIt.hasNext()) {
            Map.Entry<Enemy, Float> pe = prIt.next();
            Enemy en = pe.getKey();
            float t = pe.getValue() - delta;
            if (!en.isActive()) {
                prIt.remove();
                continue;
            }
            if (t <= 0f) {
                int cell = en.getAssignedSlot();
                if (cell >= 0) {
                    int row = cell / formationCols;
                    int col = cell % formationCols;
                    float slotX = (viewport.getWorldWidth() / 2f - (formationCols - 1) * formationSpacing / 2f)
                            + col * formationSpacing;
                    float slotY = formationY + (row - (formationRows - 1) / 2f) * formationRowSpacing;
                    MovementPattern returnPattern = new CurvedEntryPattern(new Vector2(en.getPosition()),
                            new Vector2(slotX, slotY), entryDuration / 1.5f);
                    en.setMovementPattern(returnPattern);
                }
                prIt.remove();
            } else {
                pe.setValue(t);
            }
        }

        // destroy enemies that flew far off-screen (allow some margin)
        float margin = 200f;
        float worldW = viewport.getWorldWidth();
        float worldH = viewport.getWorldHeight();
        for (Enemy e : enemies) {
            if (!e.isActive())
                continue;
            Vector2 p = e.getPosition();
            if (p.x < -margin || p.x > worldW + margin || p.y < -margin || p.y > worldH + margin) {
                // free reservation if any
                if (reservedMap.containsKey(e)) {
                    int s = reservedMap.remove(e);
                    if (s >= 0 && s < slotReserved.length)
                        slotReserved[s] = false;
                }
                e.destroy();
            }
        }

        // cleanup reserved map for dead enemies; keep reservations while enemy is
        // away on attack so its cell remains claimed
        java.util.Iterator<Map.Entry<Enemy, Integer>> it = reservedMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Enemy, Integer> entry = it.next();
            Enemy e = entry.getKey();
            int s = entry.getValue();
            if (!e.isActive()) { // cleanup dead
                if (s >= 0 && s < slotReserved.length)
                    slotReserved[s] = false;
                it.remove();
            }
        }

        // коллизия врагов с пулями игрока
        for (Bullet b : player.getPlayerBullets()) {
            for (Enemy e : enemies) {
                if (e.isActive() && b.getBounds().overlaps(e.getBounds())) {
                    e.destroy();
                    b.destroy();
                }
            }
        }
        // коллизия врагов с пулями игрока

        for (Enemy e : enemies) {
            if (e.isActive() && player.getBounds().overlaps(e.getBounds())) {
                player.destroy();
            }
        }

        // удаление неактивных врагов
        for (int i = enemies.size - 1; i >= 0; i--) {
            if (!enemies.get(i).isActive()) {
                Enemy dead = enemies.removeIndex(i);
                if (reservedMap.containsKey(dead)) {
                    int s = reservedMap.remove(dead);
                    if (s >= 0 && s < slotReserved.length)
                        slotReserved[s] = false;
                }
            }
        }

        // удаление неактивных пуль игрока
        for (int i = player.getPlayerBullets().size - 1; i >= 0; i--) {
            if (!player.getPlayerBullets().get(i).isActive()) {
                player.getPlayerBullets().removeIndex(i);
            }
        }
    }

    private void spawnRandomWave() {
        // Determine occupied formation slots
        boolean[] occupied = new boolean[formationCols];
        for (Enemy e : enemies) {
            if (!e.isActive())
                continue;
            Vector2 pos = e.getPosition();
            for (int c = 0; c < formationCols; c++) {
                float slotX = (viewport.getWorldWidth() / 2f - (formationCols - 1) * formationSpacing / 2f)
                        + c * formationSpacing;
                if (Math.abs(pos.x - slotX) < formationSpacing * 0.4f && Math.abs(pos.y - formationY) < 20f) {
                    occupied[c] = true;
                }
            }
        }

        // collect free cells (row-major)
        java.util.List<Integer> freeSlots = new java.util.ArrayList<>();
        for (int r = 0; r < formationRows; r++) {
            for (int c = 0; c < formationCols; c++) {
                int cellId = r * formationCols + c;
                boolean occ = false;
                // is there an active enemy in that cell?
                for (Enemy e : enemies) {
                    if (!e.isActive())
                        continue;
                    Vector2 pos = e.getPosition();
                    float slotX = (viewport.getWorldWidth() / 2f - (formationCols - 1) * formationSpacing / 2f)
                            + c * formationSpacing;
                    float slotY = formationY + (r - (formationRows - 1) / 2f) * formationRowSpacing;
                    if (Math.abs(pos.x - slotX) < formationSpacing * 0.4f && Math.abs(pos.y - slotY) < 20f) {
                        occ = true;
                        break;
                    }
                }
                if (!occ && !slotReserved[cellId])
                    freeSlots.add(cellId);
            }
        }

        if (freeSlots.isEmpty())
            return; // no space in formation

        Collections.shuffle(freeSlots, random);

        int desired = 3 + random.nextInt(4);
        int spawnCount = Math.min(desired, freeSlots.size());

        float startX = random.nextBoolean() ? -100f : viewport.getWorldWidth() + 100f;
        float startY = random.nextBoolean() ? viewport.getWorldHeight() + 100f : -100f;

        for (int i = 0; i < spawnCount; i++) {
            int cell = freeSlots.get(i);

            slotReserved[cell] = true;
            int row = cell / formationCols;
            int col = cell % formationCols;
            float targetX = (viewport.getWorldWidth() / 2f - (formationCols - 1) * formationSpacing / 2f)
                    + col * formationSpacing;
            float targetY = formationY + (row - (formationRows - 1) / 2f) * formationRowSpacing;

            MovementPattern entryPattern = new CurvedEntryPattern(new Vector2(startX, startY),
                    new Vector2(targetX, targetY), entryDuration);

            Enemy newE = new blueRed_Bazz_Enemy(entryPattern, startX, startY);
            enemies.add(newE);
            reservedMap.put(newE, cell);
            newE.setAssignedSlot(cell);
        }
    }

    private void launchAttackFromFormation(Player player) {
        if (enemies.size == 0)
            return;
        // choose only enemies that have the inFormation flag
        java.util.List<Enemy> formed = new java.util.ArrayList<>();
        for (Enemy e : enemies) {
            if (!e.isActive())
                continue;
            if (e.isInFormation())
                formed.add(e);
        }

        if (formed.isEmpty())
            return;

        int attackers = 1 + random.nextInt(Math.min(3, formed.size()));
        Collections.shuffle(formed, random);

        for (int i = 0; i < attackers; i++) {
            Enemy e = formed.get(i);
            Vector2 start = new Vector2(e.getPosition());
            Vector2 target = new Vector2(player.getPosition());
            MovementPattern divePattern = new DiveAttackPattern(start, target, diveDuration);

            e.setMovementPattern(divePattern);
        }
    }

    public void draw(SpriteBatch batch) {
        for (Enemy e : enemies)
            e.draw(batch);
    }

    public Array<Enemy> getEnemies() {
        return enemies;
    }

    public void dispose() {
        for (Enemy e : enemies) {
            e.dispose();

        }
        blueRed_Bazz_Enemy.disposeStatic();

    }
}
