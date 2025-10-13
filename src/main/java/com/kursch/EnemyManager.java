package com.kursch;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.kursch.patterns.MovementPattern;
import com.kursch.menu.GameScreen;
import com.kursch.patterns.CurvedTurnFormationPattern;
import com.kursch.patterns.LeftRightPattern;
import com.kursch.patterns.DiveAttackPattern;
import java.util.*;

public class EnemyManager {

    private final FitViewport viewport;
    private final Array<Enemy> enemies;
    private final Random random;
    private GameScreen gameScreen;

    private final float formationY = 700f;
    private final int formationCols = 8;
    private final int formationRows = 2;
    private final float formationSpacing = 50f;
    private final float formationRowSpacing = 60f;

    private float spawnTimer = 0f;
    private float spawnInterval = 5f;
    private float attackTimer = 0f;
    private float attackInterval = 5f;

    private float entryDuration = 5f;
    private float diveDuration = 6f;

    // Базовые интервалы
    private final float baseMinSpawnInterval = 5f;
    private final float baseMaxSpawnInterval = 8f;
    private final float baseAttackInterval = 5f;

    // Минимальные интервалы (не опускаться ниже)
    private final float minAllowedSpawnInterval = 1.5f;
    private final float minAllowedAttackInterval = 2f;

    // Базовое количество врагов
    private final int baseMinSpawn = 3;
    private final int baseMaxSpawn = 6;
    private final int baseMinAttackers = 1;
    private final int baseMaxAttackers = 3;

    private final boolean[] slotReserved;
    private final Map<Enemy, Integer> reservedMap;
    private final Map<Enemy, Float> pendingReturnTimers;

    // Система ускорения игры
    private float gameTime = 0f;
    private float speedMultiplier = 1f;
    private final float baseSpeedMultiplier = 1f;
    private final float maxSpeedMultiplier = 2.5f;
    private final float speedIncreaseRate = 0.1f; // 10% каждые 10 секунд

    public EnemyManager(FitViewport viewport, GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        this.viewport = viewport;
        this.enemies = new Array<>();
        this.random = new Random();
        this.slotReserved = new boolean[formationCols * formationRows];
        this.reservedMap = new HashMap<>();
        this.pendingReturnTimers = new HashMap<>();
    }

    public void update(float delta, Player player) {
        // Обновляем игровое время и множитель скорости
        gameTime += delta;
        speedMultiplier = Math.min(maxSpeedMultiplier,
                baseSpeedMultiplier + (gameTime / 10f) * speedIncreaseRate);

        spawnTimer += delta;
        attackTimer += delta;

        // Динамические интервалы - уменьшаются со временем
        float currentMinSpawnInterval = Math.max(minAllowedSpawnInterval,
                baseMinSpawnInterval / speedMultiplier);
        float currentMaxSpawnInterval = Math.max(minAllowedSpawnInterval + 1f,
                baseMaxSpawnInterval / speedMultiplier);
        float currentAttackInterval = Math.max(minAllowedAttackInterval,
                baseAttackInterval / speedMultiplier);

        if (spawnTimer >= spawnInterval) {
            spawnRandomWave(player);
            spawnTimer = 0f;
            spawnInterval = currentMinSpawnInterval +
                    random.nextFloat() * (currentMaxSpawnInterval - currentMinSpawnInterval);
        }

        if (attackTimer >= attackInterval) {
            launchAttackFromFormation(player);
            attackTimer = 0f;
            attackInterval = currentAttackInterval;
        }

        // Обновляем врагов
        for (Enemy e : enemies) {
            if (!e.isActive())
                continue;

            e.update(delta * speedMultiplier);

            if (e.isPatternComplete() && e.getAssignedSlot() != -1) {
                MovementPattern pattern = e.getPattern();

                // После входа в строй — начинаем LeftRightPattern с синхронизацией
                if (pattern instanceof CurvedTurnFormationPattern) {
                    int cell = e.getAssignedSlot();
                    float slotX = (viewport.getWorldWidth() / 2f - (formationCols - 1) * formationSpacing / 2f)
                            + (cell % formationCols) * formationSpacing;
                    float slotY = formationY
                            + ((cell / formationCols) - (formationRows - 1) / 2f) * formationRowSpacing;

                    LeftRightPattern leftRightPattern = new LeftRightPattern(
                            new Vector2(slotX, slotY),
                            100f,
                            0.4f * speedMultiplier);
                    e.setMovementPattern(leftRightPattern);
                }

                // После атаки — возвращаемся в строй
                if (pattern instanceof DiveAttackPattern) {
                    if (!pendingReturnTimers.containsKey(e)) {
                        pendingReturnTimers.put(e, 0.6f / speedMultiplier);
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

                    int returnDirection = e.getPosition().x < slotX ? 1 : -1;
                    e.setMovementPattern(
                            new CurvedTurnFormationPattern(
                                    new Vector2(e.getPosition()),
                                    player.getPosition(),
                                    new Vector2(slotX, slotY),
                                    (entryDuration / 1.5f) / speedMultiplier,
                                    120f,
                                    returnDirection));
                }
                prIt.remove();
            } else {
                entry.setValue(t);
            }
        }

        // Удаление врагов за пределами экрана
        float margin = 1000f;
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
                    gameScreen.addScore(e.getPoints());
                    b.destroy();
                }

            }
        }

        // --- Столкновения пуль врагов с игроком ---
        for (Enemy e : enemies) {
            if (!e.isActive())
                continue;

            Array<Bullet> enemyBullets = e.getEnemyBullets();
            for (int i = enemyBullets.size - 1; i >= 0; i--) {
                Bullet bulletEnemy = enemyBullets.get(i);
                if (!bulletEnemy.isActive()) {
                    enemyBullets.removeIndex(i);
                    continue;
                }

                // Проверка попадания пули во врага в игрока
                if (bulletEnemy.getBounds().overlaps(player.getBounds())) {
                    bulletEnemy.destroy();
                    player.addHit();
                }
            }
        }

        // Столкновение с игроком
        for (Enemy e : enemies) {
            if (e.isActive() && player.getBounds().overlaps(e.getBounds())) {
                player.addHit();
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

        // Увеличиваем количество врагов со временем
        int maxSpawnCount = (int) Math.min(baseMaxSpawn + (gameTime / 20f), formationCols);
        int minSpawnCount = Math.min(baseMinSpawn, maxSpawnCount);
        int spawnCount = Math.min(
                minSpawnCount + random.nextInt(maxSpawnCount - minSpawnCount + 1),
                freeSlots.size());

        // Определяем стартовую позицию для сосисочки
        boolean fromLeft = random.nextBoolean();
        float startX = fromLeft ? -100f : viewport.getWorldWidth() + 100f;
        float startY = viewport.getWorldHeight() + 50f;
        int direction = fromLeft ? 1 : -1;

        // Расстояние между врагами в цепочке
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

            MovementPattern entryPattern = new CurvedTurnFormationPattern(
                    new Vector2(enemyStartX, enemyStartY),
                    player.getPosition(),
                    new Vector2(targetX, targetY),
                    (entryDuration + spawnDelay) / speedMultiplier,
                    150f,
                    direction);

            Enemy newE = new blueRed_Bazz_Enemy(entryPattern, enemyStartX, enemyStartY);
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

        // Увеличиваем количество атакующих со временем
        int maxAttackers = Math.min(
                (int) (baseMaxAttackers + (gameTime / 30f)),
                Math.min(5, formed.size()));
        int minAttackers = Math.min(baseMinAttackers, maxAttackers);
        int attackers = minAttackers + random.nextInt(maxAttackers - minAttackers + 1);

        Collections.shuffle(formed, random);

        for (int i = 0; i < attackers; i++) {
            Enemy e = formed.get(i);
            Vector2 start = new Vector2(e.getPosition());
            Vector2 target = new Vector2(player.getPosition());
            e.setMovementPattern(new DiveAttackPattern(start, target, diveDuration / speedMultiplier));
            e.shooting(player);
        }
    }

    public void draw(SpriteBatch batch) {
        for (Enemy e : enemies)
            e.draw(batch);
    }

    public Array<Enemy> getEnemies() {
        return enemies;
    }

    public float getSpeedMultiplier() {
        return speedMultiplier;
    }

    public float getGameTime() {
        return gameTime;
    }

    public void dispose() {
        for (Enemy e : enemies)
            e.dispose();
        blueRed_Bazz_Enemy.disposeStatic();
    }
}