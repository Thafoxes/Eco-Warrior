package io.github.eco_warrior.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class AnimatedSprite {
    private Animation<TextureRegion> animation;
    private float stateTime;
    private Rectangle collisionRect;
    private Vector2 position;
    private float scale = 1f;

    public AnimatedSprite(String atlasPath, String baseName, Vector2 position, float scale, float frameDuration) {
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(atlasPath));
        Array<TextureAtlas.AtlasRegion> frames = atlas.findRegions(baseName);

        if(frames.size == 0){
            throw new NullPointerException("frames is null" + baseName);
        }

        this.animation = new Animation<>(frameDuration);
        this.position = position;
        this.scale = scale;

        TextureRegion firstFrame = animation.getKeyFrame(0);
        float width = firstFrame.getRegionWidth() * scale;
        float height = firstFrame.getRegionHeight() * scale;

        this.collisionRect = new Rectangle(position.x, position.y, width, height);
    }

    public void update(float delta){
        stateTime += delta;
    }

    public void draw(SpriteBatch batch){
        TextureRegion region = animation.getKeyFrame(stateTime, true);
        batch.draw(region, position.x, position.y, region.getRegionWidth() * scale, region.getRegionHeight() * scale);
    }

    public void reset(){
        this.stateTime = 0f;
    }

    public Rectangle getCollisionRect() {
        return this.collisionRect;
    }

    public boolean isAnimationFinished(){
        return animation.isAnimationFinished(stateTime);
    }

    public void setPosition(Vector2 newPosition){
        this.position = newPosition;
        this.collisionRect.setPosition(newPosition);
    }

    public void setAnimation(Animation<TextureRegion> animation){
        this.animation = animation;
        this.reset();
    }
}
