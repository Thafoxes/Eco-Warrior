package io.github.eco_warrior.sprite.Enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import io.github.eco_warrior.LevelTwoScreen.WormPath;
import io.github.eco_warrior.entity.Trees;
import io.github.eco_warrior.entity.gameSprite;

import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;

public class Worm extends gameSprite {
    private static Vector2 originalPos;
    private WormState currentState;
    private WormState previousState;
    private float stateTime;
    private boolean isMovingRight;
    private float movementSpeed = 50f;
    private float attackCooldown = 1.5f;
    private float timeSinceLastAttack = 0f;
    private boolean canAttack = true;

    private TextureAtlas atlas;
    private Map<WormState, Animation<TextureRegion>> animationMap = new HashMap<>();


    public enum WormState {
        MOVING,
        IDLE,
        ATTACKING,
        DEAD
    }

    //sound effects
    private final Sound attackSound = Gdx.audio.newSound(Gdx.files.internal("sound_effects/whip.mp3"));

    public Worm(Vector2 position, float scale) {
        super("atlas/worm/worm.atlas",
            "moving",
            4, //this is for moving frameCount only, later in the load Animation it will run through all the frames
            position,
            scale);
        originalPos = position;
        currentState = WormState.MOVING;
        previousState = WormState.MOVING;

        stateTime = 0f;
        isMovingRight = true;

        loadAnimations();
    }

    /**
     * This is normally where you add sound, animation, state of it.
     * @param position
     */
    public Worm(Vector2 position) {
        this(position, .2f);
    }

    private void loadAnimations() {
        // Load animations directly here
        atlas = new TextureAtlas(Gdx.files.internal("atlas/worm/worm.atlas"));

        // Create animations for different states
        animationMap.put(WormState.MOVING, new Animation<>(0.1f, atlas.findRegions("moving"), Animation.PlayMode.LOOP));
        animationMap.put(WormState.ATTACKING, new Animation<>(0.1f, atlas.findRegions("attack"), Animation.PlayMode.NORMAL));
        animationMap.put(WormState.DEAD, new Animation<>(0.15f, atlas.findRegions("death"), Animation.PlayMode.NORMAL));
        animationMap.put(WormState.IDLE, new Animation<>(0.15f, atlas.findRegions("idle"), Animation.PlayMode.LOOP));

        currentState = WormState.MOVING;

    }



    public void update(float delta) {
        timeSinceLastAttack += delta;

        updateState(delta);

        if(currentState == WormState.MOVING){
            float moveAmount = movementSpeed * delta;
           moveBy(isMovingRight? moveAmount : -moveAmount, 0);

        }
    }


    private void updateState(float delta) {
        if(previousState != currentState) {
            stateTime = 0;
        }

       Animation<TextureRegion> currentAnimation = animationMap.get(currentState);
       if(currentAnimation != null){
           TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, currentState == WormState.MOVING);
           getSprite().setRegion(currentFrame);
           getSprite().flip(isMovingRight, false);

           // Play sound if attacking
           if(currentState ==WormState.ATTACKING && previousState != currentState ){
               attackSound.play(0.5f);
           }

           if(currentState == WormState.ATTACKING && currentAnimation.isAnimationFinished(stateTime)){
               setState(WormState.IDLE);
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


        previousState = currentState;
        stateTime += delta;
    }

    public void setState(WormState newState) {
        if (currentState != WormState.DEAD) { // Can't change state if dead
            this.currentState = newState;
        }
    }

    public void setDirection(boolean movingRight) {
        this.isMovingRight = movingRight;
    }

    public void attack() {
        if (timeSinceLastAttack >= attackCooldown && currentState != WormState.DEAD) {
            setState(WormState.ATTACKING);
            timeSinceLastAttack = 0;
        }
    }

    public void die() {
        setState(WormState.DEAD);
    }

    public boolean isDead() {
        return currentState == WormState.DEAD;
    }

    public WormState getCurrentState() {
        return currentState;
    }

    public void dispose() {
        attackSound.dispose();
    }

    public Vector2 getPosition() {
        return new Vector2(getSprite().getX(), getSprite().getY());
    }

    public boolean isMovingRight() {
        return isMovingRight;
    }

    public void setPosition(Vector2 position) {
        getSprite().setPosition(position.x, position.y);
        //originalPos = position; // Update original position
    }


    private void addAnimation(String moving, float v) {
    }


}
