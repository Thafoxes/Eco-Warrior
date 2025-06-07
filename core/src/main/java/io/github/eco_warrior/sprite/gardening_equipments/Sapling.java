package io.github.eco_warrior.sprite.gardening_equipments;

import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.tool;

public class Sapling extends tool {

    public Sapling(Vector2 position, float scale) {
        super(
            "atlas/gardening_equipments/equipments.atlas",
            "sapling",
            position,
            scale,
            null);
    }

}
