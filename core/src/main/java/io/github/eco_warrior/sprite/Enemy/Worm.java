package io.github.eco_warrior.sprite.Enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import io.github.eco_warrior.entity.Enemies;

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
        isRightDirection = true;

        loadAnimations();
        loadAudio();
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
        animationMap.put(EnemyState.MOVING, new Animation<>(0.2f, atlas.findRegions("moving"), Animation.PlayMode.LOOP));
        animationMap.put(EnemyState.ATTACKING, new Animation<>(0.2f, atlas.findRegions("attack"), Animation.PlayMode.NORMAL));
        animationMap.put(EnemyState.DEAD, new Animation<>(0.1f, atlas.findRegions("death"), Animation.PlayMode.NORMAL));
        animationMap.put(EnemyState.IDLE, new Animation<>(0.1f, atlas.findRegions("idle"), Animation.PlayMode.LOOP));

        currentState = EnemyState.MOVING;

    }

    @Override
    protected void loadAudio() {
        super.attackSound = attackSound;
    }

    @Override
    public void updateState(float delta) {
        super.updateState(delta);
    }

}
