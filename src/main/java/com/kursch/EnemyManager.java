package com.kursch;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.kursch.patterns.MovementPattern;
import com.kursch.patterns.SpiralPattern;

public class EnemyManager {
    private Array<Enemy> enemies;
    private Texture enemiesTexture;

    public EnemyManager(FitViewport viewport) {
        enemiesTexture = new Texture("26482.png");
        enemies = new Array<>();
        spawnWave();
    }

    private void spawnWave() {
        TextureRegion region = new TextureRegion(enemiesTexture, 110, 75, 15, 12);
        for (int i = 0; i < 7; i++) {
            float x = 100 + i * 100;
            MovementPattern pattern = new SpiralPattern(new Vector2(x, 500), 10f, 2f);
            enemies.add(new Enemy(region, pattern, x, 500));
        }
    }

    public void update(float delta, Player player) {

        for (Enemy e : enemies) {
            if (e.isActive())
                e.update(delta);
        }

        // колізія ворогів з кулями гравця
        for (Bullet b : player.getPlayerBullets()) {
            for (Enemy e : enemies) {
                if (e.isActive() && b.getBounds().overlaps(e.getBounds())) {
                    e.destroy();
                    b.destroy();
                }
            }
        }

        for (int i = enemies.size - 1; i >= 0; i--) {
            if (!enemies.get(i).isActive()) {
                enemies.removeIndex(i);
            }
        }

        for (int i = player.getPlayerBullets().size - 1; i >= 0; i--) {
            if (!player.getPlayerBullets().get(i).isActive()) {
                player.getPlayerBullets().removeIndex(i);
            }
        }
    }

    public void draw(SpriteBatch batch) {
        if (enemies.size == 0) {
            spawnWave();
        }

        for (Enemy e : enemies)
            e.draw(batch);
    }

    public Array<Enemy> getEnemies() {
        return enemies;
    }
}
