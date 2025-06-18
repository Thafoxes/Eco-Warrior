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
    protected boolean isMoving;
    protected float movementSpeed = 50f;
    protected float attackCooldown = 1.5f;
    protected float timeSinceLastAttack = 0f;
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


    protected abstract void updateState(float delta);

    public void setState(EnemyState newState) {
        if (currentState != EnemyState.DEAD) { // Can't change state if dead
            this.currentState = newState;
        }
    }

    /**
     * Direction can be going from right to left
     * Or from top to bottom
     * Some enemies do not move
     * @param moving
     */
    public void setDirection(boolean moving) {
        this.isMoving = moving;
    }

    public void attack() {
        if (timeSinceLastAttack >= attackCooldown && currentState != EnemyState.DEAD) {
            setState(EnemyState.ATTACKING);
            timeSinceLastAttack = 0;
        }
    }

    public void render(SpriteBatch batch) {
        // Draw the sprite
        this.getSprite().draw(batch);
    }

    public void update(float delta) {
        timeSinceLastAttack += delta;

        updateState(delta);

        if(currentState == EnemyState.MOVING){
            float moveAmount = movementSpeed * delta;
            moveBy(isMoving ? moveAmount : -moveAmount, 0);

        }
    }

    public void die() {
        setState(EnemyState.DEAD);
    }

    public boolean isDead() {
        return currentState == EnemyState.DEAD;
    }

    public EnemyState getCurrentState() {
        return currentState;
    }


    public void dispose() {
        attackSound.dispose();
    }


    public Vector2 getPosition() {
        return new Vector2(getSprite().getX(), getSprite().getY());
    }

    public boolean isMoving() {
        return isMoving;
    }


    public void setPosition(Vector2 position) {
        getSprite().setPosition(position.x, position.y);
        //originalPos = position; // Update original position
    }


    private void addAnimation(String moving, float v) {
    }

}
