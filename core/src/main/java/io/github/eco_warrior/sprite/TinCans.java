package io.github.eco_warrior.sprite;

import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.gameSprite;
import io.github.eco_warrior.enums.ERecycleMap;

public class TinCans extends TrashPile {
    private ERecycleMap categoryPile = ERecycleMap.tin_cans;
    private float speed = -50f;

    public TinCans(Vector2 position) {
        super(position,"tin_cans");

    }


    @Override
    public ERecycleMap getCategoryPile() {
        return categoryPile;
    }

}
