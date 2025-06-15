package io.github.eco_warrior.sprite.tree_healths;

import io.github.eco_warrior.entity.TreeHealth;
import io.github.eco_warrior.entity.Trees;

public class IceTreeHealth extends TreeHealth {

    public IceTreeHealth(Trees tree) {
        super("atlas/tree_health/ice_tree_health.atlas",
            "IHP",
            tree.adjustedPosition,
            tree);
    }
}
