package io.github.eco_warrior.sprite.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.controller.FontGenerator;
import io.github.eco_warrior.entity.gameSprite;
import io.github.eco_warrior.enums.textEnum;

public class WaterResevior extends gameSprite {

    private static final String ATLAS_PATH = "atlas/water_resevior_funnel/water_resevoir.atlas";
    private static final String REGION_NAME = "resevior_pipe";
    private Sound emptyingSound;
    //won't be using the water level, but keeping it for future reference
    private int waterCollected = 0;
    private FontGenerator font;

    public WaterResevior(Vector2 position, float scale) {
        super(ATLAS_PATH, REGION_NAME, position, scale);
        emptyingSound = Gdx.audio.newSound(Gdx.files.internal("sound_effects/water_pouring.mp3"));
        // Initialize font with white text and black border
        font = new FontGenerator(16, Color.WHITE, Color.BLACK);
    }

    public boolean receiveWater(int amount) {
        this.waterCollected += amount;
        emptyingSound.play();
        return true;
    }

    /**
     * Empties the reservoir and returns the amount of water collected
     * @return the amount of water collected
     * Won't be using this method for now, but keeping it for future reference
     */
    public int getWaterCollected() {
        return waterCollected;
    }

    @Override
    public void draw(SpriteBatch batch) {
        super.draw(batch);
    }

    /**
     * Draw the water reservoir with the water collected count on top
     * @param batch SpriteBatch to draw with
     * @param camera Camera for text positioning
     */
    public void drawWithWaterCount(SpriteBatch batch, OrthographicCamera camera) {
        // Draw the sprite first
        super.draw(batch);
        //stop this batch to draw the text
        batch.end();

        // Draw the text showing water collected
        // Calculate position above the sprite
        Vector2 spritePosition = new Vector2(
            (getSprite().getX() + getSprite().getWidth()) * getScale(),
            (getSprite().getY() + getSprite().getHeight()) * getScale() - 20f
        );


        String waterText = "Water Collected: " + waterCollected;
        font.fontDraw(batch, waterText, camera, spritePosition, textEnum.X_CENTER, textEnum.BOTTOM);

        //begin again
        batch.begin();
    }

    @Override
    public void dispose() {
        super.dispose();
        emptyingSound.dispose();
        font.dispose();
    }
}
