package io.github.eco_warrior.sprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class ReservoirSprite {
    private Sprite sprite;
    private Rectangle bounds;
    private float waterLevel = 0f; // in liters
    private final float CAPACITY = 100f; // 100 liters
    private TextureRegion region;

    private static TextureAtlas reservoirAtlas;

    public ReservoirSprite(Vector2 position) {

        try {
            reservoirAtlas = new TextureAtlas(Gdx.files.internal("assets/atlas/water_resevior_funnel/water_resevoir.atlas"));
            // Get the region from the atlas
            region = reservoirAtlas.findRegion("resevior_pipe");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.sprite = new Sprite(region);
        this.sprite.setPosition(position.x, position.y);
        this.sprite.setScale(2f); // Scale up the reservoir
        updateBounds();
    }

    private void updateBounds() {
        bounds = new Rectangle(
            sprite.getX(), sprite.getY(),
            sprite.getWidth() * sprite.getScaleX(),
            sprite.getHeight() * sprite.getScaleY()
        );
    }

    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void addWater(float amount) {
        waterLevel = Math.min(waterLevel + amount, CAPACITY);
    }

    public float getWaterLevel() {
        return waterLevel;
    }

    public float getCapacity() {
        return CAPACITY;
    }

    public Vector2 getCenter() {
        return new Vector2(
            bounds.x + bounds.width / 2,
            bounds.y + bounds.height / 2
        );
    }

    // Static cleanup method
    public static void disposeTextures() {
        if (reservoirAtlas != null) {
            reservoirAtlas.dispose();
            reservoirAtlas = null;
        }
    }
}
