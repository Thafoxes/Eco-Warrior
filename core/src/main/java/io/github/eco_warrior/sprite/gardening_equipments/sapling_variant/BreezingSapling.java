package io.github.eco_warrior.sprite.gardening_equipments.sapling_variant;

import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.tool;

public class BreezingSapling extends tool {

    public BreezingSapling(Vector2 position, float scale) {
        super(
            "atlas/saplings/saplings.atlas",
            "breezing_sapling",
            position,
            scale,
            null);
    }

}
