package io.github.eco_warrior.controller;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.animation.SmoothMovement;
import io.github.eco_warrior.sprite.tools.FlipFlop;

public class FlipFlopController {
    private FlipFlop flipFlop;
    private Vector2 originalPosition;
    private boolean isDragging = false;
    private boolean isReturning = false;
    private float returnSpeed = 5.0f;


    public FlipFlopController(Vector2 startPosition) {
        this.originalPosition = new Vector2(startPosition);
        this.flipFlop = new FlipFlop(originalPosition);
    }

    public boolean handleTouch(Vector2 touchPos) {
        if (flipFlop.getCollisionRect().contains(touchPos)) {
            isDragging = true;
            return true;
        }
        return false;
    }


    public void dragTo(Vector2 position) {
        if (!isDragging) return;

        // Move flip-flop to cursor position
        flipFlop.getSprite().setPosition(
            position.x - (flipFlop.getSprite().getWidth() * flipFlop.getSprite().getScaleX() / 2),
            position.y - (flipFlop.getSprite().getHeight() * flipFlop.getSprite().getScaleY() / 2)
        );

        // Update collision rectangle
        flipFlop.getCollisionRect().setPosition(
            flipFlop.getSprite().getX(),
            flipFlop.getSprite().getY()
        );
    }

    public void release() {
        isDragging = false;
        isReturning = true;
    }


    public void update(float delta) {
        if (isReturning) {
            boolean movementComplete = SmoothMovement.moveLerpToPosition(
                flipFlop,
                originalPosition,
                returnSpeed,
                0.05f
            );

            if (movementComplete) {
                isReturning = false;
            }
        }
    }

    public FlipFlop getFlipFlop() {
        return flipFlop;
    }

    public boolean isDragging() {
        return isDragging;
    }

    public boolean isReturning() {
        return isReturning;
    }

    public void draw(SpriteBatch batch) {
        flipFlop.draw(batch);
    }

    public void drawDebug(ShapeRenderer shapeRenderer) {
        flipFlop.drawDebug(shapeRenderer);
    }

    public void dispose() {
        flipFlop.dispose();
    }


}
