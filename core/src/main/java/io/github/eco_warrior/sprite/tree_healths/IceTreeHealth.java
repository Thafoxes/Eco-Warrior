package io.github.eco_warrior.sprite.tree_healths;

import io.github.eco_warrior.entity.BaseTreeHealth;
import io.github.eco_warrior.sprite.tree_variant.IceTree;

public class IceTreeHealth extends BaseTreeHealth {


    public IceTreeHealth(IceTree iceTree) {
        super("atlas/tree_health/ice_tree_health.atlas",
            "IHP",
            iceTree);

    }


}
