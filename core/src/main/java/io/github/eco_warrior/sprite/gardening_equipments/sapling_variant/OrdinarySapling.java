package io.github.eco_warrior.sprite.gardening_equipments.sapling_variant;

import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.tool;

public class OrdinarySapling extends tool {

    public OrdinarySapling(Vector2 position, float scale) {
        super(
            "atlas/saplings/saplings.atlas",
            "sapling",
            position,
            scale,
            null);
    }

}
