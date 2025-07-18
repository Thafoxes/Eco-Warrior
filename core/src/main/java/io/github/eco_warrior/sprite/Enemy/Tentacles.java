package io.github.eco_warrior.sprite.Enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.Enemies;

public class Tentacles extends Enemies {

    private final Sound attackSound = Gdx.audio.newSound(Gdx.files.internal("sound_effects/tentacles/attack.mp3"));
    private final Sound spawnSound = Gdx.audio.newSound(Gdx.files.internal("sound_effects/tentacles/spawn.mp3"));
    private final Sound deathSound = Gdx.audio.newSound(Gdx.files.internal("sound_effects/tentacles/dead.mp3"));

    public Tentacles(Vector2 position) {
        super(
            "atlas/Tentacles/tentacles.atlas",
            "attack",
            1,
            position,
            0.2f
        );

        isFromRightDirection = false;
        loadAnimations();
        loadAudio();
    }

    @Override
    protected void loadAnimations() {
        atlas = new TextureAtlas(Gdx.files.internal("atlas/Tentacles/tentacles.atlas"));

        // Create animations for different states
        animationMap.put(EnemyState.SPAWNING, new Animation<>(0.3f, atlas.findRegions("spawn"), Animation.PlayMode.NORMAL));
        animationMap.put(EnemyState.ATTACKING, new Animation<>(0.3f, atlas.findRegions("attack"), Animation.PlayMode.NORMAL));
        animationMap.put(EnemyState.DEAD, new Animation<>(0.3f, atlas.findRegions("death"), Animation.PlayMode.NORMAL));
        animationMap.put(EnemyState.IDLE, new Animation<>(0.3f, atlas.findRegions("idle"), Animation.PlayMode.LOOP));

        currentState = EnemyState.SPAWNING;
    }

    @Override
    protected void loadAudio() {
        super.attackSound = attackSound;

    }

    @Override
    public void update(float delta) {
        // Update animation time
        stateTime += delta;

        Animation<TextureRegion> currentAnimation = animationMap.get(currentState);
        switch (currentState) {
            //dont handle the animation transition here, let controller handle it.

            case SPAWNING:
                if(currentAnimation.isAnimationFinished(stateTime)){
                    spawnSound.play();
                }
                break;
            case IDLE:
                //do nothing
                break;
            case ATTACKING:
                break;
            case DEAD:
                break;
        }
        updateAnimationFrame(currentAnimation);

    }

    public boolean isCurrentAnimationDone() {
        Animation<TextureRegion> currentAnimation = animationMap.get(currentState);
        return currentAnimation.isAnimationFinished(stateTime);
    }

    @Override
    protected void updateAnimationFrame(Animation<TextureRegion> currentAnimation) {
        if (currentAnimation == null) {
            System.err.println("Warning: Animation is null for state: " + currentState);
            // Default to IDLE animation if current animation is null
            currentAnimation = animationMap.get(EnemyState.IDLE);

            // If we still don't have an animation, return to avoid crash
            if (currentAnimation == null) {
                return;
            }
        }

        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime,
            currentState == EnemyState.IDLE);
        getSprite().setRegion(currentFrame);
        handleSpriteFlip();
    }

    @Override
    public void die(){
        if(currentState != EnemyState.DEAD) {
            setState(EnemyState.DEAD);
            attackSound.stop(); // Stop attack sound if it's playing
            deathSound.play();

        }
    }

    @Override
    public void setState(EnemyState state){
        super.setState(state);
        stateTime = 0;
    }

    @Override
    public void attack(){
        if(currentState != EnemyState.ATTACKING && currentState != EnemyState.DEAD) {
            setState(EnemyState.ATTACKING);
            attackSound.play();
            stateTime = 0; // Reset state time for new animation
        }
    }

    @Override
    public void resetState() {
        super.resetState();
        currentState = EnemyState.IDLE;
        previousState = EnemyState.IDLE;

        stateTime = 0f;

    }

    @Override
    public void dispose() {
        super.dispose();
        attackSound.dispose();
        spawnSound.dispose();
        deathSound.dispose();
        atlas.dispose();
    }

}
