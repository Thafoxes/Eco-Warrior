package io.github.eco_warrior.controller.Enemy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import io.github.eco_warrior.controller.Trees.TreeController;
import io.github.eco_warrior.entity.Enemies;
import io.github.eco_warrior.enums.EnemyType;
import io.github.eco_warrior.sprite.Enemy.BombPecker;
import io.github.eco_warrior.sprite.Enemy.IceCrab;
import io.github.eco_warrior.sprite.Enemy.Tentacles;

public class TentaclesController extends EnemyController{


    private Tentacles tentacles;
    private TreeController<?> treeController; //take this to controller
    private boolean isAttacking = false;
    private boolean isDead = false; // Track if the Ice Crab is dead
    private float attackCooldown = 3.0f; // Cooldown for attack




    /***
     * This is where you add effects for the worm, such as explosion effects.
     * You can add more effects as needed.
     * Add text on top of the worm when it dies, or any other effects.
     * you can add sprite icon on top of the worm when it dies, or any other effects.
     */
    public TentaclesController(Vector2 position) {
        super(new Tentacles(position), EnemyType.TENTACLES);
        this.tentacles = (Tentacles) super.enemy;
    }

    @Override
    public void update(float delta) {
        tentacles.update(delta);
        if (isDead) {
            return;
        }

        switch(tentacles.getCurrentState()){
            case SPAWNING:
                if(tentacles.isCurrentAnimationDone()){
                    tentacles.setState(Enemies.EnemyState.IDLE);
                }
                break;
            case IDLE:
                //no action needed
                break;
            case ATTACKING:
                if(isAttacking){
                    if(tentacles.isCurrentAnimationDone()){
                        if(this.treeController != null) {
                            treeController.takeDamage(1);
                            treeController = null; // Reset the tree controller after attack
                        }
                        tentacles.setState(Enemies.EnemyState.IDLE);
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
                    if(tentacles.isCurrentAnimationDone()){
                        isDead = true; // Set dead state
                    }
                    break;
        }

    }

    public boolean isCurrentAnimationDone(){
        return tentacles.isCurrentAnimationDone();
    }

    @Override
    public void draw(SpriteBatch batch){
        tentacles.draw(batch);
    }

    public void attack(TreeController<?> treeController){
        if(!isAttacking){
            this.treeController = treeController;
            tentacles.setState(Enemies.EnemyState.ATTACKING);
            isAttacking = true;
        }
    }

    @Override
    public void attack(){
        if(!isAttacking && !isDead) {
            tentacles.attack();
            isAttacking = true;
        }
    }

    @Override
    public void die() {
        if (!isDead) {
            tentacles.die();
            isAttacking = false;
        }
    }

    @Override
    public boolean isDead() {
        return isDead;
    }

    @Override
    public void resetState(){
        super.resetState();
        Tentacles enemy = (Tentacles) this.enemy;
        enemy.resetState();
        isDead = false;
        isAttacking = false;
    }

    @Override
    public void drawDebug(ShapeRenderer shapeRenderer) {
        super.drawDebug(shapeRenderer);
    }

    @Override
    public void dispose() {
        tentacles.dispose();
        super.dispose();
    }

}
