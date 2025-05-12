package io.github.eco_warrior.sprite.tools;

import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.tool;

public class WaterSpray extends tool {

    public WaterSpray(Vector2 position, float scale) {
        super(
            "atlas/tools/tools.atlas",
            "water_spray_x33",
            position,
            scale,
            null);
    }

}
