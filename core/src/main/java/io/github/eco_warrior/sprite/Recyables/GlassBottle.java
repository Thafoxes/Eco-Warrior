package io.github.eco_warrior.sprite.Recyables;

import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.gameSprite;
import io.github.eco_warrior.enums.ERecycleMap;

public class GlassBottle extends Recyclables {
    private ERecycleMap categoryPile = ERecycleMap.glass_bottle;
    private float speed = 150f;

    public GlassBottle(Vector2 position) {
        super(
            position,
            "glass_bottle"
            );
    }

//    public GlassBottle(Vector2 position, String regionName) {
//        super(
//            "atlas/recyclables/trash.atlas",
//            regionName,
//            position,
//            0.5f
//        );
//    }



    //move from right to left
    @Override
    public void update(float delta){
        getSprite().setX( getSprite().getX() - speed * delta);
        getCollisionRect().setX(getSprite().getX() );
    }


    public ERecycleMap getCategoryPile() {
        return categoryPile;
    }


}
