package io.github.eco_warrior.sprite.gardening_equipments;

import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.tool;

public class Fertilizer extends tool {

    public Fertilizer(Vector2 position, float scale) {
        super(
            "atlas/gardening_equipments/equipments.atlas",
            "fertilizer",
            position,
            scale,
            null);
    }

}
