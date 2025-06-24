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
        super("atlas/bomb_pecker/bomber_pecker_new.atlas",
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
        atlas = new TextureAtlas(Gdx.files.internal("atlas/bomb_pecker/bomber_pecker_new.atlas"));

        // Create animations for different states
        animationMap.put(EnemyState.MOVING, new Animation<>(0.1f, atlas.findRegions("move"), Animation.PlayMode.LOOP));
        animationMap.put(EnemyState.ATTACKING, new Animation<>(0.12f, atlas.findRegions("attack"), Animation.PlayMode.NORMAL));
        animationMap.put(EnemyState.DEAD, new Animation<>(0.10f, atlas.findRegions("shotdeath"), Animation.PlayMode.NORMAL));
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

    @Override
    public void update(float delta) {
        if (!isDead()) {
            stateTime += delta;
        }

        Animation<TextureRegion> currentAnimation = animationMap.get(currentState);

        switch (currentState){
            case MOVING:
                updateAnimationFrame(currentAnimation);
                break;
            case ATTACKING:
                break;
            case DEAD:
                //slap to death, not self explosion
                break;
        }

        handleAttackingAnimation(currentAnimation);

    }

    @Override
    protected void handleAttackingAnimation(Animation<TextureRegion> currentAnimation){
        // Play sound only when entering ATTACKING state
        if(currentState == EnemyState.ATTACKING && previousState != EnemyState.ATTACKING && !startPlay) {
            if (attackSound != null) {
                attackSound.play(0.5f);
            }
            startPlay = true;
        }

        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime,
            currentState == EnemyState.MOVING || currentState == EnemyState.IDLE);
        getSprite().setRegion(currentFrame);
        handleSpriteFlip();


    }

    @Override
    public void setState(EnemyState state){
        super.setState(state);
        stateTime = 0;
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


    public boolean isCurrentAnimationDone() {
        Animation<TextureRegion> currentAnimation = animationMap.get(currentState);
        return currentAnimation.isAnimationFinished(stateTime);
    }
}
