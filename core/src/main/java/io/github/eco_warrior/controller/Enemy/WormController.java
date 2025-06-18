package io.github.eco_warrior.controller.Enemy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.animation.Explosion.RedExplosion;
import io.github.eco_warrior.entity.Enemies;
import io.github.eco_warrior.sprite.Enemy.Worm;

public class WormController {
    private final Worm worm;
    private float moveSpeed = 100f;
    private RedExplosion deathEffect;
    private boolean isExploding = false;

    /***
     * This is where you add effects for the worm, such as explosion effects.
     * You can add more effects as needed.
     * Add text on top of the worm when it dies, or any other effects.
     * you can add sprite icon on top of the worm when it dies, or any other effects.
     */
    public WormController(Worm worm) {
        this.worm = worm;
        // Initialize effects for explosion
        initializeEffects();
    }

    private void initializeEffects() {

//        deathEffect = new RedExplosion(worm.getPosition(), 1f);
    }

    public void update(float delta) {

        //update worm animation state
        worm.update(delta);

        // Handle movement
        if (worm.getCurrentState() == Enemies.EnemyState.MOVING) {
            float direction = worm.isMoving() ? 1 : -1;
            Vector2 position = worm.getPosition();
            position.x += direction * moveSpeed * delta;
            worm.setPosition(position);
        }

        // Handle death effect
        if (worm.getCurrentState() == Enemies.EnemyState.DEAD && !isExploding) {
            Vector2 position = worm.getPosition();
            position.x += worm.getSprite().getWidth() /2; // Center the explosion effect
            position.y += worm.getSprite().getHeight() / 2; // Center the explosion effect
//            deathEffect.reset(position);
            isExploding = true;
        }

//        if (isExploding) {
//            deathEffect.update(delta);
//        }
    }

    public void attack(){

    }



    public void draw(SpriteBatch batch) {
        worm.draw(batch);
//        if (isExploding && !deathEffect.isFinished()) {
//            deathEffect.draw(batch);
//        }
    }

    public void dispose() {
//        deathEffect.dispose();
        worm.dispose();
    }


}
