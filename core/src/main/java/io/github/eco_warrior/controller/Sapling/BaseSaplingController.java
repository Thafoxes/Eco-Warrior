package io.github.eco_warrior.controller.Sapling;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.GameSprite;
import io.github.eco_warrior.entity.Tool;
import io.github.eco_warrior.enums.SaplingType;

public class BaseSaplingController extends Tool {


    private SaplingType saplingType;

    /**
     * Constructor for BaseSapling.
     *  If you want to create your own method of sampling, you can extend this class. Dont do it from tool.
     * @param position The position of the sapling in the game world.
     * @param scale The scale factor for the sapling sprite.
     */
    public BaseSaplingController(Vector2 position, String regionName, float scale, SaplingType saplingType) {
        super(
            "atlas/saplings/saplings.atlas",
            regionName,
            position,
            scale);
        this.saplingType = saplingType;


    }

    @Override
    public void update(float delta) {
        super.update(delta);

        // Update logic specific to sapling can be added here
    }

    @Override
    public void draw(SpriteBatch batch) {
        Sprite sprite = getSprite();
        if (sprite != null) {
            //TODO - FIX ALIGNMENT ISSUE
            float centerX = sprite.getX();
            float centerY = sprite.getY();
            sprite.setPosition(centerX, centerY);
            sprite.setScale(getScale());
            sprite.draw(batch);
        }
    }

    public SaplingType getSaplingType() {
        return saplingType;
    }


    public Rectangle getCollisionRect() {
        Sprite sprite = getSprite();
        return new Rectangle(
            sprite.getX(),
            sprite.getY(),
            sprite.getWidth() * getScale(),
            sprite.getHeight() * getScale()
        );
    }

    @Override
    public void debug(ShapeRenderer shapeRenderer) {
        // Draw sprite bounds in green
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(
            getSprite().getX() - 10f,
              getSprite().getY() - 10f,
            getSprite().getWidth() + 20f,
            getSprite().getHeight() + 20f
        );

        // Draw collision rectangle in yellow
        shapeRenderer.setColor(Color.YELLOW);
        Rectangle collision = getCollisionRect();
        shapeRenderer.rect(
            collision.x,
            collision.y,
            collision.width,
            collision.height
        );
    }


}
