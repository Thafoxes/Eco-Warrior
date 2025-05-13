package io.github.eco_warrior.entity;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public abstract class spriteGenerator {

    protected Sprite sprite;
    protected Rectangle collisionRect;
    protected float scale = 1f;

    // Multi-frame support
    protected TextureRegion[] frames;
    protected int currentFrameIndex = 0;

    public abstract void draw(SpriteBatch batch);
    public abstract void drawDebug(ShapeRenderer shapeRenderer);
    public abstract void playCorrectSound();
    public Rectangle getCollisionRect() { return collisionRect; }
    public Sprite getSprite() { return sprite; }
}
