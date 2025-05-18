package io.github.eco_warrior.sprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class WaterDrop {
    private static final float GRAVITY = 200f;
    private Vector2 position;
    private Vector2 velocity;
    private Sprite dropSprite;
    private float volume; // in liters
    private boolean active = true;
    private static Texture texture;

    public WaterDrop(Vector2 position) {


        this.position = new Vector2(position);
        this.velocity = new Vector2(0, -50f); // Start with small downward velocity


        // edit the file path here
        texture = new Texture(Gdx.files.internal("water_drop.png"));
        this.dropSprite = new Sprite(texture);
        this.dropSprite.setPosition(position.x, position.y);
        this.dropSprite.setScale(0.5f); // Small drop size


        // Randomize volume between 0.5L and 1L
        this.volume = MathUtils.random(0.5f, 1.0f);
    }

    public void update(float delta) {
        // Apply gravity
        velocity.y -= GRAVITY * delta;

        // Update position
        position.add(velocity.x * delta, velocity.y * delta);
        dropSprite.setPosition(position.x, position.y);
    }

    public void draw(SpriteBatch batch) {
        if (active) {
            dropSprite.draw(batch);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(
            position.x, position.y,
            dropSprite.getWidth() * dropSprite.getScaleX(),
            dropSprite.getHeight() * dropSprite.getScaleY()
        );
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
        return position.y;
    }

}
