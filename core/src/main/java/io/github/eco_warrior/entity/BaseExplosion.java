package io.github.eco_warrior.entity;

import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class BaseExplosion extends Sprite {
        private float animationDuration = 0.6f;
        private String regionName;

        private Animation<TextureRegion> explosionAnimation;
        private float stateTime = 0f;
        private boolean isFinished = false;
        private float scale = 1.0f;


    protected BaseExplosion(String atlasPath, Vector2 position, float scale, String regionName, float animationDuration) {
        super();
        this.scale = scale;
        this.regionName = regionName;

        TextureAtlas atlas = new TextureAtlas(atlasPath);
        Array<TextureAtlas.AtlasRegion> frames = atlas.findRegions(regionName);
        explosionAnimation = new Animation<>(animationDuration / frames.size, frames, Animation.PlayMode.NORMAL);

        TextureRegion firstFrame = explosionAnimation.getKeyFrame(0);
        setRegion(firstFrame);
        setSize(firstFrame.getRegionWidth() * scale, firstFrame.getRegionHeight() * scale);
        setPosition(position.x - getWidth()/2, position.y - getHeight()/2);
        setOrigin(getWidth()/2, getHeight()/2);
    }

    protected BaseExplosion(String atlasPath, Vector2 position, float scale) {
        this(atlasPath, position, scale, "Explosion", 0.6f);
    }


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

    public void draw(Batch batch){
        if(!isFinished){
            super.draw(batch);
        }
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void reset(Vector2 position) {
        stateTime = 0f;
        isFinished = false;
        setPosition(position.x - getWidth()/2, position.y - getHeight()/2);
    }

    public void dispose() {
        // Add disposal logic if needed
    }

    private Object userObject;

    public void setUserObject(Object obj) {
        this.userObject = obj;
    }

    public Object getUserObject() {
        return this.userObject;
    }
}
