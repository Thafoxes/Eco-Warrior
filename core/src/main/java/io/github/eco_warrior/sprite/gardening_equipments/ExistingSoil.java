package io.github.eco_warrior.sprite.gardening_equipments;

import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.Tool;

public class ExistingSoil extends Tool {

    public ExistingSoil(Vector2 position, float scale) {
        super(
            "atlas/gardening_equipments/equipments.atlas",
            "existing_soil",
            position,
            scale);
    }

}
