package io.github.eco_warrior.sprite.Recyables;

import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.enums.ERecycleMap;

public class Newspaper extends Recyclables {
    private ERecycleMap categoryPile = ERecycleMap.newspaper;
    private float speed = -50f;

    public Newspaper(Vector2 position) {
        super(position,"newspaper");

    }

    @Override
    public ERecycleMap getCategoryPile() {
        return categoryPile;
    }

}
