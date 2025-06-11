package io.github.eco_warrior.sprite.Characters;

import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.gameSprite;

public class Racoon extends gameSprite {
    private boolean isFreeze;
    private boolean isHit;
    private boolean isDying;
    private boolean shouldRemove;

    // Add frame animation timing control
    private float frameTimer = 0.0f;
    private final float FRAME_DURATION = 0.1f;
    private final int IDLE_FRAME = 8;

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
    }

    public Racoon(Vector2 position) {
        this(position, 1.0f);
    }

    @Override
    public void update(float delta) {
        frameTimer += delta;

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
        super.nextFrame();
        frameTimer = 0f;
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
