package io.github.eco_warrior.sprite.Enemy;

import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.gameSprite;

public class Worm extends gameSprite {
    private static Vector2 originalPos;

    public enum WormState {
        ALIVE,
        DEAD
    }

    private final float speed = 150f; // Speed of the worm

    public Worm(Vector2 position, float scale) {
        super("atlas/mobs/worm.atlas",
            "worm",
            14,
            position,
            scale);
        originalPos = position;
    }

    public Worm(Vector2 position) {
        this(position, 0.2f);
    }

    public void reset() {
        getSprite().setPosition(originalPos.x, originalPos.y);
        getCollisionRect().setPosition(originalPos.x, originalPos.y);
        System.out.println("Worm reset to original position: " + originalPos);
    }
    //move from right to left
    @Override
    public void update(float delta){
        getSprite().setX(getSprite().getX() - speed * delta);
        getCollisionRect().setX(getSprite().getX());

    }
}
