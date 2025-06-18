package io.github.eco_warrior.controller.Pools;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.eco_warrior.controller.Enemy.EnemyController;
import io.github.eco_warrior.controller.Enemy.WormController;
import io.github.eco_warrior.controller.Manager.EnemyManager;
import io.github.eco_warrior.entity.Enemies;

import static io.github.eco_warrior.constant.ConstantsVar.WINDOW_WIDTH;

public class WormPool{

    private Array<WormController> pooledWorms;
    private final int poolSize = 5;
    private EnemyManager enemyManager;
    private int poolSent = 0;

    public WormPool(EnemyManager enemyManager) {
        this.enemyManager = enemyManager;
        pooledWorms = new Array<>(poolSize);

        // Pre-initialize all worms
        for (int i = 0; i < poolSize; i++) {
            WormController worm = new WormController(new Vector2(WINDOW_WIDTH + 50F, -100)); // Off-screen position
            worm.setState(Enemies.EnemyState.IDLE);
            worm.setRightDirection(false); // change direction to left
            pooledWorms.add(worm);
        }
    }


    // Method to get a worm from the pool
    public WormController getWorm(Vector2 position) {
        if(pooledWorms.size > 0 && poolSent < poolSize) {
            WormController worm = pooledWorms.pop();
            worm.getSprite().setPosition(position.x, position.y);
            worm.setState(Enemies.EnemyState.MOVING);
            worm.setRightDirection(false); // Set direction to right
            enemyManager.addEnemy(worm); // Add to enemy manager
            poolSent++;
            System.out.println("WormPool: Worm sent to enemy manager. Total sent: " + poolSent);
            return worm;
        }
        // All worms are in use, return null or create a new one
        return null;
    }


    public void returnEnemy(WormController worm) {
        if (worm != null && poolSent > 0) {
            // Reset worm state
            worm.getSprite().setPosition(WINDOW_WIDTH + 50F, -100);
            worm.resetState();
            pooledWorms.add(worm);
            poolSent--;
        }
    }

    public int getActiveCount(){
        return poolSent;
    }

    public void setWormCount(int count){
        this.poolSent = count;
    }

    public void addWorm(WormController enemy) {
        pooledWorms.add(enemy);
    }
}
