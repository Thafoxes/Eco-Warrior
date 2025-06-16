package io.github.eco_warrior.sprite.tree_healths;

import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.BaseTreeHealth;
import io.github.eco_warrior.entity.Trees;

public class BlazingTreeHealth extends BaseTreeHealth {


    public BlazingTreeHealth(Trees blazingTree) {
        super("atlas/tree_health/blazing_tree_health.atlas",
            "BzHP",
            blazingTree);

    }


}
