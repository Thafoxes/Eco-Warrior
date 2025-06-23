package io.github.eco_warrior.controller.Pools;

import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.controller.Enemy.WormController;
import io.github.eco_warrior.controller.Manager.EnemyManager;


import static io.github.eco_warrior.constant.ConstantsVar.WINDOW_WIDTH;

public class WormPool extends EnemyPool<WormController>{

    public WormPool(EnemyManager enemyManager) {
        super(enemyManager);
    }

    @Override
    protected WormController createEnemy(Vector2 vector2) {
        return new WormController(vector2);
    }


}
