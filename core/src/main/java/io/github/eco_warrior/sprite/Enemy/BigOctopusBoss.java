package io.github.eco_warrior.sprite.Enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.Enemies;

public class BigOctopusBoss extends Enemies {

    public BigOctopusBoss(Vector2 position) {
        super(
            "atlas/big_octopus/big_octopus.atlas",
            "idle",
            1,
            position,
            0.4f
        );

        loadAnimations();

    }
    @Override
    protected void loadAnimations() {
        atlas = new TextureAtlas(Gdx.files.internal("atlas/big_octopus/big_octopus.atlas"));

        // Create animations for different states
        animationMap.put(EnemyState.SPAWNING, new Animation<>(0.3f, atlas.findRegions("emerge"), Animation.PlayMode.NORMAL));
        animationMap.put(EnemyState.IDLE, new Animation<>(0.3f, atlas.findRegions("idle"), Animation.PlayMode.LOOP));

        currentState = EnemyState.SPAWNING;
    }

    @Override
    protected void loadAudio() {

    }

    @Override
    public void update(float delta) {
        // Update animation time
        stateTime += delta;

        Animation<TextureRegion> currentAnimation = animationMap.get(currentState);
        switch(currentState){
            case SPAWNING:
                break;
            case IDLE:

                break;
            default:
                System.err.println("Unhandled animation state: " + currentState);
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
    public void setState(EnemyState state){
        super.setState(state);
        stateTime = 0;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (atlas != null) {
            atlas.dispose();
        }
    }
}
