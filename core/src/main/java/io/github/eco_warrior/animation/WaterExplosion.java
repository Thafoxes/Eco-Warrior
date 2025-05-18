package io.github.eco_warrior.animation;

import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class WaterExplosion extends Sprite {
    private static final String ATLAS_PATH = "effects/blue_explosion.atlas";
    private static final String REGION_NAME = "Explosion";
    private static final float ANIMATION_DURATION = 0.6f;

    private Animation<TextureRegion> explosionAnimation;
    private float stateTime = 0f;
    private boolean isFinished = false;
    private float scale = 1.0f;

    public WaterExplosion(Vector2 position, float scale) {
        super();
        this.scale = scale;

        TextureAtlas atlas = new TextureAtlas(ATLAS_PATH);

        Array<TextureAtlas.AtlasRegion> frames = atlas.findRegions(REGION_NAME);

        //create animation - non-looping
        explosionAnimation = new Animation<>(ANIMATION_DURATION / frames.size, frames, Animation.PlayMode.NORMAL);

        TextureRegion firstFrame = explosionAnimation.getKeyFrame(0);
        setRegion(firstFrame);
        setSize(firstFrame.getRegionWidth() * scale, firstFrame.getRegionHeight() * scale);
        setPosition(position.x - getWidth()/2, position.y - getHeight()/2);
        setOrigin(getWidth()/2, getHeight()/2);


    }

    /**
     * Creates a new explosion animation at the specified position with default scale
     *
     * @param position The position where the explosion should appear
     */
    public WaterExplosion(Vector2 position) {
        this(position, 1.0f);
    }

    /**
     * Update the explosion animation
     *
     * @param delta Time elapsed since last update
     */
    public void update(float delta) {
        if(!isFinished){
            stateTime += delta;

            TextureRegion currentFrame = explosionAnimation.getKeyFrame(stateTime);
            setRegion(currentFrame);

            if(explosionAnimation.isAnimationFinished(stateTime)){
                isFinished = true;
            }
        }
    }

    /**
     * Draws the current frame of the explosion animation
     *
     * @param batch Sprite batch for drawing, I'm expecting this to be a SpriteBatch. check it later when it causes error
     */
    public void draw(Batch batch){
        if(!isFinished){
            super.draw(batch);
        }
    }

    /**
     * Checks if the explosion animation has completed
     *
     * @return true if the animation is finished
     */
    public boolean isFinished() {
        return isFinished;
    }

    public void reset(Vector2 position) {
        stateTime = 0f;
        isFinished = false;
        setPosition(position.x - getWidth()/2, position.y - getHeight()/2);
    }

    public void dispose() {

    }



}
