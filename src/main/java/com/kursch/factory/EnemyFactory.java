// EnemyManager.java
package com.kursch.factory;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.kursch.enemyManager.*;
import com.kursch.entities.Enemy;
import com.kursch.entities.Player;
import com.kursch.entities.enemyCollection.blueRed_Bazz_Enemy;
import com.kursch.menu.GameScreen;

public class EnemyFactory {

    private final FitViewport viewport;
    private final Array<Enemy> enemies;
    private final GameScreen gameScreen;

    private final GameSpeedController speedController;
    private final EnemySpawner spawner;
    private final EnemyAttacker attacker;
    private final EnemyReturnHandler returnHandler;
    private final EnemyCollisionHandler collisionHandler;

    public EnemyFactory(FitViewport viewport, GameScreen gameScreen) {
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

        speedController.update(delta);

        // Обновляем глобальное время для всех формационных паттернов
        com.kursch.patterns.FormationEntryPattern.updateGlobalTime(delta * speedMultiplier);
        com.kursch.patterns.GalagaFormationPattern.updateGlobalTime(delta * speedMultiplier);

        spawner.spawnIfNeeded(delta, player, speedMultiplier);

        updateEnemies(delta, speedMultiplier);

        attacker.update(delta, player, speedMultiplier);

        returnHandler.update(delta, player, speedMultiplier);

        collisionHandler.update(player);

        removeOffscreenEnemies();
    }

    private void updateEnemies(float delta, float speedMultiplier) {
        for (Enemy e : enemies) {
            if (!e.isAlive())
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

                    // Один паттерн: вход + волна одновременно
                    e.setMovementPattern(new com.kursch.patterns.FormationEntryPattern(
                            e.getPosition(), // Текущая позиция врага
                            slotX, // Целевая X (центр волны)
                            slotY, // Целевая Y
                            1.5f, // Длительность входа
                            0.5f, // Частота волны
                            100f)); // Амплитуда волны
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

            if (!e.isAlive()) {
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
        com.kursch.entities.enemyCollection.blueRed_Bazz_Enemy.disposeStatic();
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
