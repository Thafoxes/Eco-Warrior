package io.github.eco_warrior.sprite.tree_healths;

import io.github.eco_warrior.entity.TreeHealth;
import io.github.eco_warrior.entity.Trees;

public class OrdinaryTreeHealth extends TreeHealth {

    public OrdinaryTreeHealth(Trees tree) {
        super("atlas/tree_health/tree_health.atlas",
            "HP",
            tree.adjustedPosition,
            tree);
    }
}
