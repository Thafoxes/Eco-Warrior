package io.github.eco_warrior.controller.Enemy;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.controller.Trees.TreeController;
import io.github.eco_warrior.entity.BaseExplosion;
import io.github.eco_warrior.entity.Enemies;
import io.github.eco_warrior.enums.EnemyType;
import io.github.eco_warrior.enums.TreeType;

public abstract class EnemyController {
    protected Enemies enemy;
    protected static final float moveSpeed = 50f;
    protected BaseExplosion deathEffect;
    protected boolean isExploding = false;
    protected EnemyType enemyType;
    protected boolean isAttacking = false;
    protected TreeType currentAttackTreeType = null; // The tree type that the worm is currently attacking

    /***
     * This is where you add effects for the worm, such as explosion effects.
     * You can add more effects as needed.
     * Add text on top of the worm when it dies, or any other effects.
     * you can add sprite icon on top of the worm when it dies, or any other effects.
     */
    public EnemyController(Enemies enemy, EnemyType enemyType) {
        this.enemy = enemy;
        this.enemyType = enemyType;
        // Initialize effects for explosion
        initializeEffects();
    }

    private void initializeEffects() {

//        deathEffect = new RedExplosion(worm.getPosition(), 1f);
    }

    public void update(float delta) {
        //update worm animation state
        enemy.update(delta);

        if (enemy.getCurrentState() == Enemies.EnemyState.MOVING) {
            float direction = enemy.isRightDirection() ? 1 : -1;
            Vector2 position = enemy.getPosition();
            position.x += direction * moveSpeed * delta;
            enemy.setPosition(position);
        }

        // Handle death effect
        if (enemy.getCurrentState() == Enemies.EnemyState.DEAD && !isExploding) {
            Vector2 position = enemy.getPosition();
            position.x += enemy.getSprite().getWidth() /2; // Center the explosion effect
            position.y += enemy.getSprite().getHeight() / 2; // Center the explosion effect
            isExploding = true;
        }

    }

    public void die(){
        enemy.die();
    }

    public boolean isDead() {
        return enemy.isDead();
    }

    public void attack(){
        if(enemy.isCanAttack()){
            isAttacking = true;
            enemy.attack();
        }
    }

    public boolean isAnimDoneAttacking(TreeController<?> treeController) {

        if(isAttacking && (enemy.isDoneAttacking() || enemy.getCurrentState() == Enemies.EnemyState.IDLE)) {
            isAttacking = false;
            treeController.takeDamage(1);
            return true;
        }else{
            return false;
        }
    }

    public void move(){
        enemy.move();
    }

    public void resetState() {
        isAttacking = false;
        currentAttackTreeType = null; // Reset the current attack tree type
        enemy.resetState();
    }

    public void setCurrentAttackTreeType(TreeType treeType) {
        this.currentAttackTreeType = treeType;
    }

    public TreeType getCurrentAttackTreeType() {
        return currentAttackTreeType;
    }

    public void setState(Enemies.EnemyState state) {
        enemy.setState(state);
    }


    public void setSpritePosition(Vector2 position) {
        enemy.setPosition(position);
    }

    public Sprite getSprite() {
        return enemy.getSprite();
    }

    public Rectangle getCollisionRect() {
        return enemy.getCollisionRect();
    }

    public void draw(SpriteBatch batch) {
        enemy.draw(batch);
//        if (isExploding && !deathEffect.isFinished()) {
//            deathEffect.draw(batch);
//        }
    }

    public void changeDirection(){
        enemy.setDirection();
    }

    public void setRightDirection(boolean isRightDirection) {
        enemy.setDirection(isRightDirection);
    }

    public void drawDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.RED);
        Rectangle spriteBounds = enemy.getSprite().getBoundingRectangle();
        shapeRenderer.rect(spriteBounds.x - 5f, spriteBounds.y - 5, spriteBounds.width + 10 , spriteBounds.height + 10);

        // Draw collision bounds in green
        shapeRenderer.setColor(Color.GREEN);
        Rectangle collisionBounds = enemy.getCollisionRect();
        shapeRenderer.rect(collisionBounds.x, collisionBounds.y, collisionBounds.width, collisionBounds.height);
    }


    public void dispose() {
//        deathEffect.dispose();
        enemy.dispose();
    }

    public void resetAttackState() {
        isAttacking = false;
        // Reset any attack-related variables
    }

    public boolean isAttacking() {
        return isAttacking;
    }


}
