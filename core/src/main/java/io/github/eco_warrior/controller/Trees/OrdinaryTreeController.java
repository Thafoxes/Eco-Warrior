package io.github.eco_warrior.controller.Trees;

import io.github.eco_warrior.sprite.gardening_equipments.WateringCan;
import io.github.eco_warrior.sprite.tree_variant.OrdinaryTree;
import io.github.eco_warrior.sprite.tree_variant.VoltaicTree;

public class OrdinaryTreeController extends TreeController<OrdinaryTree> {

    public OrdinaryTreeController(OrdinaryTree tree, WateringCan wateringCan) {

        super(tree, wateringCan);
    }
}
