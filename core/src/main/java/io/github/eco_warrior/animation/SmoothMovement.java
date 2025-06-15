package io.github.eco_warrior.animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.gameSprite;

public class SmoothMovement {

    /**
     * Smoothly moves a game sprite toward a target position.
     * Design for gameSprite class.
     *
     * @param sprite The gameSprite class to move
     * @param targetPosition The position to move toward
     * @param speed The movement speed (higher = faster)
     * @param snapDistance Distance threshold to snap to target position
     * @return True if movement is complete, false if still moving
     */
    public static boolean moveLerpToPosition(gameSprite sprite, Vector2 targetPosition, float speed, float snapDistance) {
        float deltaTime = Gdx.graphics.getDeltaTime();

        // Current position
        Vector2 current = new Vector2(
            sprite.getSprite().getX(),
            sprite.getSprite().getY()
        );

        // Linear interpolation
        Vector2 lerped = current.lerp(targetPosition, speed * deltaTime);

        // Update position
        sprite.getSprite().setPosition(lerped.x, lerped.y);
        sprite.getCollisionRect().setPosition(lerped.x, lerped.y);

        // Check if we're close enough to the target
        if (current.dst(targetPosition) < snapDistance) {
            // Snap to exact position
            sprite.getSprite().setPosition(targetPosition.x, targetPosition.y);
            sprite.getCollisionRect().setPosition(targetPosition.x, targetPosition.y);
            return true; // Movement complete
        }

        return false; // Still moving
    }

    /***
     * example
     *  boolean movementComplete = SmoothMovement.moveToPosition(
     *             flipFlop,
     *             flipFlopOriginalPosition,
     *             flipFlopReturnSpeed,
     *             0.05f
     *         );
     */


}
