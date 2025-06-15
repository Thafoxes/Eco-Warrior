package io.github.eco_warrior.controller.Enemy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.animation.RedExplosion;
import io.github.eco_warrior.sprite.Enemy.Worm;

public class WormController {
    private final Worm worm;
    private float moveSpeed = 100f;
    private RedExplosion deathEffect;
    private boolean isExploding = false;

    public WormController(Worm worm) {
        this.worm = worm;
        // Initialize effects
        initializeEffects();
    }

    private void initializeEffects() {
        deathEffect = new RedExplosion(worm.getPosition(), 1f);
    }

    public void update(float delta) {

        //update worm animation state
        worm.update(delta);

        // Handle movement
        if (worm.getCurrentState() == Worm.WormState.MOVING) {
            float direction = worm.isMovingRight() ? 1 : -1;
            Vector2 position = worm.getPosition();
            position.x += direction * moveSpeed * delta;
            worm.setPosition(position);
        }

        // Handle death effect
        if (worm.getCurrentState() == Worm.WormState.DEAD && !isExploding) {
            deathEffect.reset(worm.getPosition());
            isExploding = true;
        }

        if (isExploding) {
            deathEffect.update(delta);
        }
    }


    public void draw(SpriteBatch batch) {
        worm.draw(batch);
        if (isExploding && !deathEffect.isFinished()) {
            deathEffect.draw(batch);
        }
    }

    public void dispose() {
        deathEffect.dispose();
        worm.dispose();
    }


}
