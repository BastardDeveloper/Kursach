package com.kursch;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.kursch.patterns.MovementPattern;
import com.kursch.patterns.CurvedTurnFormationPattern;
import com.kursch.patterns.LeftRightPattern;
import com.kursch.patterns.DiveAttackPattern;
import java.util.*;

public class EnemyManager {

    private final FitViewport viewport;
    private final Array<Enemy> enemies;
    private final Random random;

    private final float formationY = 500f;
    private final int formationCols = 8;
    private final int formationRows = 2;
    private final float formationSpacing = 120f;
    private final float formationRowSpacing = 60f;

    private float spawnTimer = 0f;
    private float spawnInterval = 5f;
    private float attackTimer = 0f;
    private float attackInterval = 7f;

    private float entryDuration = 7f;
    private float diveDuration = 6f;
    private float minSpawnInterval = 5f;
    private float maxSpawnInterval = 8f;

    private final boolean[] slotReserved;
    private final Map<Enemy, Integer> reservedMap;
    private final Map<Enemy, Float> pendingReturnTimers;

    public EnemyManager(FitViewport viewport) {
        this.viewport = viewport;
        this.enemies = new Array<>();
        this.random = new Random();
        this.slotReserved = new boolean[formationCols * formationRows];
        this.reservedMap = new HashMap<>();
        this.pendingReturnTimers = new HashMap<>();
    }

    public void update(float delta, Player player) {
        spawnTimer += delta;
        attackTimer += delta;

        if (spawnTimer >= spawnInterval) {
            spawnRandomWave(player);
            spawnTimer = 0f;
            spawnInterval = minSpawnInterval + random.nextFloat() * (maxSpawnInterval - minSpawnInterval);
        }

        if (attackTimer >= attackInterval) {
            launchAttackFromFormation(player);
            attackTimer = 0f;
        }

        // Обновляем врагов
        for (Enemy e : enemies) {
            if (!e.isActive())
                continue;

            e.update(delta);

            if (e.isPatternComplete() && e.getAssignedSlot() != -1) {
                MovementPattern pattern = e.getPattern();

                // После входа в строй — начинаем LeftRightPattern
                if (pattern instanceof CurvedTurnFormationPattern) {
                    int cell = e.getAssignedSlot();
                    float slotX = (viewport.getWorldWidth() / 2f - (formationCols - 1) * formationSpacing / 2f)
                            + (cell % formationCols) * formationSpacing;
                    float slotY = formationY
                            + ((cell / formationCols) - (formationRows - 1) / 2f) * formationRowSpacing;

                    e.setMovementPattern(new LeftRightPattern(new Vector2(slotX, slotY), 200f, 0.5f));
                }

                // После атаки — возвращаемся в строй
                if (pattern instanceof DiveAttackPattern) {
                    if (!pendingReturnTimers.containsKey(e)) {
                        pendingReturnTimers.put(e, 0.6f);
                    }
                }
            }
        }

        // Обработка возврата после атаки
        Iterator<Map.Entry<Enemy, Float>> prIt = pendingReturnTimers.entrySet().iterator();
        while (prIt.hasNext()) {
            Map.Entry<Enemy, Float> entry = prIt.next();
            Enemy e = entry.getKey();
            float t = entry.getValue() - delta;

            if (!e.isActive()) {
                prIt.remove();
                continue;
            }

            if (t <= 0f) {
                int cell = e.getAssignedSlot();
                if (cell >= 0) {
                    float slotX = (viewport.getWorldWidth() / 2f - (formationCols - 1) * formationSpacing / 2f)
                            + (cell % formationCols) * formationSpacing;
                    float slotY = formationY
                            + ((cell / formationCols) - (formationRows - 1) / 2f) * formationRowSpacing;

                    // ✅ ИСПРАВЛЕНО: правильный порядок параметров
                    int returnDirection = e.getPosition().x < slotX ? 1 : -1;
                    e.setMovementPattern(
                            new CurvedTurnFormationPattern(
                                    new Vector2(e.getPosition()),
                                    player.getPosition(),
                                    new Vector2(slotX, slotY),
                                    entryDuration / 1.5f, // duration
                                    120f, // turnRadius (увеличен!)
                                    returnDirection // direction (-1 или 1)
                            ));
                }
                prIt.remove();
            } else {
                entry.setValue(t);
            }
        }

        // Удаление врагов за пределами экрана
        float margin = 2000f;
        float worldW = viewport.getWorldWidth();
        float worldH = viewport.getWorldHeight();

        for (int i = enemies.size - 1; i >= 0; i--) {
            Enemy e = enemies.get(i);
            if (!e.isActive()) {
                Integer sObj = reservedMap.remove(e);
                int s = (sObj != null) ? sObj : -1;
                if (s >= 0 && s < slotReserved.length)
                    slotReserved[s] = false;
                enemies.removeIndex(i);
                continue;
            }
            Vector2 p = e.getPosition();
            if (p.x < -margin || p.x > worldW + margin || p.y < -margin || p.y > worldH + margin) {
                Integer sObj = reservedMap.remove(e);
                int s = (sObj != null) ? sObj : -1;
                if (s >= 0 && s < slotReserved.length)
                    slotReserved[s] = false;
                e.destroy();
            }
        }

        // Обработка столкновений с пулями
        for (int i = player.getPlayerBullets().size - 1; i >= 0; i--) {
            Bullet b = player.getPlayerBullets().get(i);
            if (!b.isActive()) {
                player.getPlayerBullets().removeIndex(i);
                continue;
            }
            for (Enemy e : enemies) {
                if (e.isActive() && b.getBounds().overlaps(e.getBounds())) {
                    e.destroy();
                    b.destroy();
                }
            }
        }

        // Столкновение с игроком
        for (Enemy e : enemies) {
            if (e.isActive() && player.getBounds().overlaps(e.getBounds())) {
                player.destroy();
            }
        }
    }

    private void spawnRandomWave(Player player) {
        List<Integer> freeSlots = new ArrayList<>();
        for (int r = 0; r < formationRows; r++) {
            for (int c = 0; c < formationCols; c++) {
                int cellId = r * formationCols + c;
                if (!slotReserved[cellId])
                    freeSlots.add(cellId);
            }
        }
        if (freeSlots.isEmpty())
            return;

        Collections.shuffle(freeSlots, random);
        int spawnCount = Math.min(3 + random.nextInt(4), freeSlots.size());

        // Определяем стартовую позицию для "сосисочки"
        boolean fromLeft = random.nextBoolean();
        float startX = fromLeft ? -100f : viewport.getWorldWidth() + 100f;
        float startY = viewport.getWorldHeight() + 50f;
        int direction = fromLeft ? 1 : -1;

        // Расстояние между врагами в цепочке
        float chainSpacing = 60f;

        for (int i = 0; i < spawnCount; i++) {
            int cell = freeSlots.get(i);
            slotReserved[cell] = true;

            int row = cell / formationCols;
            int col = cell % formationCols;

            float targetX = (viewport.getWorldWidth() / 2f - (formationCols - 1) * formationSpacing / 2f)
                    + col * formationSpacing;
            float targetY = formationY + (row - (formationRows - 1) / 2f) * formationRowSpacing;

            // Каждый следующий враг стартует чуть позже по Y (сосисочка)
            float enemyStartX = startX;
            float enemyStartY = startY - i * chainSpacing;

            // Добавляем задержку для каждого врага в цепочке
            float spawnDelay = i * 0.15f; // 0.15 секунды между спавном каждого врага

            MovementPattern entryPattern = new CurvedTurnFormationPattern(
                    new Vector2(enemyStartX, enemyStartY),
                    player.getPosition(),
                    new Vector2(targetX, targetY),
                    entryDuration + spawnDelay, // увеличиваем длительность с учетом задержки
                    150f, // turnRadius
                    direction);

            Enemy newE = new blueRed_Bazz_Enemy(entryPattern, enemyStartX, enemyStartY);

            // Если твой Enemy класс поддерживает задержку спавна
            newE.setSpawnDelay(spawnDelay);

            enemies.add(newE);
            reservedMap.put(newE, cell);
            newE.setAssignedSlot(cell);
        }
    }

    private void launchAttackFromFormation(Player player) {
        List<Enemy> formed = new ArrayList<>();
        for (Enemy e : enemies) {
            if (e.isActive() && e.isInFormation())
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
            e.setMovementPattern(new DiveAttackPattern(start, target, diveDuration));
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
        for (Enemy e : enemies)
            e.dispose();
        blueRed_Bazz_Enemy.disposeStatic();
    }
}