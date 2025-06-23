package io.github.eco_warrior.controller.Enemy;

import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.controller.Trees.TreeController;
import io.github.eco_warrior.entity.Enemies;
import io.github.eco_warrior.enums.EnemyType;
import io.github.eco_warrior.sprite.Enemy.BombPecker;

import static io.github.eco_warrior.constant.ConstantsVar.WINDOW_HEIGHT;

public class BombPeckerController extends EnemyController{

    private boolean dead = false;
    private TreeController treeController;


    public BombPeckerController(Vector2 position) {
        super(new BombPecker(position), EnemyType.BOMB_PECKER);
    }

    public void setTargetPosition(Vector2 position){
        enemy.setPosition(new Vector2(position.x, WINDOW_HEIGHT));
    }

    @Override
    public void attack(){
        if(!isAttacking){
            BombPecker enemy = (BombPecker) this.enemy;
            enemy.attack();
            isAttacking = true;
        }
    }

    @Override
    public void resetState(){
        super.resetState();
        BombPecker enemy = (BombPecker) this.enemy;
        enemy.resetState();
        dead = false;
        isAttacking = false;
    }


    /**
     * This method is used to check if the enemy is done attacking.
     * If the enemy is done attacking, it will deal damage to the tree.
     * It will remove itself from the treeController and return true.
     * @param treeController
     * @return boolean
     */
    @Override
    public void isAnimDoneAttacking(TreeController<?> treeController){
        this.treeController = treeController;
    }


    /**
     * This method is used in test for checking if the enemy is death by gun. NOT BY EXPLOSION.
     * @return TextureRegion
     */
    @Override
    public boolean isDead(){
        BombPecker enemy = (BombPecker) this.enemy;
        return enemy.isDead() && dead;
    }


    @Override
    public void update(float delta) {
        BombPecker enemy = (BombPecker) this.enemy;
        enemy.update(delta);

        if (enemy.getCurrentState() == Enemies.EnemyState.MOVING) {
            Vector2 position = enemy.getPosition();
            position.y -= moveSpeed * delta;
            enemy.setPosition(position);
        }

        if(enemy.isDoneAttacking() ) {
            if( treeController != null) {
                treeController.takeDamage(2);
            }
            dead = true;
        }

        if( enemy.getCurrentState() == Enemies.EnemyState.DEAD) {
            //TODO - IF KILLED BY GUN, THEN IT IS DEAD.
            System.out.println("BombPeckerController is dead");
            dead = true;
        }

        if(enemy.isDead()){
            dead = true;

        }

    }
}
