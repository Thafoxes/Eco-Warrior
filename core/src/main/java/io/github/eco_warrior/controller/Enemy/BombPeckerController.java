package io.github.eco_warrior.controller.Enemy;

import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.controller.Trees.TreeController;
import io.github.eco_warrior.entity.Enemies;
import io.github.eco_warrior.enums.EnemyType;
import io.github.eco_warrior.sprite.Enemy.BombPecker;

import static io.github.eco_warrior.constant.ConstantsVar.WINDOW_HEIGHT;

public class BombPeckerController extends EnemyController{

    private TreeController treeController;
    private BombPecker bombPecker;
    private boolean isDead = false;


    public BombPeckerController(Vector2 position) {
        super(new BombPecker(position), EnemyType.BOMB_PECKER);
        this.bombPecker = (BombPecker) super.enemy;
    }

    /**
     * use for test screen
     * @param position
     */
    public void setTargetPosition(Vector2 position){
        bombPecker.setPosition(new Vector2(position.x, WINDOW_HEIGHT));
    }

    @Override
    public void attack(){
        if(!isAttacking){
            BombPecker enemy = (BombPecker) this.enemy;
            enemy.setState(Enemies.EnemyState.ATTACKING);
            isAttacking = true;
        }
    }

    @Override
    public void resetState(){
        super.resetState();
        BombPecker enemy = (BombPecker) this.enemy;
        enemy.resetState();
        isDead = false;
        isAttacking = false;
    }


    @Override
    public void update(float delta) {
        bombPecker.update(delta);
        if(isDead){
            return;
        }

        switch(bombPecker.getCurrentState()){
            case MOVING:
                Vector2 position = bombPecker.getPosition();
                position.y -= moveSpeed * delta;
                enemy.setPosition(position);
               break;
            case ATTACKING:
                if(bombPecker.isCurrentAnimationDone()){
                    if( treeController != null) {
                        treeController.takeDamage(2);
                    }
                    isDead = true;
                }
                break;
            case DEAD:
                if(bombPecker.isCurrentAnimationDone()){
                    isDead = true; // Set dead state
                }
                break;
        }


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
        return isDead;
    }

}
