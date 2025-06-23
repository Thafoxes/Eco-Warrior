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
    protected final int maxPoolSize = 3;
    protected EnemyManager enemyManager;
    protected int poolSent = 0;
    protected ArrayList<TreeType> treeTypeToAttack;

    public EnemyPool(EnemyManager enemyManager) {
        this.enemyManager = enemyManager;
        this.enemyPool = new Array<>(maxPoolSize);
        this.treeTypeToAttack = new ArrayList<>();

        // Pre-initialize all enemies
        generateEnemies();
    }

    private void generateEnemies() {
//        System.out.println("EnemyPool - - Generating enemies for pool: " + this.getClass());
        for (int i = 0; i < maxPoolSize; i++) {
            T enemy = createEnemy(new Vector2(-100, -100)); // Off-screen position
            enemy.setState(Enemies.EnemyState.IDLE);
            enemy.setRightDirection(false);
            enemyPool.add(enemy);
        }
    }

    protected abstract T createEnemy(Vector2 vector2);

    /**
     * Retrieves an enemy from the pool and initializes it with the given position and tree type.
     *
     * @param position  The position to set for the enemy.
     * @param treeType  The type of tree the enemy will attack.
     */
    public void getEnemy(Vector2 position, TreeType treeType) {
        if (enemyPool.size > 0 && poolSent < maxPoolSize) {
            T enemy = (T) enemyPool.pop();
            enemy.resetState();
            enemy.move();
            enemy.setCurrentAttackTreeType(treeType);
            enemy.setSpritePosition(position);
            enemy.setState(Enemies.EnemyState.MOVING);
            enemy.setRightDirection(false);
            enemyManager.addEnemy(enemy);
            poolSent++;

        }
    }

    public void returnEnemy(EnemyController enemy) {
        if(enemyPool.contains(enemy, true)) {
            return;
        }
        if (enemy != null && poolSent > 0) {
            // Reset enemy state
            enemy.setSpritePosition(new Vector2(-100, -100)); // Move off-screen
            enemy.resetState();// Use a proper method instead of direct field access
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
