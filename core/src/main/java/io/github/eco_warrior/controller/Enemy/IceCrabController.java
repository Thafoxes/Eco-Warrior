package io.github.eco_warrior.controller.Enemy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import io.github.eco_warrior.controller.Trees.TreeController;
import io.github.eco_warrior.entity.Enemies;
import io.github.eco_warrior.enums.EnemyType;
import io.github.eco_warrior.sprite.Enemy.IceCrab;

public class IceCrabController extends EnemyController{

    private IceCrab iceCrab;
    private TreeController<?> treeController; //take this to controller
    private boolean isAttacking = false;
    private boolean isDead = false; // Track if the Ice Crab is dead
    private float attackCooldown = 3.0f; // Cooldown for attack, can be adjusted

    public IceCrabController(Vector2 position) {
        super(new IceCrab(position), EnemyType.ICE_CRAB);
        this.iceCrab = (IceCrab) super.enemy;
    }

    @Override
    public void update(float delta) {
        iceCrab.update(delta);
        if(isDead){
            return;
        }


        switch (iceCrab.getCurrentState()){
            case SPAWNING:
                if(iceCrab.isCurrentAnimationDone()){
                    iceCrab.setState(Enemies.EnemyState.IDLE);
                }
                break;
            case IDLE:
                break;
            case ATTACKING:
                if(isAttacking){
                    if(iceCrab.isCurrentAnimationDone()){
                        if (this.treeController != null) {
                            treeController.takeDamage(1);
                            treeController = null; // Reset the tree controller after attack
                        }
                        iceCrab.setState(Enemies.EnemyState.IDLE);
                        // Use Timer for attack cooldown
                        Timer.schedule(new Timer.Task() {
                            @Override
                            public void run() {
                                isAttacking = false;
                            }
                        }, attackCooldown);
                    }
                }
                break;
            case DEAD:
                if(iceCrab.isCurrentAnimationDone()){
                    isDead = true; // Set dead state
                }
                break;
        }

    }

    public boolean isCurrentAnimationDone(){
        return iceCrab.isCurrentAnimationDone();
    }

    @Override
    public void draw(SpriteBatch batch) {
        iceCrab.draw(batch);
    }

    public void attack(TreeController<?> treeController){
        if(!isAttacking){
            isAttacking = true;
            this.treeController = treeController;
            iceCrab.attack();
        }
    }

    /**
     * This method is used to attack without a specific tree controller.
     * It can be used for testing or when no tree is targeted.
     */
    @Override
    public void attack() {
        if(!isAttacking){
            isAttacking = true;
            iceCrab.attack();
        }
    }

    @Override
    public void die(){
        if(iceCrab.getCurrentState() != Enemies.EnemyState.DEAD) {
            iceCrab.die();
            isAttacking = false; // Reset attack state on death
        }
    }

    public boolean isDead() {
        return isDead;
    }

    @Override
    public void drawDebug(ShapeRenderer shapeRenderer) {
        super.drawDebug(shapeRenderer);
    }




    @Override
    public void dispose() {
        iceCrab.dispose();
        super.dispose();
    }
}
