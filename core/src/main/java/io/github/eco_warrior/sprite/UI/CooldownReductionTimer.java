package io.github.eco_warrior.sprite.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import io.github.eco_warrior.entity.GameSprite;

import java.util.HashMap;
import java.util.Map;

public class CooldownReductionTimer extends GameSprite {

    public enum TimerStage {
        STATIC,
        RUN
    }

    protected TimerStage timerStage = TimerStage.STATIC;
    protected TextureAtlas atlas;
    protected final Map<TimerStage, Animation<TextureRegion>> animationMap = new HashMap<>();
    protected float stateTime = 0;
    protected float clockAnimationTime = 1.6f;
    protected Timer.Task runTask;
    public static boolean isAnimationPlayed = false;



    protected Sound speedUpSound;

    public CooldownReductionTimer(Vector2 position, float scale){
        super("atlas/cooldown_reduction_timer/clock.atlas",
            "static",
            1,
            position,
            scale);

        speedUpSound = Gdx.audio.newSound(Gdx.files.internal("sound_effects/CooldownReduction.mp3"));

        try{
            loadAnimation();
            loadAudio();
        } catch (RuntimeException e) {
            Gdx.app.error("Timer", "Failed to load animations: " + e.getMessage());
            e.printStackTrace(); // Rethrow the exception to indicate failure
        }
    }

    /**
     * Load animations for the button stages.
     * This method should be implemented by subclasses to define specific animations.
     * It is called in the constructor of the PurchaseButton class.
     *
     * @throws RuntimeException if there is an error loading animations.
     */

    protected void loadAnimation() {
        // Load animations directly here
        atlas = new TextureAtlas(Gdx.files.internal("atlas/cooldown_reduction_timer/clock.atlas"));

        animationMap.put(TimerStage.STATIC, new Animation<>(0f, atlas.findRegions("static"), Animation.PlayMode.NORMAL));
        animationMap.put(TimerStage.RUN, new Animation<>(.4f, atlas.findRegions("run"), Animation.PlayMode.NORMAL));
    }

    protected void loadAudio() throws RuntimeException {
        this.speedUpSound = Gdx.audio.newSound(Gdx.files.internal("sound_effects/CooldownReduction.mp3"));
    }

    @Override
    public void update(float delta) {
        stateTime += delta;
        if (animationMap.containsKey(timerStage)) {
            Animation<TextureRegion> animation = animationMap.get(timerStage);
            TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
            getSprite().setRegion(currentFrame);
        }
        super.update(delta);
    }

    /**
     * Set the stage of the tree.
     * This method will reset the state time and update the sprite region based on the new stage.
     *
     * @param stage The new stage of the tree.
     */
    protected void setStage(TimerStage stage) {
        timerStage = stage;
        stateTime = 0;
        if (animationMap.containsKey(stage)) {
            Animation<TextureRegion> animation = animationMap.get(stage);
            getSprite().setRegion(animation.getKeyFrame(0));
        }
    }

    public TimerStage getStage(){
        return this.timerStage;
    }

    // Clock run animation
    public void clockRun() {
        if (timerStage == TimerStage.STATIC) {
            speedUpSound.play(0.5f);
            setStage(TimerStage.RUN);
            isAnimationPlayed = true;
            // Schedule to revert to STATIC after 1.6s
            runTask = Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    setStage(TimerStage.STATIC);
                    isAnimationPlayed = false;
                }
            }, clockAnimationTime);
        }
    }

    public void debug(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.RED); // Set color for debug rectangle
        Rectangle collisionRect = getCollisionRect();
        shapeRenderer.rect(collisionRect.x, collisionRect.y, collisionRect.width, collisionRect.height);

    }

    @Override
    public void dispose() {
        super.dispose();
        speedUpSound.dispose();
        if (atlas != null) {
            atlas.dispose();
        }
    }

}
