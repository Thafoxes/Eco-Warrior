package io.github.eco_warrior.controller.Enemy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.controller.Trees.TreeController;
import io.github.eco_warrior.enums.EnemyType;
import io.github.eco_warrior.sprite.Enemy.IceCrab;

public class IceCrabController extends EnemyController{

    private IceCrab iceCrab;
    private TreeController<?> treeController; //take this to controller
    private boolean isAttacking = false;

    public IceCrabController(Vector2 position) {
        super(new IceCrab(position), EnemyType.ICE_CRAB);
        this.iceCrab = (IceCrab) super.enemy;
    }

    @Override
    public void update(float delta) {
        iceCrab.update(delta);

        switch (iceCrab.getCurrentState()){
            case SPAWNING:
                break;
            case IDLE:
                break;
                case ATTACKING:
                    if(isAttacking){
                        if(!iceCrab.isDoneAttacking()){
                            break;
                        }else{
                            if (this.treeController != null) {
                                treeController.takeDamage(1);
                                isAttacking = false;
                                treeController = null; // Reset the tree controller after attack
                            }
                        }

                    }
                    break;
            case DEAD:
                break;
        }

    }

    @Override
    public void draw(SpriteBatch batch) {
//        super.draw(batch);
        iceCrab.draw(batch);
    }

    public void attack(TreeController<?> treeController){
        if(!isAttacking){
            this.treeController = treeController;
            iceCrab.attack();
            isAttacking = true;
        }
    }

    @Override
    public void attack() {
        iceCrab.attack();
    }

    public boolean isDead() {
        return iceCrab.isDead();
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
