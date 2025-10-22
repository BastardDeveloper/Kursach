package com.kursch.enemyManager;

import com.badlogic.gdx.utils.Array;
import com.kursch.entities.Bullet;
import com.kursch.entities.Enemy;
import com.kursch.entities.Player;
import com.kursch.menu.GameScreen;

public class EnemyCollisionHandler {

    private final Array<Enemy> enemies;
    private final GameScreen gameScreen;

    public EnemyCollisionHandler(Array<Enemy> enemies, GameScreen gameScreen) {
        this.enemies = enemies;
        this.gameScreen = gameScreen;
    }

    public void update(Player player) {
        handlePlayerBullets(player);
        handleEnemyBullets(player);
        handlePlayerCollision(player);
    }

    private void handlePlayerBullets(Player player) {
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
    }

    private void handleEnemyBullets(Player player) {
        for (Enemy e : enemies) {
            if (!e.isActive())
                continue;

            Array<Bullet> enemyBullets = e.getEnemyBullets();
            for (int i = enemyBullets.size - 1; i >= 0; i--) {
                Bullet bullet = enemyBullets.get(i);
                if (!bullet.isActive()) {
                    enemyBullets.removeIndex(i);
                    continue;
                }

                if (bullet.getBounds().overlaps(player.getBounds())) {
                    bullet.destroy();
                    player.addHit();
                }
            }
        }
    }

    private void handlePlayerCollision(Player player) {
        for (Enemy e : enemies) {
            if (e.isActive() && player.getBounds().overlaps(e.getBounds())) {
                player.addHit();
            }
        }
    }
}
