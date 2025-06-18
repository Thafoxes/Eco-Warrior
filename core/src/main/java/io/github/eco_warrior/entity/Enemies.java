package io.github.eco_warrior.entity;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;

import java.util.HashMap;
import java.util.Map;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;

public abstract class Enemies extends GameSprite{



    public enum EnemyState {
        MOVING,
        IDLE,
        ATTACKING,
        DEAD
    }

    protected Vector2 originalPos;
    protected EnemyState currentState;
    protected EnemyState previousState;
    protected float stateTime;
    // Direction of movement, true for right, false for left
    protected boolean isRightDirection = false;
    protected float movementSpeed = 50f;
    protected float attackCooldown = 1.5f;
    protected boolean canAttack = true;

    protected TextureAtlas atlas;
    protected Map<EnemyState, Animation<TextureRegion>> animationMap = new HashMap<>();

    //sound effects
    protected Sound attackSound;

    public Enemies(String atlasPath, String regionBaseName, int frameCount, Vector2 position, float scale) {
        super(atlasPath,
            regionBaseName,
            frameCount,
            position,
            scale);
    }

    protected abstract void loadAnimations();
    protected abstract void loadAudio();

    public void resetState(){
        System.out.println("Enemies :  "+ this.stateTime);
        this.currentState = EnemyState.IDLE;
        this.previousState = EnemyState.IDLE;
        this.stateTime = 0f;
        this.isRightDirection = false;
        this.canAttack = true;
        // Reset animations
        loadAnimations();
        loadAudio();
    }

    public void setState(EnemyState newState) {
        if (currentState != EnemyState.DEAD) { // Can't change state if dead
            this.currentState = newState;
        }
    }

    /**
     * Direction can be going from right to left
     * Or from top to bottom
     * Some enemies do not move
     */
    public void setDirection() {
        this.isRightDirection = !isRightDirection;
    }

    public void setDirection(boolean isRightDirection) {
        this.isRightDirection = isRightDirection;
    }

    public void attack() {
        if (canAttack && currentState != EnemyState.DEAD) {
            setState(EnemyState.ATTACKING);
            canAttack = false;
            stateTime = 0;
        }
    }

    public void render(SpriteBatch batch) {
        // Draw the sprite
        this.getSprite().draw(batch);
    }

    public void update(float delta) {

        updateState(delta);

    }

    public void updateState(float delta){
        if(previousState != currentState) {
            stateTime = 0;
        }

        Animation<TextureRegion> currentAnimation = animationMap.get(currentState);
        if (currentAnimation != null) {
            if (currentState == EnemyState.ATTACKING) {
                handleAttackingAnimation(currentAnimation);
            }
            updateAnimationFrame(currentAnimation);

        }

        previousState = currentState;
        stateTime += delta;
    }

    public void move() {
        setState(EnemyState.MOVING);
    }

    public void die() {
        setState(EnemyState.DEAD);
    }

    public boolean isDead() {
        return currentState == EnemyState.DEAD && animationMap.get(currentState).isAnimationFinished(stateTime);
    }

    public boolean isDoneAttacking() {
        return currentState == EnemyState.ATTACKING && animationMap.get(currentState).isAnimationFinished(stateTime);
    }

    public EnemyState getCurrentState() {
        return currentState;
    }


    public void dispose() {
        attackSound.dispose();
    }


    public boolean isRightDirection() {
        return isRightDirection;
    }



    private void addAnimation(String moving, float v) {
    }


    protected void handleAttackingAnimation(Animation<TextureRegion> currentAnimation) {
        // Play sound only when entering ATTACKING state
        if(currentState == EnemyState.ATTACKING && previousState != currentState) {
            if (attackSound != null) {
                attackSound.play(0.5f);
            }
        }


        if(currentState == EnemyState.ATTACKING && currentAnimation.isAnimationFinished(stateTime)){
            setState(EnemyState.IDLE);
            stateTime = 0f;
            canAttack = false;
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    canAttack = true;
                }
            }, attackCooldown);
        }
    }

    public void setAttackCooldown(float attackCooldown) {
        this.attackCooldown = attackCooldown;
    }

    public float getAttackCooldown() {
        return attackCooldown;
    }

    protected void updateAnimationFrame(Animation<TextureRegion> currentAnimation) {
        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime,
            currentState == EnemyState.MOVING || currentState == EnemyState.IDLE);
        getSprite().setRegion(currentFrame);
        handleSpriteFlip();
    }

    protected void handleSpriteFlip() {
        getSprite().flip(isRightDirection, false);
    }

}
