package io.github.eco_warrior.controller;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.Tool;
import io.github.eco_warrior.enums.SaplingType;

public class FertilizerController extends Tool {

    public FertilizerController(Vector2 position, float scale) {
        super(
            "atlas/gardening_equipments/fertilizer_revamp.atlas",
            "fertilizer_revamp",
            position,
            scale);
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
            float centerX = sprite.getX();
            float centerY = sprite.getY();
            sprite.setPosition(centerX, centerY);
            sprite.draw(batch);
        }
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
