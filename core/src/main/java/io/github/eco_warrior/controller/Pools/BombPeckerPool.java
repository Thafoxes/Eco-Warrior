package io.github.eco_warrior.controller.Pools;

import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.controller.Enemy.BombPeckerController;
import io.github.eco_warrior.controller.Enemy.EnemyController;
import io.github.eco_warrior.controller.Enemy.MetalChuckController;
import io.github.eco_warrior.controller.Manager.EnemyManager;

import static io.github.eco_warrior.constant.ConstantsVar.WINDOW_WIDTH;

public class BombPeckerPool extends EnemyPool<BombPeckerController>{

    public BombPeckerPool(EnemyManager manager){
        super(manager);

        for (int i = 0; i < maxPoolSize; i++) {
            EnemyController enemy = createEnemy(new Vector2(WINDOW_WIDTH, -100)); // Off-screen position
            enemyPool.add(enemy);
        }
    }

    @Override
    protected BombPeckerController createEnemy(Vector2 vector2) {
        return new BombPeckerController(vector2);
    }
}
