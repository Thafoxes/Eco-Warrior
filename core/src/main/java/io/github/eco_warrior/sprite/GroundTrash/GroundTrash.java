package io.github.eco_warrior.sprite.GroundTrash;

import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.GameSprite;
import io.github.eco_warrior.enums.ERecycleMap;

public class GroundTrash extends GameSprite {
    private ERecycleMap categoryPile = ERecycleMap.glass_bottle;


    public GroundTrash(Vector2 position, String regionName) {
        super(
            "atlas/recyclables/trash.atlas",
            regionName,
            position,
            0.2f
        );
    }

    public ERecycleMap getCategoryPile() {
        return categoryPile;
    }
}
