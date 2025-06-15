package io.github.eco_warrior.sprite.tree_healths;

import io.github.eco_warrior.entity.TreeHealth;
import io.github.eco_warrior.entity.Trees;

public class BlazingTreeHealth extends TreeHealth {

    public BlazingTreeHealth(Trees tree) {
        super("atlas/tree_health/blazing_tree_health.atlas",
            "BzHP",
            tree.adjustedPosition,
            tree);
    }
}
