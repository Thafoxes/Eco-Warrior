package io.github.eco_warrior.sprite.gardening_equipments.sapling_variant;

import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.controller.Sapling.BaseSaplingController;
import io.github.eco_warrior.enums.SaplingType;

public class VoltaicSapling extends BaseSaplingController {

    public VoltaicSapling(Vector2 position, float scale) {
        super(
            position,
            "voltaic_sapling",
            scale,
            SaplingType.VOLTAIC);
    }

}
