package io.github.eco_warrior.controller.Enemy;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.Enemies;
import io.github.eco_warrior.enums.EnemyType;
import io.github.eco_warrior.sprite.Enemy.BombPecker;

import static io.github.eco_warrior.constant.ConstantsVar.WINDOW_HEIGHT;

public class BombPeckerController extends EnemyController{

    /***
     * @param enemy
     * @param enemyType
     */
    public BombPeckerController(Enemies enemy, EnemyType enemyType) {
        super(enemy, enemyType);
    }

    public BombPeckerController(Vector2 position) {
        super(new BombPecker(position), EnemyType.BOMB_PECKER);
    }

    public void setTargetPosition(Vector2 position){
        enemy.setPosition(new Vector2(position.x, WINDOW_HEIGHT));
    }

    @Override
    public void attack(){
        if(!isAttacking){
            super.attack();

        }
    }

    public boolean isDead(){
        return enemy.isDead();
    }



    @Override
    public void update(float delta) {
        enemy.update(delta);


        if (enemy.getCurrentState() == Enemies.EnemyState.MOVING) {
            Vector2 position = enemy.getPosition();
            position.y -= moveSpeed * delta;
            enemy.setPosition(position);
        }

    }
}
