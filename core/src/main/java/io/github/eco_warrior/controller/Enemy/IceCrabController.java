package io.github.eco_warrior.controller.Enemy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
    private float attackCooldown = 3.0f; // Cooldown for attack, can be adjusted

    public IceCrabController(Vector2 position) {
        super(new IceCrab(position), EnemyType.ICE_CRAB);
        this.iceCrab = (IceCrab) super.enemy;
    }

    @Override
    public void update(float delta) {
        iceCrab.update(delta);

        switch (iceCrab.getCurrentState()){
            case SPAWNING:
                if(iceCrab.isCurrentAnimationDone()){
                    System.out.println("Ice Crab spawning done");
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
//                                System.out.println("Ice Crab ready to attack again");
                            }
                        }, attackCooldown);
                    }
                }
                break;
            case DEAD:
                break;
        }

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

    @Override
    public void attack() {
        if(!isAttacking){
            isAttacking = true;
            System.out.println("Attacking without tree controller");
            iceCrab.attack();
        }
    }

    public boolean isDead() {
        return iceCrab.isDead();
    }

    @Override
    public void drawDebug(ShapeRenderer shapeRenderer) {
        super.drawDebug(shapeRenderer);
    }

    @Override
    public void die(){

    }


    @Override
    public void dispose() {
        iceCrab.dispose();
        super.dispose();
    }
}
