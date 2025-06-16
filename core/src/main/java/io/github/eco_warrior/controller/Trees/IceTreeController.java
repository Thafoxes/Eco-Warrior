package io.github.eco_warrior.controller.Trees;

import io.github.eco_warrior.controller.Sapling.BaseSaplingController;
import io.github.eco_warrior.enums.SaplingType;
import io.github.eco_warrior.sprite.gardening_equipments.WateringCan;
import io.github.eco_warrior.sprite.tree_variant.IceTree;
import io.github.eco_warrior.sprite.tree_variant.OrdinaryTree;

public class IceTreeController extends TreeController<IceTree> {

    public IceTreeController(IceTree tree, WateringCan wateringCan) {

        super(tree, wateringCan);
    }

    @Override
    protected boolean canPlantSapling(BaseSaplingController sapling) {
        return super.canPlantSapling(sapling) &&
            sapling.getSaplingType() == SaplingType.ICE;
    }
}
