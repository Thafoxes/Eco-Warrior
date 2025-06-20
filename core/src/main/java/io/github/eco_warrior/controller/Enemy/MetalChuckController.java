package io.github.eco_warrior.controller.Enemy;

import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.enums.EnemyType;
import io.github.eco_warrior.enums.TreeType;
import io.github.eco_warrior.sprite.Enemy.MetalChuck;

import java.util.ArrayList;

public class MetalChuckController extends EnemyController{

    public MetalChuckController(Vector2 position) {
        super(new MetalChuck(position), EnemyType.METAL_CHUCK);
    }

//    @Override
//    void initializeTreeTypes() {
//        ArrayList<TreeType> treeTypes = new ArrayList<>();
//        treeTypes.add(TreeType.ORDINARY);
//        treeTypes.add(TreeType.VOLTAIC);
//
//        this.treeTypes = treeTypes;
//    }

    @Override
    public void update(float delta) {
        super.update(delta);
        // Add any MetalChuck specific update logic here if needed
    }

    @Override
    public void attack() {
        super.attack();
        // Implement MetalChuck-specific attack logic here if needed
    }
}
