package io.github.eco_warrior.sprite;

import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.GameSprite;

public class WaterFountain extends GameSprite {

    public WaterFountain(Vector2 position, float scale) {
        super("atlas/lake_hitbox/lake_hitbox.atlas",
            "lake_hitbox",
            position,
            scale);
    }
}
