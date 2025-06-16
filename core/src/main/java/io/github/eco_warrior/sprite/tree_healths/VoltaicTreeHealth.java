package io.github.eco_warrior.sprite.tree_healths;

import io.github.eco_warrior.entity.BaseTreeHealth;
import io.github.eco_warrior.sprite.tree_variant.VoltaicTree;

public class VoltaicTreeHealth extends BaseTreeHealth {


    public VoltaicTreeHealth(VoltaicTree voltaicTree) {
        super("atlas/tree_health/voltaic_tree_health.atlas",
            "VHP",
            voltaicTree);

    }

}
