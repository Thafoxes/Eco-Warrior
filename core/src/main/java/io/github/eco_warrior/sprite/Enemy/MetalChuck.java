package io.github.eco_warrior.sprite.Enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.Enemies;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;

public class MetalChuck extends Enemies {

    //sound effects
    private final Sound attackSound = Gdx.audio.newSound(Gdx.files.internal("sound_effects/drill.mp3"));

    public MetalChuck(Vector2 position, float scale) {
        super("atlas/metal_chuck/MetalChuck.atlas",
            "move",
            4, //this is for moving frameCount only, later in the load Animation it will run through all the frames
            position,
            scale);
        originalPos = position;
        currentState = EnemyState.MOVING;
        previousState = EnemyState.MOVING;

        stateTime = 0f;
        isFromRightDirection = true;

        loadAnimations();
        loadAudio();
    }

    /**
     * This is normally where you add sound, animation, state of it.
     * @param position
     */
    public MetalChuck(Vector2 position) {
        this(position, .2f);
    }

    @Override
    protected void loadAnimations() {
        // Load animations directly here
        atlas = new TextureAtlas(Gdx.files.internal("atlas/metal_chuck/MetalChuck.atlas"));

        // Create animations for different states
        animationMap.put(EnemyState.MOVING, new Animation<>(0.3f, atlas.findRegions("move"), Animation.PlayMode.LOOP));
        animationMap.put(EnemyState.ATTACKING, new Animation<>(0.3f, atlas.findRegions("attack"), Animation.PlayMode.NORMAL));
        animationMap.put(EnemyState.DEAD, new Animation<>(0.2f, atlas.findRegions("death"), Animation.PlayMode.NORMAL));
        animationMap.put(EnemyState.IDLE, new Animation<>(0.3f, atlas.findRegions("idle"), Animation.PlayMode.LOOP));

        currentState = EnemyState.MOVING;

    }

    @Override
    protected void loadAudio() {
        super.attackSound = attackSound;
    }


}

