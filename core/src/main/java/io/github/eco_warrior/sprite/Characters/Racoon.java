package io.github.eco_warrior.sprite.Characters;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.gameSprite;

public class Racoon extends gameSprite {
    private boolean isFreeze;
    private boolean isHit;
    private boolean isDying;
    private boolean shouldRemove;

    // Add frame animation timing control
    private float frameTimer = 0.0f;
    private final float FRAME_DURATION = 0.15f;
    private final int IDLE_FRAME = 7;
    private final int FRAME_SIZE = 512;

    public enum state {
        HIDDEN, SPAWN, IDLE, HIT, DYING
    }

    private state currentState = state.HIDDEN;

    public Racoon(Vector2 position, float scale)
    {
        super("character/raccoon/raccoon.atlas", "raccoon", 15, position, scale);
        isFreeze = false;
        isHit = false;
        isDying = false;
        shouldRemove = false;

        currentState = state.HIDDEN;
        resetFrame();

        fixSprite(scale);
    }

    private void fixSprite(float scale) {
        // Fix scaling issues by setting a consistent size for all frames
        Sprite sprite = getSprite();
        // Use the largest frame size from your atlas (approximately 497×482)
        sprite.setSize(FRAME_SIZE * scale, FRAME_SIZE * scale);
        // Center the sprite origin
        sprite.setOriginCenter();

        // Update collision rectangle to match sprite dimensions
        getCollisionRect().setWidth(sprite.getWidth());
        getCollisionRect().setHeight(sprite.getHeight());
    }

    public Racoon(Vector2 position) {
        this(position, 1.0f);
    }



    @Override
    public void update(float delta) {
        frameTimer += delta;

        // Update collision rectangle to match sprite dimensions and position
        getCollisionRect().set(
            getSprite().getX(),
            getSprite().getY(),
            getSprite().getWidth(),
            getSprite().getHeight()
        );

        if (!isFreeze) {
            if(currentState == state.HIT && getCurrentFrame() == IDLE_FRAME){
                currentState = state.DYING;
            }
        }

        if(currentState == state.DYING && getCurrentFrame() == getFrameCount() - 1){
            //remove itself
            shouldRemove = true;
        }else if(getCurrentFrame() == IDLE_FRAME && currentState == state.SPAWN){
            currentState = state.IDLE;
            isFreeze = true;

        }
        else{
            if(getCurrentFrame() < IDLE_FRAME && frameTimer >= FRAME_DURATION){
                nextFrame();
                frameTimer = 0f; //Reset frame timer

            }
        }


        // Call parent update to update position
        super.update(delta);
    }

    // Override nextFrame to also reset the timer
    @Override
    public void nextFrame() {
        // Get current sprite size before changing frame
        float width = getSprite().getWidth();
        float height = getSprite().getHeight();

        super.nextFrame();

        // Maintain consistent size after frame change
        getSprite().setSize(width, height);
    }

    public void setFreeze(){
        isFreeze = true;
    }

    public void unfreeze(){
        isFreeze = false;
    }

    public boolean isHit(){
        return isHit;
    }

    public void setHit(){
        if(currentState == state.IDLE){
            isHit = true;
            currentState = state.HIT;
            unfreeze();
            resetFrame(IDLE_FRAME);
            frameTimer = 0f;
        }
    }

    public boolean isDying() {
        return currentState == state.DYING;
    }

    public boolean isAnimationComplete() {
        return isFreeze && currentState == state.DYING;
    }

    public boolean shouldRemove() {
        return shouldRemove;
    }


}
