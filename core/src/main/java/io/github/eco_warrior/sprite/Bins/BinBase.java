package io.github.eco_warrior.sprite.Bins;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.GameSprite;
import io.github.eco_warrior.enums.ERecycleMap;

public abstract class BinBase extends GameSprite {
    //animation
    private float animationTimer = 0f;
    private boolean isAnimating = false;
    private float animationDuration = 0.3f;

    private ERecycleMap acceptingMaterial = ERecycleMap.glass_bottle;

    // Add method to return bin type
    public abstract String getBinType();

    public BinBase(String regionName, Vector2 pos,
                   String correctSoundPath, String wrongSoundPath, String hittingSoundPath) {
        super("atlas/bins/recyclables_bin.atlas",
            regionName,
            pos,
            0.4f,
            correctSoundPath,
            wrongSoundPath,
            hittingSoundPath);
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (isAnimating) {
            animationTimer += Gdx.graphics.getDeltaTime();


            Vector2 originalPosition = new Vector2(getSprite().getX(), getSprite().getY());

            //vibration of clicked bin
            if (animationTimer < animationDuration) {
                float shakeIntensity = 3f; // pixels to shake
                float offsetX = (float)(Math.sin(animationTimer * 60) * shakeIntensity);
                float offsetY = (float)(Math.cos(animationTimer * 60) * shakeIntensity);

                // Apply jitter effect
                getSprite().setPosition(originalPosition.x + offsetX, originalPosition.y + offsetY);
            } else {
                // Reset position and stop animation
                getSprite().setPosition(originalPosition.x, originalPosition.y);
                isAnimating = false;
            }


        }
        super.draw(batch);
    }

    /***
     * Check if the bin is pressed by the cursor position.
     * If pressed, play the hitting sound and start the animation.
     *
     * @param cursorPosition The position of the cursor.
     * @return true if the bin is pressed, false otherwise.
     */
    @Override
    public boolean isPressed(Vector2 cursorPosition){
        if(getCollisionRect().contains(cursorPosition)){
            getHittingSFX().play();

            // Start the animation (vibration effect)
            isAnimating = true;
            animationTimer = 0f;

            return true;
        }
        return false;
    }


    public boolean isCorrectCategory(ERecycleMap categoryPile){
        if(acceptingMaterial.equals(categoryPile)){
            return true;
        }else{
            return false;
        }
    }


}
