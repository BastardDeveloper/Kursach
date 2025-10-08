package com.kursch;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.kursch.patterns.MovementPattern;
// import com.kursch.patterns.ZigzagPattern;
import com.kursch.patterns.CirclePattern;
// import com.kursch.patterns.InfinityPattern;
// import com.kursch.patterns.LeftRightPattern;
// import com.kursch.patterns.Stap_wawe;

public class EnemyManager {

    private Array<Enemy> enemies;

    public EnemyManager(FitViewport viewport) {
        enemies = new Array<>();
        spawnWave();
    }

    private void spawnWave() {
        for (int i = 0; i < 8; i++) {
            float x = 100 + i * 200;
            MovementPattern circlePattern = new CirclePattern(new Vector2(x, 500), 20f,
                    2f);
            // MovementPattern ZigzagPattern = new ZigzagPattern(new Vector2(x, 500), 1f,
            // 500f, 1f);
            // MovementPattern InfinityPattern = new InfinityPattern(new Vector2(x, 500),
            // 50f, 0.3f);
            // MovementPattern Stap_wawe = new Stap_wawe(new Vector2(x, 500), 200f, 100f,
            // 5f);
            // MovementPattern LeftRightPattern = new LeftRightPattern(new Vector2(x, 500),
            // 200f, 3f);
            // enemies.add(new blueRed_Bazz_Enemy(LeftRightPattern, x, 500));
            enemies.add(new blueRed_Bazz_Enemy(circlePattern, x, 500));
            // enemies.add(new blueRed_Bazz_Enemy(InfinityPattern, x, 500));
            // enemies.add(new blueRed_Bazz_Enemy(Stap_wawe, x, 500));

        }

    }

    public void update(float delta, Player player) {
        // обновление врагов
        for (Enemy e : enemies) {
            if (e.isActive())
                e.update(delta);
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
                enemies.removeIndex(i);
            }
        }

        // удаление неактивных пуль игрока
        for (int i = player.getPlayerBullets().size - 1; i >= 0; i--) {
            if (!player.getPlayerBullets().get(i).isActive()) {
                player.getPlayerBullets().removeIndex(i);
            }
        }
    }

    public void draw(SpriteBatch batch) {
        // если все враги уничтожены, спавним новую волну
        if (enemies.size == 0) {
            spawnWave();
        }

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
