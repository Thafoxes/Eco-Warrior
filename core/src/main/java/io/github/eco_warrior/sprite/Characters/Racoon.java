package io.github.eco_warrior.sprite.Characters;

import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.gameSprite;

public class Racoon extends gameSprite {
    private boolean isFreeze;
    private boolean isHit;
    private boolean isDying;
    private boolean shouldRemove;

    public enum state {
        HIDDEN, SPAWN, IDLE, HIT, DYING
    }

    private state currentState = state.HIDDEN;

    public Racoon(Vector2 position, float scale)
    {
        super("character/raccoon/raccoon.atlas", "raccoon", position, scale);
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
        if (!isFreeze) {
            if(currentState == state.HIT && getCurrentFrame() == 9){
                currentState = state.DYING;
            }
        }

        if(currentState == state.DYING && getCurrentFrame() == getFrameCount() - 1){
            //remove itself
            shouldRemove = true;
        }else{
            nextFrame();
        }

        // Call parent update to update position
        super.update(delta);
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
            resetFrame(9);
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
