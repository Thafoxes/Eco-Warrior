package io.github.eco_warrior.sprite.tree_healths;

import io.github.eco_warrior.entity.TreeHealth;
import io.github.eco_warrior.entity.Trees;

public class BreezingTreeHealth extends TreeHealth {

    public BreezingTreeHealth(Trees tree) {
        super("atlas/tree_health/breezing_tree_health.atlas",
            "BrHP",
            tree.adjustedPosition,
            tree);
    }
}
