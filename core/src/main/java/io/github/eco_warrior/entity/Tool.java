package io.github.eco_warrior.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Tool extends GameSprite {

    public Tool(String atlasPath, String regionName, Vector2 position, float scale, String soundEffectPath) {
        super(atlasPath,
            regionName,
            position,
            scale,
            soundEffectPath
            );

    }


    // Constructor for tools with multiple frames and custom frame count
    public Tool(String atlasPath, String regionName, int frameCount, Vector2 position, float scale) {
        super(atlasPath,
            regionName,
            frameCount,
            position,
            scale);
    }

    // Constructor for tools with a single frame and no Audio
    public Tool(String atlasPath, String regionName, Vector2 position, float scale) {
        super(atlasPath,
            regionName,
            position,
            scale);
    }


    public void debug(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.RED); // Set color for debug rectangle
        Rectangle collisionRect = getCollisionRect();
        shapeRenderer.rect(collisionRect.x, collisionRect.y, collisionRect.width, collisionRect.height);

    }
}
