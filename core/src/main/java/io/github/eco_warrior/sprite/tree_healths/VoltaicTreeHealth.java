package io.github.eco_warrior.sprite.tree_healths;

import io.github.eco_warrior.entity.TreeHealth;
import io.github.eco_warrior.entity.Trees;

public class VoltaicTreeHealth extends TreeHealth {

    public VoltaicTreeHealth(Trees tree) {
        super("atlas/tree_health/voltaic_tree_health.atlas",
            "VHP",
            tree.adjustedPosition,
            tree);
    }
}
