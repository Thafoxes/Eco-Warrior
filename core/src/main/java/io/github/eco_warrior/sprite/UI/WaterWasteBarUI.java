package io.github.eco_warrior.sprite.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.controller.fontGenerator;
import io.github.eco_warrior.enums.textEnum;

public class WaterWasteBarUI {
    private static final int MAX_WATER_LEVEL = 44; // Based on atlas frames

    private TextureAtlas waterMeterAtlas;
    private Sprite meterSprite;
    private float currentWaterLevel = 0f;
    private int currentFrame = 1; // Starting with first frame
    private boolean isFull = false;
    private Vector2 position;

    //font show
    private fontGenerator uiFont;
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

        uiFont = new fontGenerator(20, Color.WHITE, Color.BLACK);
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
        int newFrame = Math.min((int)currentWaterLevel, MAX_WATER_LEVEL);
        if (newFrame != currentFrame) {
            currentFrame = newFrame;
            updateMeterSprite();
            meterSprite.setPosition(position.x, position.y);

            if (currentFrame >= MAX_WATER_LEVEL) {
                isFull = true;
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
