package io.github.eco_warrior.sprite.gardening_equipments.sapling_variant;

import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.controller.Sapling.BaseSaplingController;
import io.github.eco_warrior.enums.SaplingType;

public class BlazingSapling extends BaseSaplingController {

    public BlazingSapling(Vector2 position, float scale) {
        super(position,
            "blazing_sapling" ,
            scale,
            SaplingType.BLAZING);
    }

}
