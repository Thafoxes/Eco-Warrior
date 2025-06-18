package io.github.eco_warrior.sprite.GroundTrash;

import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.enums.ERecycleMap;

public class GroundPaper extends GroundTrash {
    private ERecycleMap categoryPile = ERecycleMap.glass_bottle;

    public GroundPaper(Vector2 position) {
        super(
            position,
            "newspaper"
        );
    }

    public ERecycleMap getCategoryPile() {
        return categoryPile;
    }

}
