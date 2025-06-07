package io.github.eco_warrior.sprite.Recyables;

import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.enums.ERecycleMap;

public class PlasticBottle extends Recyclables {
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
