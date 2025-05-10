package io.github.eco_warrior.sprite;

import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.gameSprite;
import io.github.eco_warrior.enums.ERecycleMap;

public class PlasticBottle extends TrashPile {
    private ERecycleMap categoryPile = ERecycleMap.plastic_bottle;
    private float speed = -50f;

    public PlasticBottle(Vector2 position) {
        super(position,"plastic_bottle");
    }


    @Override
    public ERecycleMap getCategoryPile() {
        return categoryPile;
    }
}
