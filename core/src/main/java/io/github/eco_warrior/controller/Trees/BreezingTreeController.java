package io.github.eco_warrior.controller.Trees;

import io.github.eco_warrior.controller.Sapling.BaseSaplingController;
import io.github.eco_warrior.enums.SaplingType;
import io.github.eco_warrior.sprite.gardening_equipments.WateringCan;
import io.github.eco_warrior.sprite.gardening_equipments.sapling_variant.BreezingSapling;
import io.github.eco_warrior.sprite.tree_variant.BreezingTree;
import io.github.eco_warrior.sprite.tree_variant.IceTree;

public class BreezingTreeController extends TreeController<BreezingTree> {

    public BreezingTreeController(BreezingTree tree, WateringCan wateringCan) {

        super(tree, wateringCan);
    }

    @Override
    protected boolean canPlantSapling(BaseSaplingController sapling) {
        return super.canPlantSapling(sapling) &&
            sapling.getSaplingType() == SaplingType.BREEZING;
    }

}
