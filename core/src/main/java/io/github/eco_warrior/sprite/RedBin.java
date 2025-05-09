package io.github.eco_warrior.sprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.gameSprite;
import io.github.eco_warrior.enums.ERecycleMap;

public class RedBin extends gameSprite {
    //animation
    private float animationTimer = 0f;
    private boolean isAnimating = false;
    private float animationDuration = 0.3f;

    private ERecycleMap acceptingMaterial = ERecycleMap.trash_pile;
    public RedBin(Vector2 pos) {
        super(
            "sprite/bins/bins.atlas",
            "waste_bin",
            pos,
            7f ,
            "sound_effects/correct.mp3",
            "sound_effects/wrong.mp3",
            "sound_effects/hitting_bin.mp3"
            );
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (isAnimating) {
            animationTimer += Gdx.graphics.getDeltaTime();


            Vector2 originalPosition = new Vector2(getSprite().getX(), getSprite().getY());

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

    @Override
    public boolean isPressed(Vector2 cursorPosition){
        if(getCollisionRect().contains(cursorPosition)){
            getHittingSFX().play();

            // Start the animation
            isAnimating = true;
            animationTimer = 0f;

            return true;
        }
        return false;
    }


}
