package io.github.eco_warrior.sprite;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.gameSprite;

public class WaterDrop extends gameSprite {
    private static final float GRAVITY = 200f;
    private Vector2 velocity;
    private float volume; // in liters
    private boolean active = true;

    public WaterDrop(Vector2 position) {
        super("atlas/water_resevior_funnel/water_drop.atlas",
            "water_drop",
            position, 0.5f
        );

        this.velocity = new Vector2(0, -50f); // Start with small downward velocity

        // Randomize volume between 0.5L and 1L
        this.volume = MathUtils.random(0.5f, 1.0f);
    }

    @Override
    public void update(float delta) {
        // Apply gravity
        velocity.y -= GRAVITY * delta;

        // Update position
        Rectangle collisionRect = getCollisionRect();
        collisionRect.x += velocity.x * delta;
        collisionRect.y += velocity.y * delta;

        super.update(delta);
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (active) {
            super.draw(batch);
        }
    }

    public Rectangle getBounds() {
        return getCollisionRect();
    }

    public float getVolume() {
        return volume;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public float getY() {
        return getCollisionRect().y;
    }

    public float getX() {
        return getCollisionRect().x;
    }
}
