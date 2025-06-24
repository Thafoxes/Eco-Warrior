package io.github.eco_warrior.sprite.Enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.Enemies;

public class IceCrab extends Enemies {

    //sound effects
    private final Sound attackSound = Gdx.audio.newSound(Gdx.files.internal("sound_effects/ice_crab/ice_crab_attack.mp3"));
    private final Sound spawnSound  = Gdx.audio.newSound(Gdx.files.internal("sound_effects/ice_crab/ice_crab_spawn.mp3"));


    private boolean isdoneAttack = false;

    public IceCrab(Vector2 position) {
        super("atlas/IceCrab/ice_crab.atlas",
            "spawn", 1, position, 0.2f);

        isFromRightDirection = true;
        loadAnimations();
        loadAudio();
    }
    @Override
    protected void loadAnimations() {
        atlas = new TextureAtlas(Gdx.files.internal("atlas/IceCrab/ice_crab.atlas"));

        // Create animations for different states
        animationMap.put(EnemyState.SPAWNING, new Animation<>(0.15f, atlas.findRegions("spawn"), Animation.PlayMode.NORMAL));
        animationMap.put(EnemyState.ATTACKING, new Animation<>(0.1f, atlas.findRegions("attack"), Animation.PlayMode.NORMAL));
        animationMap.put(EnemyState.DEAD, new Animation<>(0.10f, atlas.findRegions("death"), Animation.PlayMode.NORMAL));
        animationMap.put(EnemyState.IDLE, new Animation<>(0.15f, atlas.findRegions("idle"), Animation.PlayMode.LOOP));

        currentState = EnemyState.SPAWNING;
    }

    @Override
    public void update(float delta) {
        // Update animation time
        stateTime += delta;

        Animation<TextureRegion> currentAnimation = animationMap.get(currentState);
        switch (currentState) {
            //dont handle the animation transition here, let controller handle it.

            case SPAWNING:
                if (animationMap.get(EnemyState.SPAWNING).isAnimationFinished(stateTime)) {

                }
                break;
            case IDLE:
                //do nothings
                break;
            case ATTACKING:
                // When attack animation is done, go back to IDLE
                if (animationMap.get(EnemyState.ATTACKING).isAnimationFinished(stateTime)) {
                }
                break;
            case DEAD:
                // Handle death animation completion if needed
                if (animationMap.get(EnemyState.DEAD).isAnimationFinished(stateTime)) {

                }
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
        }
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
    public void setState(EnemyState state){
        super.setState(state);
        stateTime = 0;
    }

    @Override
    public boolean isDoneAttacking(){
        return isdoneAttack;
    }


    @Override
    public void updateState(float delta) {
        super.updateState(delta);
    }

    @Override
    protected void loadAudio() {
        super.attackSound = attackSound;
        spawnSound.play(); // Play spawn sound when the enemy is created

    }

    @Override
    public void resetState() {
        super.resetState();
        isdoneAttack = false; // Reset the attack state
    }

    @Override
    public void dispose() {
        attackSound.dispose();
        spawnSound.dispose();
        super.dispose();
    }
}
