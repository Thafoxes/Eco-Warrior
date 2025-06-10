package io.github.eco_warrior.sprite.Enemy;

import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.gameSprite;

public class Worm extends gameSprite {
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
    }
}
