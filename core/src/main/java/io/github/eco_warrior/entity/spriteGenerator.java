package io.github.eco_warrior.entity;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public abstract class spriteGenerator {

    protected Sprite sprite;
    protected Rectangle collisionRect;
    protected float scale = 1f;

    public abstract void draw(SpriteBatch batch);
    public abstract void drawDebug(ShapeRenderer shapeRenderer);
    public abstract void playCorrectSound();
    public Rectangle getCollisionRect() { return collisionRect; }
    public Sprite getSprite() { return sprite; }
}
