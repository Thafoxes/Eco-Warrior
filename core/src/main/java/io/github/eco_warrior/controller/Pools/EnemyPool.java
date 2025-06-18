package io.github.eco_warrior.controller.Pools;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.eco_warrior.controller.Enemy.EnemyController;
import io.github.eco_warrior.controller.Manager.EnemyManager;

public abstract class EnemyPool<T extends EnemyController> {
    protected Array<EnemyController> enemyPool;
    protected final int maxPoolSize = 5;
    protected EnemyManager enemyManager;
    protected int activeCount = 0;

    public EnemyPool(EnemyManager enemyManager) {
        this.enemyManager = enemyManager;
        this.enemyPool = new Array<>(maxPoolSize);

        // Pre-initialize all enemies
        for (int i = 0; i < maxPoolSize; i++) {
            T enemy = createEnemy(new Vector2(-100, -100)); // Off-screen position
            enemyPool.add(enemy);
        }
    }

    protected abstract T createEnemy(Vector2 vector2);

    public EnemyController getEnemy(Vector2 position) {
        if (enemyPool.size > 0 && activeCount < maxPoolSize) {
            EnemyController enemy = enemyPool.pop();
            enemy.getSprite().setPosition(position.x, position.y);
            activeCount++;
            return enemy;
        }
        return null;
    }

    public void returnEnemy(EnemyController enemy) {
        if (enemy != null && activeCount > 0) {
            // Reset enemy state
            enemy.getSprite().setPosition(-100, -100); // Move off-screen
            enemy.resetState();
            enemyPool.add(enemy);
            activeCount--;
        }
    }

    public int getActiveCount() {
        return activeCount;
    }

    public void setActiveCount(int count) {
        this.activeCount = count;
    }
}
