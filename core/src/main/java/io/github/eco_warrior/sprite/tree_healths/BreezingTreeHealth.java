package io.github.eco_warrior.sprite.tree_healths;

import io.github.eco_warrior.entity.BaseTreeHealth;
import io.github.eco_warrior.sprite.tree_variant.BreezingTree;

public class BreezingTreeHealth extends BaseTreeHealth {


    public BreezingTreeHealth(BreezingTree breezingTree) {
        super("atlas/tree_health/breezing_tree_health.atlas",
            "BrHP",
            breezingTree);

    }

}
