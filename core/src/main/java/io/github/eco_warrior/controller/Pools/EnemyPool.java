package io.github.eco_warrior.controller.Pools;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.utils.Array;
import io.github.eco_warrior.controller.Enemy.EnemyController;
import io.github.eco_warrior.controller.Manager.EnemyManager;
import io.github.eco_warrior.entity.Enemies;
import io.github.eco_warrior.enums.TreeType;

import java.util.ArrayList;

public abstract class EnemyPool<T extends EnemyController> {
    protected Array<EnemyController> enemyPool;
    protected final int maxPoolSize = 5;
    protected EnemyManager enemyManager;
    protected int poolSent = 0;
    protected ArrayList<TreeType> treeTypeToAttack;

    public EnemyPool(EnemyManager enemyManager) {
        this.enemyManager = enemyManager;
        this.enemyPool = new Array<>(maxPoolSize);
        this.treeTypeToAttack = new ArrayList<>();

        // Pre-initialize all enemies
        for (int i = 0; i < maxPoolSize; i++) {
            T enemy = createEnemy(new Vector2(-100, -100)); // Off-screen position
            enemy.setState(Enemies.EnemyState.IDLE);
            enemy.setRightDirection(false);
            enemyPool.add(enemy);
        }
    }

    protected abstract T createEnemy(Vector2 vector2);

    public T getEnemy(Vector2 position) {
        if (enemyPool.size > 0 && poolSent < maxPoolSize) {
            T enemy = (T) enemyPool.pop();
            enemy.setSpritePosition(position);
            enemy.setState(Enemies.EnemyState.MOVING);
            enemy.setRightDirection(false);
            enemyManager.addEnemy(enemy);
            poolSent++;
            return enemy;
        }
        return null;
    }

    public void returnEnemy(EnemyController enemy) {
        if (enemy != null && poolSent > 0) {
            // Reset enemy state
            enemy.setSpritePosition(new Vector2(-100, -100)); // Move off-screen
            enemy.resetState();
            enemyPool.add(enemy);
            poolSent--;
        }
    }

    public int getActiveCount() {
        return poolSent;
    }

    public void setPoolSent(int count) {
        this.poolSent = count;
    }

    public void setAttackTreeType(ArrayList<TreeType> positions){
        this.treeTypeToAttack = positions;
    }

    public ArrayList<TreeType> getAttackTreeType() {
        return treeTypeToAttack;
    }
}
