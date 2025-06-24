package io.github.eco_warrior.sprite.Enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.Enemies;

public class BombPecker extends Enemies {

    private final Sound attackSound = Gdx.audio.newSound(Gdx.files.internal("sound_effects/bomb_pecker_explosion_sfx.mp3"));
    private boolean isDead = false;
    private boolean isDoneAttacking = false;
    private boolean startPlay = false;


    public BombPecker(Vector2 position, float scale) {
        super("atlas/bomb_pecker/bomber_pecker.atlas",
            "move",
            1, // this is for moving frameCount only, later in the load Animation it will run through all the frames
            position,
            scale);

        originalPos = position;
        currentState = EnemyState.MOVING;
        previousState = EnemyState.MOVING;

        stateTime = 0f;
        isFromRightDirection = true;

        loadAnimations();
    }

    public BombPecker(Vector2 position) {
        this(position, 0.2f);
    }

    @Override
    protected void loadAnimations() {
        // Load animations directly here
        atlas = new TextureAtlas(Gdx.files.internal("atlas/bomb_pecker/bomber_pecker.atlas"));

        // Create animations for different states
        animationMap.put(EnemyState.MOVING, new Animation<>(0.1f, atlas.findRegions("move"), Animation.PlayMode.LOOP));
        animationMap.put(EnemyState.ATTACKING, new Animation<>(0.12f, atlas.findRegions("attack"), Animation.PlayMode.NORMAL));
        animationMap.put(EnemyState.DEAD, new Animation<>(0.10f, atlas.findRegions("dead"), Animation.PlayMode.NORMAL));
        animationMap.put(EnemyState.IDLE, new Animation<>(0.15f, atlas.findRegions("idle"), Animation.PlayMode.LOOP));

        currentState = EnemyState.MOVING;

    }

    @Override
    protected void loadAudio() {
        //sound_effects/bomb_pecker_explosion_sfx.mp3
        super.attackSound = attackSound;
    }


    @Override
    public boolean isDoneAttacking(){
        return isDoneAttacking && (currentState == EnemyState.DEAD || currentState == EnemyState.ATTACKING);
    }

    public void resetDoneAttacking() {
       isDoneAttacking = false;
    }

    public void hit(){
        currentState = EnemyState.DEAD;
    }


    @Override
    public void update(float delta) {
        if (!isDead()) {
            stateTime += delta;
        }

        Animation<TextureRegion> currentAnimation = animationMap.get(currentState);

        if(currentState == EnemyState.ATTACKING){

            if(currentAnimation.isAnimationFinished(stateTime)){
                //no animation
                isDoneAttacking = true;
                isDead = true;
                stateTime = 0;
            }
        }


        if(currentState == EnemyState.DEAD){
            //slap to death, not self explosion
            if (currentAnimation.isAnimationFinished(stateTime)) {
                //TODO - Check if this is the right way to handle death animation
                System.out.println("BombPecker animation dead is done!");
                isDead = true;
                stateTime = 0;
            }
        }
        updateAnimationFrame(currentAnimation);
        handleAttackingAnimation(currentAnimation);

    }

    @Override
    protected void handleAttackingAnimation(Animation<TextureRegion> currentAnimation){
        // Play sound only when entering ATTACKING state
        if(currentState == EnemyState.ATTACKING && previousState != currentState && !startPlay) {
            if (attackSound != null) {
                attackSound.play(0.5f);
            }
            startPlay = true;
        }


        if(currentState == EnemyState.ATTACKING && currentAnimation.isAnimationFinished(stateTime)){
            setState(EnemyState.DEAD);
            isDoneAnimation = true;
            stateTime = 0f;
            canAttack = false;
        }
    }


    @Override
    public boolean isDead() {
        return isDead;
    }

    @Override
    public void resetState(){
        super.resetState();
        isDead = false;
        isDoneAttacking = false;
        startPlay = false;
        currentState = EnemyState.IDLE;
        previousState = EnemyState.IDLE;

        stateTime = 0f;

    }


}
