package io.github.eco_warrior.sprite;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class CrackSprite extends Sprite {
    /** The type of crack (1, 2, or 3) */
    private int crackType;

    /** Whether this crack is currently visible */
    private boolean visible = true;

    /**
     * Base region name used in the atlas
     * Actual regions will be named crack1, crack2, crack3
     */
    private static final String BASE_REGION_NAME = "crack";
    private static final String CRACK_ATLAS_PATH = "atlas/crack/crackx64v2.atlas";

    private TextureAtlas atlas;


    public CrackSprite( int type, Vector2 position) {
        super();

        initialize(type, position.x, position.y);
    }

    public CrackSprite( int type, float x, float y) {
        super();
        initialize( type, x, y);
    }

    public CrackSprite( int type, Vector2 position, float scale) {
        super();
        initialize(type, position.x, position.y);
        setScale(scale);
    }

    public Rectangle getCollisionBox() {
        // Create a rectangle for collision detection
        Rectangle crackRect = new Rectangle(
            getX(),
            getY(),
            getWidth() * getScaleX(),
            getHeight() * getScaleY()
        );

        return crackRect;
    }
    private void initialize( int type, float x, float y) {
        this.atlas = new TextureAtlas(CRACK_ATLAS_PATH);
        // Ensure type is between 1 and 3
        this.crackType = Math.max(1, Math.min(3, type));

        // Get the texture region based on the type
        String regionName = BASE_REGION_NAME + this.crackType;
        TextureRegion region = atlas.findRegion(regionName);

        if (region == null) {
            throw new IllegalArgumentException("Could not find region: " + regionName + " in atlas");
        }

        // Set up the sprite
        setRegion(region);
        setSize(region.getRegionWidth(), region.getRegionHeight());
        setOrigin(getWidth() / 2, getHeight() / 2);

        // Position the sprite (centered on the given coordinates)
        setPosition(x - getWidth() / 2, y - getHeight() / 2);

    }

    public int getCrackType() {
        return crackType;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }

    @Override
    public void draw(Batch batch) {
        if (visible) {
            super.draw(batch);
        }
    }

    public Vector2 getWaterDropPosition() {
        return new Vector2(getX(), getY());
    }


    /**
     * Centers this crack at the given position
     *
     * @param x The x-coordinate of the center
     * @param y The y-coordinate of the center
     */
    public void centerAt(float x, float y) {
        setPosition(x - getWidth() / 2, y - getHeight() / 2);
    }


    public void dispose() {
        atlas.dispose();
    }
}
