// EnemyManager.java
package com.kursch;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.kursch.enemy.*;
import com.kursch.menu.GameScreen;

public class EnemyManager {

    private final FitViewport viewport;
    private final Array<Enemy> enemies;
    private final GameScreen gameScreen;

    private final GameSpeedController speedController;
    private final EnemySpawner spawner;
    private final EnemyAttacker attacker;
    private final EnemyReturnHandler returnHandler;
    private final EnemyCollisionHandler collisionHandler;

    public EnemyManager(FitViewport viewport, GameScreen gameScreen) {
        this.viewport = viewport;
        this.gameScreen = gameScreen;
        this.enemies = new Array<>();

        this.speedController = new GameSpeedController();
        this.spawner = new EnemySpawner(viewport, enemies);
        this.attacker = new EnemyAttacker(enemies);
        this.returnHandler = new EnemyReturnHandler(enemies, viewport);
        this.collisionHandler = new EnemyCollisionHandler(enemies, gameScreen);
    }

    public void update(float delta, Player player) {
        float speedMultiplier = speedController.getSpeedMultiplier();

        // Оновлюємо прискорення гри
        speedController.update(delta);

        // Спавнимо ворогів
        spawner.spawnIfNeeded(delta, player, speedMultiplier);

        // Оновлюємо всіх ворогів
        updateEnemies(delta, speedMultiplier);

        // Логіка атак ворогів
        attacker.update(delta, player, speedMultiplier);

        // Повернення після атаки
        returnHandler.update(delta, player, speedMultiplier);

        // Колізії
        collisionHandler.update(player);

        // Видаляємо ворогів за межами екрану
        removeOffscreenEnemies();
    }

    private void updateEnemies(float delta, float speedMultiplier) {
        for (Enemy e : enemies) {
            if (!e.isActive())
                continue;

            e.update(delta * speedMultiplier);

            if (e.isPatternComplete() && e.getAssignedSlot() != -1) {
                if (e.getPattern() instanceof com.kursch.patterns.CurvedTurnFormationPattern) {
                    int cell = e.getAssignedSlot();
                    float slotX = (viewport.getWorldWidth() / 2f
                            - (spawner.getFormationCols() - 1) * spawner.getFormationSpacing() / 2f)
                            + (cell % spawner.getFormationCols()) * spawner.getFormationSpacing();
                    float slotY = spawner.getFormationY()
                            + ((cell / spawner.getFormationCols()) - (spawner.getFormationRows() - 1) / 2f)
                                    * spawner.getFormationRowSpacing();
                    e.setMovementPattern(new com.kursch.patterns.LeftRightPattern(
                            new com.badlogic.gdx.math.Vector2(slotX, slotY),
                            100f,
                            0.4f * speedMultiplier));
                }
            }
        }
    }

    private void removeOffscreenEnemies() {
        float margin = 1000f;
        float worldW = viewport.getWorldWidth();
        float worldH = viewport.getWorldHeight();

        for (int i = enemies.size - 1; i >= 0; i--) {
            Enemy e = enemies.get(i);

            if (!e.isActive()) {
                spawner.freeSlot(e.getAssignedSlot());
                enemies.removeIndex(i);
                continue;
            }

            float x = e.getPosition().x;
            float y = e.getPosition().y;
            if (x < -margin || x > worldW + margin || y < -margin || y > worldH + margin) {
                e.destroy();
                enemies.removeIndex(i);
            }
        }
    }

    public void draw(SpriteBatch batch) {
        for (Enemy e : enemies)
            e.draw(batch);
    }

    public void dispose() {
        for (Enemy e : enemies)
            e.dispose();
        com.kursch.blueRed_Bazz_Enemy.disposeStatic();
    }

    public Array<Enemy> getEnemies() {
        return enemies;
    }

    public float getSpeedMultiplier() {
        return speedController.getSpeedMultiplier();
    }

    public float getGameTime() {
        return speedController.getGameTime();
    }
}
