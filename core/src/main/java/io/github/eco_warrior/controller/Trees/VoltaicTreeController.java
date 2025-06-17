package io.github.eco_warrior.controller.Trees;

import io.github.eco_warrior.controller.Sapling.BaseSaplingController;
import io.github.eco_warrior.enums.SaplingType;
import io.github.eco_warrior.sprite.gardening_equipments.WateringCan;
import io.github.eco_warrior.sprite.tree_healths.VoltaicTreeHealth;
import io.github.eco_warrior.sprite.tree_variant.VoltaicTree;

public class VoltaicTreeController extends TreeController<VoltaicTree> {

    public VoltaicTreeController(VoltaicTree tree, WateringCan wateringCan) {
        super(tree, wateringCan, new VoltaicTreeHealth(tree));
    }

    @Override
    protected boolean canPlantSapling(BaseSaplingController sapling) {
        return super.canPlantSapling(sapling) &&
            sapling.getSaplingType() == SaplingType.VOLTAIC;
    }
}
