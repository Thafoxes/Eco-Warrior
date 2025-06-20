package io.github.eco_warrior.sprite.GroundTrash;

import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.enums.ERecycleMap;

public class GroundPlastic extends GroundTrash {
    private ERecycleMap categoryPile = ERecycleMap.glass_bottle;

    public GroundPlastic(Vector2 position) {
        super(
            position,
            "plastic_bottle"
        );
    }

    public ERecycleMap getCategoryPile() {
        return categoryPile;
    }

}
