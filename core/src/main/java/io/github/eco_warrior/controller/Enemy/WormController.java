package io.github.eco_warrior.controller.Enemy;

import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.Enemies;
import io.github.eco_warrior.enums.EnemyType;
import io.github.eco_warrior.sprite.Enemy.Worm;

public class WormController extends EnemyController {
    private float moveSpeed = 50f;

    public WormController(Vector2 position) {
        super(new Worm(position), EnemyType.WORM);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
    }

    @Override
    public void attack() {
        super.attack();
        // Implement worm-specific attack logic here
        // For example, you can call the attack method of the worm sprite
        // worm.attack();
    }
}
