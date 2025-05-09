package io.github.eco_warrior.sprite;

import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.gameSprite;
import io.github.eco_warrior.enums.ERecycleMap;

public class Newspaper extends TrashPile {
    private ERecycleMap categoryPile = ERecycleMap.trash_pile;
    private float speed = -50f;

    public Newspaper(Vector2 position) {
        super(position,"newspaper");
    }



}
