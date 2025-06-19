package io.github.eco_warrior.controller.Trees;

import io.github.eco_warrior.controller.Sapling.BaseSaplingController;
import io.github.eco_warrior.enums.SaplingType;
import io.github.eco_warrior.enums.TreeType;
import io.github.eco_warrior.sprite.gardening_equipments.WateringCan;
import io.github.eco_warrior.sprite.tree_healths.OrdinaryTreeHealth;
import io.github.eco_warrior.sprite.tree_variant.OrdinaryTree;
import io.github.eco_warrior.sprite.tree_variant.VoltaicTree;

public class OrdinaryTreeController extends TreeController<OrdinaryTree> {

    public OrdinaryTreeController(OrdinaryTree tree, WateringCan wateringCan) {

        super(tree, wateringCan, new OrdinaryTreeHealth(tree), TreeType.ORDINARY);
    }

    @Override
    protected boolean canPlantSapling(BaseSaplingController sapling) {
        return super.canPlantSapling(sapling) &&
            sapling.getSaplingType() == SaplingType.ORDINARY;
    }
}
