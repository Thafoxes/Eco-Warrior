package io.github.eco_warrior.sprite.GroundTrash;

import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.enums.ERecycleMap;

public class GroundMetal extends GroundTrash {
    private ERecycleMap categoryPile = ERecycleMap.glass_bottle;

    public GroundMetal(Vector2 position) {
        super(
            position,
            "metal_can"
        );
    }

    public ERecycleMap getCategoryPile() {
        return categoryPile;
    }

}
