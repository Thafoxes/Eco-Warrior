package io.github.eco_warrior.sprite;

import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.gameSprite;
import io.github.eco_warrior.enums.ERecycleMap;

public class TrashPile extends gameSprite {
    private ERecycleMap categoryPile = ERecycleMap.trash_pile;
    private float speed = -50f;

    public TrashPile(Vector2 position) {
        super(
            "sprite/recyclables/recyclables.atlas",
            "trash_pile",
            position,
            3f
            );
    }

    //move from right to left
    @Override
    public void update(float delta){
        getSprite().setX(getSprite().getX() + this.speed * delta);
        getCollisionRect().setX(getSprite().getX());
    }


}
