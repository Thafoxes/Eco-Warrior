package io.github.eco_warrior.sprite.Recyables;

import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.enums.ERecycleMap;

public class TinCans extends Recyclables {
    private ERecycleMap categoryPile = ERecycleMap.tin_cans;
    private float speed = -50f;

    public TinCans(Vector2 position) {
        super(position,"metal_can");

    }


    @Override
    public ERecycleMap getCategoryPile() {
        return categoryPile;
    }

}
