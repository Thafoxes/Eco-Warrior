package io.github.eco_warrior.sprite.Enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import io.github.eco_warrior.entity.Enemies;

import java.util.HashMap;
import java.util.Map;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;

public class Worm extends Enemies {

    //sound effects
    private final Sound attackSound = Gdx.audio.newSound(Gdx.files.internal("sound_effects/whip.mp3"));

    public Worm(Vector2 position, float scale) {
        super("atlas/worm/worm.atlas",
            "moving",
            4, //this is for moving frameCount only, later in the load Animation it will run through all the frames
            position,
            scale);
        originalPos = position;
        currentState = EnemyState.MOVING;
        previousState = EnemyState.MOVING;

        stateTime = 0f;
        isMoving = true;

        loadAnimations();
    }

    /**
     * This is normally where you add sound, animation, state of it.
     * @param position
     */
    public Worm(Vector2 position) {
        this(position, .2f);
    }

    @Override
    protected void loadAnimations() {
        // Load animations directly here
        atlas = new TextureAtlas(Gdx.files.internal("atlas/worm/worm.atlas"));

        // Create animations for different states
        animationMap.put(EnemyState.MOVING, new Animation<>(0.1f, atlas.findRegions("moving"), Animation.PlayMode.LOOP));
        animationMap.put(EnemyState.ATTACKING, new Animation<>(0.1f, atlas.findRegions("attack"), Animation.PlayMode.NORMAL));
        animationMap.put(EnemyState.DEAD, new Animation<>(0.15f, atlas.findRegions("death"), Animation.PlayMode.NORMAL));
        animationMap.put(EnemyState.IDLE, new Animation<>(0.15f, atlas.findRegions("idle"), Animation.PlayMode.LOOP));

        currentState = EnemyState.MOVING;

    }

    @Override
    protected void updateState(float delta) {
        if(previousState != currentState) {
            stateTime = 0;
        }

       Animation<TextureRegion> currentAnimation = animationMap.get(currentState);
       if(currentAnimation != null){
           TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, currentState == EnemyState.MOVING);
           getSprite().setRegion(currentFrame);
           getSprite().flip(isMoving, false);

           // Play sound if attacking
           if(currentState ==EnemyState.ATTACKING && previousState != currentState){
               attackSound.play(0.5f);
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


        previousState = currentState;
        stateTime += delta;
    }
}
