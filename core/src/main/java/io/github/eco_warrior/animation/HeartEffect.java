package io.github.eco_warrior.animation;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class HeartEffect {
    private TextureAtlas.AtlasRegion heartRegion;
    private Vector2 position;
    private Vector2 originalPosition;
    private float scale;
    private float alpha;
    private boolean finished;

    // Variables for controlling the effect stages
    private float effectDuration = 1.5f;
    private float currentDuration = 0f;
    private static final float SHAKE_PHASE = 0.7f;  // 70% of time is shaking
    private static final float FADE_PHASE = 0.3f;   // 30% of time is fading
    private boolean isShaking = true;
    private boolean isFading = false;

    // Shake parameters
    private float shakeIntensity = 5.0f;
    private float shakeFrequency = 25.0f;

    public HeartEffect(TextureAtlas.AtlasRegion heartRegion, Vector2 position, float initialScale) {
        this.heartRegion = heartRegion;
        this.originalPosition = position;
        this.position = position;
        this.scale = initialScale;
        this.alpha = 1f; // Fully visible
        this.finished = false;
    }

    public void update(float delta) {
        currentDuration += delta;

        if (currentDuration <= effectDuration * SHAKE_PHASE) {
            // vibrating phase
            updateVibrating(delta);
        } else if (currentDuration <= effectDuration) {
            // Fading phase
            if (isShaking) {
                isShaking = false;
                isFading = true;
                // Reset position before starting fade
                position.set(originalPosition);
            }
            updateFading();
        } else {
            finished = true;
        }
    }

    private void updateVibrating(float delta){
        // Create small, rapid vibrations
        float shakeTime = currentDuration * shakeFrequency * 2;

        // Use small offsets for subtle vibration
        float offsetX = MathUtils.sin(shakeTime) * shakeIntensity;
        float offsetY = MathUtils.cos(shakeTime * 1.5f) * shakeIntensity;

        // Apply shake offset to position
        position.set(
            originalPosition.x + offsetX,
            originalPosition.y + offsetY
        );

        // Keep shake intensity constant for consistent vibration
        shakeIntensity = 2.0f; // Small intensity for vibration
    }


    private void updateShaking(float delta) {
        // Calculate shake offset based on time
        float shakeTime = currentDuration * shakeFrequency;
        float offsetX = MathUtils.sin(shakeTime) * shakeIntensity
            + MathUtils.sin(shakeTime * 2.7f) * (shakeIntensity * 0.5f);

        float offsetY = MathUtils.cos(shakeTime * 1.3f) * shakeIntensity
            + MathUtils.cos(shakeTime * 3.1f) * (shakeIntensity * 0.4f);

        // Apply shake offset to position
        position.set(
            originalPosition.x + offsetX,
            originalPosition.y + offsetY
        );

        // Gradually increase shake intensity
        shakeIntensity = Math.min(8.0f, shakeIntensity + delta * 5.0f);
    }


    private void updateFading() {
        // Calculate fade progress
        float fadeProgress = (currentDuration - (effectDuration * SHAKE_PHASE)) / (effectDuration * FADE_PHASE);

        // Apply scaling and fading
        scale = MathUtils.lerp(scale, 0f, fadeProgress);
        alpha = MathUtils.lerp(1.0f, 0.0f, fadeProgress);
    }

    public void draw(SpriteBatch batch) {
        if (!finished) {
            // Save the batch's current color
            float oldR = batch.getColor().r;
            float oldG = batch.getColor().g;
            float oldB = batch.getColor().b;
            float oldA = batch.getColor().a;

            // Set the new alpha for transparency
            batch.setColor(oldR, oldG, oldB, alpha);

            // Draw the heart with current effect
            float width = heartRegion.getRegionWidth() * scale;
            float height = heartRegion.getRegionHeight() * scale;

            // Draw from center
            float x = position.x + (width / 2);
            float y = position.y + (height / 2);


            batch.draw(heartRegion, position.x, position.y, width, height);

            // Restore the original color
            batch.setColor(oldR, oldG, oldB, oldA);
        }
    }

    public boolean isFinished() {
        return finished;
    }
}
