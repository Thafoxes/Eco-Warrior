package io.github.eco_warrior.sprite.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.controller.FontGenerator;
import io.github.eco_warrior.enums.textEnum;

public class WaterWasteBarUI {
    private static final int MAX_WATER_LEVEL = 40; // Based on atlas framesprivate static final int MAX_WATER_LEVEL = 40; // Normal max level before animation
    private static final int MAX_ANIMATION_FRAME = 44; // Final frame including animation
    private boolean isAnimating = false; // Track if we're in the animation sequence
    private float animationTimer = 0f; // Timer for controlling animation speed
    private static final float ANIMATION_FRAME_DURATION = 0.15f; // Time per animation frame in seconds

    private TextureAtlas waterMeterAtlas;
    private Sprite meterSprite;
    private float currentWaterLevel = 0f;
    private int currentFrame = 1; // Starting with first frame
    private boolean isFull = false;
    private Vector2 position;

    //font show
    private FontGenerator uiFont;
    private String labelText = "Water Waste Meter: ";


    public WaterWasteBarUI(float x, float y, float scale) {
        try{
        waterMeterAtlas = new TextureAtlas(Gdx.files.internal("atlas/water_meter/water_meter.atlas"));
         } catch (Exception e) {
            throw new RuntimeException(e);
        }
        position = new Vector2(x, y);

        updateMeterSprite();
        meterSprite.setPosition(x, y);
        meterSprite.setScale(scale);

        uiFont = new FontGenerator(20, Color.WHITE, Color.BLACK);
    }

    private void updateMeterSprite() {
        TextureRegion region = waterMeterAtlas.findRegion("1water_meter", currentFrame);
        if(region == null) {
            region = waterMeterAtlas.findRegion("1water_meter"); // Fallback to last frame
        }

        if (meterSprite == null) {
            meterSprite = new Sprite(region);
        } else {
            meterSprite.setRegion(region);
        }
    }


    public void addWater(float amount) {
        if (!isFull) {
            currentWaterLevel += amount;
            update();
        }
    }


    public void update() {
        // Handle normal water level updates
        if (!isAnimating) {
            int newFrame = Math.min((int) currentWaterLevel, MAX_WATER_LEVEL);
            if (newFrame != currentFrame) {
                currentFrame = newFrame;
                updateMeterSprite();
                meterSprite.setPosition(position.x, position.y);

                // If we've reached max level, start animation
                if (currentFrame >= MAX_WATER_LEVEL && !isFull) {
                    isAnimating = true;
                    animationTimer = 0f;
                    currentFrame = MAX_WATER_LEVEL; // Start animation from this frame
                }
            }
        }
        // Handle animation sequence when meter is full
        else {
            // Update animation timer
            animationTimer += Gdx.graphics.getDeltaTime();

            // Move to next animation frame based on timer
            if (animationTimer >= ANIMATION_FRAME_DURATION) {
                animationTimer -= ANIMATION_FRAME_DURATION;
                currentFrame++;

                // If we've gone through all animation frames
                if (currentFrame > MAX_ANIMATION_FRAME) {
                    // Either loop the animation or stop at the last frame
                    currentFrame = MAX_WATER_LEVEL + 1; // Restart animation from first explosion frame
                    isFull = true; // Mark as full
                }

                // Update the sprite with the new animation frame
                updateMeterSprite();
                meterSprite.setPosition(position.x, position.y);
            }
        }
    }

    public void drawWithLabel(SpriteBatch batch, OrthographicCamera camera) {

        meterSprite.draw(batch);

        // End the batch to prepare for font drawing (which has its own batch cycle)
        batch.end();

        // Draw the label text to the left of the sprite
        // The y position should be vertically centered with the sprite
        float textY = position.y  + (meterSprite.getHeight() * meterSprite.getScaleY() / 10f);
        float textX = position.x - (getTextWidth(labelText) - 130f);

        uiFont.fontDraw(batch, labelText, camera, new Vector2(textX, textY),
            textEnum.RIGHT, textEnum.Y_MIDDLE);


        // Start the batch again for the caller
        batch.begin();
    }

    private float getTextWidth(String text) {
        GlyphLayout layout = new GlyphLayout(uiFont.getFont(), text);
        return layout.width;
    }

    public void draw(SpriteBatch batch) {
        meterSprite.draw(batch);
    }

    public boolean isFull() {
        return isFull;
    }

    public void dispose() {
        waterMeterAtlas.dispose();
        uiFont.dispose();
    }


}
