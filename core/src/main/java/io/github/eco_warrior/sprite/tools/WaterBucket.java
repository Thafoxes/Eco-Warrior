package io.github.eco_warrior.sprite.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.gameSprite;

public class WaterBucket extends gameSprite {

    private enum WaterBucketState {
        EMPTY,
        HALF_FULL,
        FILLED
    }

    private int waterLevel = WaterBucketState.EMPTY.ordinal();
    private int waterDropCount = 0;

    //Water capacity thresholds
    private int halfFullThreshold = 10;
    private int maxCapacity = 20;

    private boolean isFull = false;

    private Sound fillSound;
    private Sound pourSound;

    public WaterBucket(Vector2 position, float scale) {
        super(
            "atlas/bucket/bucket_anim.atlas",
            "bucket",
            3,
            position,
            scale);

        setFrame(WaterBucketState.EMPTY.ordinal());
        fillSound = Gdx.audio.newSound(Gdx.files.internal("sound_effects/fill_water_sfx.mp3"));
        pourSound = Gdx.audio.newSound(Gdx.files.internal("sound_effects/sonido-correcto.mp3"));

    }

    public WaterBucket(Vector2 position, float scale, int halfFullThreshold, int maxCapacity) {
        this(position, scale);
        this.halfFullThreshold = halfFullThreshold;
        this.maxCapacity = maxCapacity;
        updateBucketApperance();

    }

    public boolean catchWaterDrop() {
       if(isFull){
           return false;
       }
        waterDropCount++;
        updateBucketApperance();
        fillSound.play(0.5f);

        return true;


    }

    private void updateBucketApperance() {
        //update animation based on water level
        if (waterDropCount >= maxCapacity) {
            waterLevel = WaterBucketState.FILLED.ordinal();
            isFull = true;

        } else if (waterDropCount >= halfFullThreshold) {
            waterLevel = WaterBucketState.HALF_FULL.ordinal();

        } else {
            waterLevel = WaterBucketState.EMPTY.ordinal();

        }

        setFrame(waterLevel);
    }

    /**
     * Empty the bucket into the reservoir
     * @param reservoir The water reservoir sprite
     * @return true if successfully emptied
     */
    public boolean emptyIntoReservoir(gameSprite reservoir) {

        if (getCollisionRect().overlaps(reservoir.getCollisionRect()) && waterDropCount > 0) {
            // Reset water level
            waterDropCount = 0;
            waterLevel = WaterBucketState.EMPTY.ordinal();
            pourSound.play(0.5f);
            isFull = false;
            setFrame(waterLevel);
            return true;
        }
        return false;
    }

    public boolean isFull(){
        return isFull;

    }

    @Override
    public void draw(SpriteBatch batch) {
        super.draw(batch);
    }

    public void dispose() {
        super.dispose();
        fillSound.dispose();
        pourSound.dispose();
    }

    public int getWaterDropCount() {
        return waterDropCount;
    }


}
