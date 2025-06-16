package io.github.eco_warrior.sprite.tree_healths;

import io.github.eco_warrior.entity.BaseTreeHealth;
import io.github.eco_warrior.sprite.tree_variant.OrdinaryTree;

public class OrdinaryTreeHealth extends BaseTreeHealth {


    public OrdinaryTreeHealth(OrdinaryTree ordinaryTree) {
        super("atlas/tree_health/tree_health.atlas",
            "HP",
            ordinaryTree);

    }


}
