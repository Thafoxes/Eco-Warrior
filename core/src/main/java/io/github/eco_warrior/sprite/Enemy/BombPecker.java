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
        isRightDirection = true;

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
        animationMap.put(EnemyState.ATTACKING, new Animation<>(0.1f, atlas.findRegions("attack"), Animation.PlayMode.NORMAL));
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
    public void attack(){
        if(currentState != EnemyState.ATTACKING){
            currentState = EnemyState.ATTACKING;
            stateTime = 0;
            if (attackSound != null) {
                attackSound.play(0.8f);
            }
        }

    }

    @Override
    public void update(float delta) {
        if (!isDead()) {
            stateTime += delta;
        }

        Animation<TextureRegion> currentAnimation = animationMap.get(currentState);

        if(currentState == EnemyState.ATTACKING){

            if(currentAnimation.isAnimationFinished(stateTime)){
                currentState = EnemyState.DEAD;
                previousState = EnemyState.ATTACKING;
                stateTime = 0;
            }
        }
        if(currentState == EnemyState.DEAD){
            if (currentAnimation.isAnimationFinished(stateTime)) {
                stateTime = 0;
            }
        }
        updateAnimationFrame(currentAnimation);
        super.update(delta);

    }



}
